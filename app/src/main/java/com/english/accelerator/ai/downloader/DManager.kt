package com.english.accelerator.ai.downloader

import android.content.Context
import com.english.accelerator.ai.model.ModelConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 模型下载管理器
 *
 * 管理 Gemma 3n E2B-it 模型的下载
 */
class DManager(private val context: Context) {
    private val modelFile = File(context.filesDir, "models/gemma-3n-e2b-it-int4.litertlm")
    private val modelConfig = ModelConfig.getInstance()

    // Config.json 管理器 - 单一真相来源
    private val configManager = com.english.accelerator.ai.downloader.DConfig(context)

    // 从 Config.json 读取配置
    private val expectedModelSize: Long
        get() = configManager.getExpectedModelSize()

    // 下载引擎
    private val downloadEngine = DEngine()

    // 线路枚举
    enum class DownloadRoute {
        HUGGINGFACE,
        MODELSCOPE
    }

    private var selectedRoute: DownloadRoute = DownloadRoute.MODELSCOPE // 默认使用 ModelScope

    init {
        // 从配置中恢复下载线路
        val savedRoute = modelConfig.getDownloadRoute()
        if (savedRoute != null) {
            selectedRoute = try {
                DownloadRoute.valueOf(savedRoute)
            } catch (e: Exception) {
                DownloadRoute.MODELSCOPE
            }
        } else {
            // 如果没有保存的线路，使用 Config.json 中的默认线路
            val defaultRoute = configManager.getDefaultRoute()
            selectedRoute = try {
                DownloadRoute.valueOf(defaultRoute)
            } catch (e: Exception) {
                DownloadRoute.MODELSCOPE
            }
        }

        configManager.addDownloadLog("DManager initialized with route: $selectedRoute")
    }

    /**
     * 获取当前选择的下载地址
     */
    private fun getSelectedUrl(): String {
        val routes = configManager.getDownloadRoutes()
        return routes.find { it.name == selectedRoute.name }?.url ?: ""
    }

    /**
     * 切换下载线路
     */
    fun switchRoute() {
        selectedRoute = when (selectedRoute) {
            DownloadRoute.HUGGINGFACE -> DownloadRoute.MODELSCOPE
            DownloadRoute.MODELSCOPE -> DownloadRoute.HUGGINGFACE
        }
    }

    /**
     * 获取当前线路名称
     */
    fun getCurrentRouteName(): String = when (selectedRoute) {
        DownloadRoute.HUGGINGFACE -> "HuggingFace"
        DownloadRoute.MODELSCOPE -> "魔塔社区"
    }

    /**
     * 下载模型
     * @param onProgress 进度回调 (已下载字节, 总字节, 速度 bytes/sec)
     * @return 下载结果
     */
    suspend fun downloadModel(
        onProgress: (downloaded: Long, total: Long, speed: Long) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        // 检查模型是否已完整下载
        if (isModelComplete()) {
            configManager.addDownloadLog("Model already complete, skipping download")
            return@withContext Result.success(modelFile)
        }

        // 检查是否可以恢复下载
        val existingSize = if (modelFile.exists()) modelFile.length() else 0L
        if (existingSize > 0) {
            configManager.addResumeLog("Resuming download from $existingSize bytes")
        } else {
            configManager.addDownloadLog("Starting new download from ${selectedRoute.name}")
        }

        // 更新下载状态
        configManager.updateDownloadState(
            modelPath = modelFile.absolutePath,
            downloadedBytes = existingSize,
            totalBytes = expectedModelSize,
            isComplete = false,
            isPaused = false,
            downloadRoute = selectedRoute.name
        )

        // 使用当前选择的线路下载
        val result = downloadEngine.download(getSelectedUrl(), modelFile, onProgress)

        // 下载成功后验证完整性并保存配置
        if (result.isSuccess) {
            if (isModelComplete()) {
                modelConfig.markModelDownloaded(modelFile.absolutePath, modelFile.length())
                configManager.updateDownloadState(
                    modelPath = modelFile.absolutePath,
                    downloadedBytes = modelFile.length(),
                    totalBytes = expectedModelSize,
                    isComplete = true,
                    isPaused = false,
                    downloadRoute = selectedRoute.name
                )
                configManager.addDownloadLog("Download completed successfully: ${modelFile.length()} bytes")
            } else {
                val errorMsg = "下载完成但文件大小不匹配。预期: ${expectedModelSize / (1024 * 1024)}MB, 实际: ${modelFile.length() / (1024 * 1024)}MB"
                configManager.addErrorLog(errorMsg)
                return@withContext Result.failure(Exception(errorMsg))
            }
        } else {
            val errorMsg = result.exceptionOrNull()?.message ?: "Unknown download error"
            configManager.addErrorLog("Download failed: $errorMsg")
        }

        result
    }

    /**
     * 暂停下载
     */
    fun pauseDownload() {
        downloadEngine.pause()
    }

    /**
     * 恢复下载
     */
    fun resumeDownload() {
        downloadEngine.resume()
    }

    /**
     * 取消下载
     */
    fun cancelDownload() {
        downloadEngine.cancel()
    }

    /**
     * 检查是否暂停
     */
    fun isPaused(): Boolean = downloadEngine.isPaused()

    /**
     * 检查模型是否已下载
     */
    fun isModelDownloaded(): Boolean = modelFile.exists() && modelFile.length() > 0

    /**
     * 检查模型文件完整性
     * @return true 如果文件完整，false 如果文件不存在或不完整
     */
    fun isModelComplete(): Boolean {
        if (!modelFile.exists()) {
            return false
        }

        val fileSize = modelFile.length()

        // 从 Config.json 读取容差值（1MB）
        val sizeTolerance = 1024 * 1024L

        // 检查文件大小是否在预期范围内
        val sizeDiff = kotlin.math.abs(fileSize - expectedModelSize)
        return sizeDiff <= sizeTolerance
    }

    /**
     * 获取下载状态
     */
    fun getDStatus(): DStatus {
        return when {
            !modelFile.exists() -> DStatus.NOT_DOWNLOADED
            isModelComplete() -> DStatus.COMPLETE
            else -> DStatus.PARTIAL
        }
    }

    /**
     * 下载状态枚举
     */
    enum class DStatus {
        NOT_DOWNLOADED,  // 文件不存在
        PARTIAL,         // 文件部分下载
        COMPLETE         // 文件完整
    }

    /**
     * 获取模型文件路径
     */
    fun getModelPath(): String = modelFile.absolutePath

    /**
     * 删除模型
     */
    fun deleteModel(): Boolean = modelFile.delete()
}
