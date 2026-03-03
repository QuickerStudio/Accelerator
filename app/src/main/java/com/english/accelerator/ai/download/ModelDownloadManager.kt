package com.english.accelerator.ai.download

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 模型下载管理器
 *
 * 管理 Gemma 3n E2B-it 模型的下载
 */
class ModelDownloadManager(private val context: Context) {
    // 下载线路
    private val huggingFaceUrl = "https://huggingface.co/google/gemma-3n-E2B-it-litert-lm/resolve/main/gemma-3n-E2B-it-int4.litertlm"
    private val modelScopeUrl = "https://www.modelscope.cn/models/google/gemma-3n-E2B-it-litert-lm/resolve/master/gemma-3n-E2B-it-int4.litertlm"

    private val modelFile = File(context.filesDir, "models/gemma-3n-e2b-it-int4.litertlm")

    // 下载引擎
    private val downloadEngine = DownloadEngine()

    // 线路枚举
    enum class DownloadRoute {
        HUGGINGFACE,
        MODELSCOPE
    }

    private var selectedRoute: DownloadRoute = DownloadRoute.HUGGINGFACE

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
        downloadEngine.download(getSelectedUrl(), modelFile, onProgress)
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
     * 获取模型文件路径
     */
    fun getModelPath(): String = modelFile.absolutePath

    /**
     * 删除模型
     */
    fun deleteModel(): Boolean = modelFile.delete()
}
