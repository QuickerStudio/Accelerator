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
    private val modelConfig = ModelConfig.getInstance()

    // Config.json 管理器 - 单一真相来源
    private val configManager = DConfig(context)

    // 状态监视器 - 监视文件和下载状态
    private val stateMonitor = DStateMonitor(context)

    // 下载引擎
    private val downloadEngine = DEngine()

    // 模型文件引用
    private val modelFile: File
        get() = File(stateMonitor.getFilePath())

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
        // 使用状态监视器检查模型是否已完整下载
        if (stateMonitor.isFileComplete()) {
            configManager.addDownloadLog("Model already complete, skipping download")
            return@withContext Result.success(modelFile)
        }

        // 检查是否可以恢复下载
        val existingSize = stateMonitor.getFileSize()
        if (existingSize > 0) {
            configManager.addResumeLog("Resuming download from $existingSize bytes")
        } else {
            configManager.addDownloadLog("Starting new download from ${selectedRoute.name}")
        }

        // 更新下载状态
        val expectedSize = configManager.getExpectedModelSize()
        configManager.updateDownloadState(
            modelPath = stateMonitor.getFilePath(),
            downloadedBytes = existingSize,
            totalBytes = expectedSize,
            isComplete = false,
            isPaused = false,
            downloadRoute = selectedRoute.name
        )

        // 使用当前选择的线路下载
        val result = downloadEngine.download(getSelectedUrl(), modelFile, onProgress)

        // 下载成功后验证完整性并保存配置
        if (result.isSuccess) {
            val validation = stateMonitor.validateFile()
            if (validation.isValid) {
                modelConfig.markModelDownloaded(stateMonitor.getFilePath(), stateMonitor.getFileSize())
                configManager.updateDownloadState(
                    modelPath = stateMonitor.getFilePath(),
                    downloadedBytes = stateMonitor.getFileSize(),
                    totalBytes = expectedSize,
                    isComplete = true,
                    isPaused = false,
                    downloadRoute = selectedRoute.name
                )
                configManager.addDownloadLog("Download completed successfully: ${stateMonitor.getFileSize()} bytes")
            } else {
                configManager.addErrorLog("Download validation failed: ${validation.reason}")
                return@withContext Result.failure(Exception(validation.reason))
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
    fun isModelDownloaded(): Boolean = stateMonitor.fileExists() && stateMonitor.getFileSize() > 0

    /**
     * 检查模型文件完整性
     */
    fun isModelComplete(): Boolean = stateMonitor.isFileComplete()

    /**
     * 获取下载状态
     */
    fun getDStatus(): DStatus = stateMonitor.getFileStatus()

    /**
     * 获取完整状态信息
     */
    fun getFullState(): DStateInfo = stateMonitor.getFullState()

    /**
     * 获取下载进度百分比
     */
    fun getProgressPercentage(): Float = stateMonitor.getProgressPercentage()

    /**
     * 获取模型文件路径
     */
    fun getModelPath(): String = stateMonitor.getFilePath()

    /**
     * 删除模型
     */
    fun deleteModel(): Boolean = stateMonitor.deleteFile()
}
