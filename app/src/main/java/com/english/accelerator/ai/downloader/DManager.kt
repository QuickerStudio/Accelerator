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

    // 状态监视器 - 监视文件和下载状态（传递配置实例）
    private val stateMonitor = DStateMonitor(context, configManager)

    // 网络检查器 - 检查网络和服务器能力
    private val networkPing = DPing()

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
     *
     * 注意：切换线路时会删除不完整的文件，避免从不同源续传导致文件损坏
     */
    fun switchRoute(): Boolean {
        // 检查是否有未完成的下载
        if (stateMonitor.fileExists() && !stateMonitor.isFileComplete()) {
            // 删除不完整的文件，避免从不同源续传
            val deleted = stateMonitor.deleteFile()
            if (deleted) {
                configManager.addDownloadLog("切换线路：删除不完整的文件以避免文件损坏")
                // 清除下载状态
                configManager.clearDownloadState()
            } else {
                configManager.addErrorLog("切换线路失败：无法删除不完整的文件")
                return false
            }
        }

        // 切换线路
        selectedRoute = when (selectedRoute) {
            DownloadRoute.HUGGINGFACE -> DownloadRoute.MODELSCOPE
            DownloadRoute.MODELSCOPE -> DownloadRoute.HUGGINGFACE
        }

        // 保存新线路到配置
        modelConfig.saveDownloadRoute(selectedRoute.name)
        configManager.addDownloadLog("已切换到线路: ${getCurrentRouteName()}")

        return true
    }

    /**
     * 获取当前线路名称
     */
    fun getCurrentRouteName(): String = when (selectedRoute) {
        DownloadRoute.HUGGINGFACE -> "备用线路" // HuggingFace
        DownloadRoute.MODELSCOPE -> "默认线路" // 魔塔社区
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

        val currentUrl = getSelectedUrl()

        // 🔧 步骤1: 使用 DPing 检查服务器能力
        configManager.addDownloadLog("正在检查服务器能力...")
        val serverCapability = networkPing.checkServer(currentUrl)

        // 记录检测结果
        configManager.addDownloadLog(serverCapability.getStatusDescription())

        // 检查网络是否可达
        if (!serverCapability.isReachable) {
            val errorMsg = "网络不可达: ${serverCapability.errorMessage}"
            configManager.addErrorLog(errorMsg)
            return@withContext Result.failure(Exception(errorMsg))
        }

        // 检查服务器响应是否正常
        if (!serverCapability.canDownload()) {
            val errorMsg = "服务器响应异常: HTTP ${serverCapability.responseCode}"
            configManager.addErrorLog(errorMsg)
            return@withContext Result.failure(Exception(errorMsg))
        }

        // 🔧 步骤2: 保存服务器 Range 支持状态到配置
        configManager.updateRouteRangeSupport(currentUrl, serverCapability.supportsRange)

        // 🔧 步骤3: 生成下载配置
        val downloadConfig = networkPing.generateDownloadConfig(serverCapability)

        // 检查是否可以恢复下载
        val existingSize = stateMonitor.getFileSize()
        if (existingSize > 0) {
            if (downloadConfig.supportsRange) {
                configManager.addResumeLog("断点续传: 从 $existingSize bytes 继续下载")
            } else {
                configManager.addDownloadLog("服务器不支持断点续传，删除旧文件重新下载")
                stateMonitor.deleteFile()
            }
        } else {
            configManager.addDownloadLog("开始全新下载: ${selectedRoute.name}")
        }

        // 更新下载状态
        val expectedSize = configManager.getExpectedModelSize()
        configManager.updateDownloadState(
            modelPath = stateMonitor.getFilePath(),
            downloadedBytes = if (downloadConfig.supportsRange) existingSize else 0L,
            totalBytes = expectedSize,
            isComplete = false,
            isPaused = false,
            downloadRoute = selectedRoute.name
        )

        // 🔧 步骤4: 使用 DEngine 执行下载任务
        val result = downloadEngine.download(
            url = currentUrl,
            targetFile = modelFile,
            onProgress = onProgress,
            onRangeSupportDetected = { url, supportsRange ->
                // 二次确认：如果实际下载时发现 Range 支持状态不同，更新配置
                if (supportsRange != serverCapability.supportsRange) {
                    configManager.updateRouteRangeSupport(url, supportsRange)
                    configManager.addDownloadLog("更新 Range 支持状态: $supportsRange")
                }
            }
        )

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

        // 保存当前下载进度到 config.json
        val currentSize = stateMonitor.getFileSize()
        val expectedSize = configManager.getExpectedModelSize()
        configManager.updateDownloadState(
            modelPath = stateMonitor.getFilePath(),
            downloadedBytes = currentSize,
            totalBytes = expectedSize,
            isComplete = false,
            isPaused = true,
            downloadRoute = selectedRoute.name
        )
        configManager.addDownloadLog("Download paused at $currentSize bytes")
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
