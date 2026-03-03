package com.english.accelerator.ai.download

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
class ModelDownloadManager(private val context: Context) {
    companion object {
        // 模型文件预期大小（约 1.8GB）
        private const val EXPECTED_MODEL_SIZE = 1_932_735_488L // 精确字节数
        private const val SIZE_TOLERANCE = 1024 * 1024 // 1MB 容差
    }

    // 下载线路
    private val huggingFaceUrl = "https://huggingface.co/google/gemma-3n-E2B-it-litert-lm/resolve/main/gemma-3n-E2B-it-int4.litertlm"
    private val modelScopeUrl = "https://www.modelscope.cn/models/google/gemma-3n-E2B-it-litert-lm/resolve/master/gemma-3n-E2B-it-int4.litertlm"

    private val modelFile = File(context.filesDir, "models/gemma-3n-e2b-it-int4.litertlm")
    private val modelConfig = ModelConfig.getInstance()

    // 下载引擎
    private val downloadEngine = DownloadEngine()

    // 线路枚举
    enum class DownloadRoute {
        HUGGINGFACE,
        MODELSCOPE
    }

    private var selectedRoute: DownloadRoute = DownloadRoute.HUGGINGFACE

    init {
        // 从配置中恢复下载线路
        val savedRoute = modelConfig.getDownloadRoute()
        if (savedRoute != null) {
            selectedRoute = try {
                DownloadRoute.valueOf(savedRoute)
            } catch (e: Exception) {
                DownloadRoute.HUGGINGFACE
            }
        }
    }

    /**
     * 获取当前选择的下载地址
     */
    private fun getSelectedUrl(): String = when (selectedRoute) {
        DownloadRoute.HUGGINGFACE -> huggingFaceUrl
        DownloadRoute.MODELSCOPE -> modelScopeUrl
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
        // 检查模型是否已存在
        if (isModelDownloaded()) {
            return@withContext Result.success(modelFile)
        }

        // 使用当前选择的线路下载
        val result = downloadEngine.download(getSelectedUrl(), modelFile, onProgress)

        // 下载成功后保存配置
        if (result.isSuccess) {
            modelConfig.markModelDownloaded(modelFile.absolutePath, modelFile.length())
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

        // 检查文件大小是否在预期范围内
        val sizeDiff = kotlin.math.abs(fileSize - EXPECTED_MODEL_SIZE)
        return sizeDiff <= SIZE_TOLERANCE
    }

    /**
     * 获取下载状态
     */
    fun getDownloadStatus(): DownloadStatus {
        return when {
            !modelFile.exists() -> DownloadStatus.NOT_DOWNLOADED
            isModelComplete() -> DownloadStatus.COMPLETE
            else -> DownloadStatus.PARTIAL
        }
    }

    /**
     * 下载状态枚举
     */
    enum class DownloadStatus {
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
