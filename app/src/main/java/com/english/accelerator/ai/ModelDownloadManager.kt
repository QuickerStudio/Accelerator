package com.english.accelerator.ai

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Manages downloading and storing the Gemma 3n E2B-it model
 * Supports dual-route download: Hugging Face (primary) and ModelScope (fallback)
 */
class ModelDownloadManager(private val context: Context) {
    // Gemma 3n E2B-it standard model (unified across all sources)
    // Dual-route download support for better accessibility

    private val primaryModelUrl = "https://huggingface.co/google/gemma-3n-E2B-it/resolve/main/gemma-3n-E2B-it.task"
    private val fallbackModelUrl = "https://www.modelscope.cn/models/google/gemma-3n-E2B-it/resolve/master/gemma-3n-E2B-it.task"

    private val modelFile = File(context.filesDir, "models/gemma-3n-e2b-it.task")

    /**
     * Download the model with progress tracking and automatic fallback
     * Tries primary URL first, falls back to secondary if primary fails
     * @param onProgress Callback with download progress (0.0 to 1.0)
     * @return Result with the model file or error
     */
    suspend fun downloadModel(
        onProgress: (Float) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        // Create models directory if it doesn't exist
        modelFile.parentFile?.mkdirs()

        // Try primary URL first (Hugging Face)
        val primaryResult = tryDownload(primaryModelUrl, onProgress)
        if (primaryResult.isSuccess) {
            return@withContext primaryResult
        }

        // If primary fails, try fallback URL (ModelScope)
        val fallbackResult = tryDownload(fallbackModelUrl, onProgress)
        if (fallbackResult.isSuccess) {
            return@withContext fallbackResult
        }

        // Both failed, return the last error
        Result.failure(
            Exception("Download failed from both sources. Primary: ${primaryResult.exceptionOrNull()?.message}, Fallback: ${fallbackResult.exceptionOrNull()?.message}")
        )
    }

    /**
     * Attempt to download from a specific URL
     */
    private suspend fun tryDownload(
        urlString: String,
        onProgress: (Float) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 30000 // 30 seconds
            connection.readTimeout = 30000

            val totalSize = connection.contentLength.toLong()

            connection.inputStream.use { input ->
                modelFile.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var downloaded = 0L
                    var bytes: Int

                    while (input.read(buffer).also { bytes = it } != -1) {
                        output.write(buffer, 0, bytes)
                        downloaded += bytes
                        if (totalSize > 0) {
                            onProgress(downloaded.toFloat() / totalSize)
                        }
                    }
                }
            }

            Result.success(modelFile)
        } catch (e: Exception) {
            // Clean up partial download
            if (modelFile.exists()) {
                modelFile.delete()
            }
            Result.failure(e)
        }
    }

    /**
     * Check if the model is already downloaded
     */
    fun isModelDownloaded(): Boolean = modelFile.exists() && modelFile.length() > 0

    /**
     * Get the absolute path to the model file
     */
    fun getModelPath(): String = modelFile.absolutePath

    /**
     * Get the model file size in bytes
     */
    fun getModelSize(): Long = if (modelFile.exists()) modelFile.length() else 0L

    /**
     * Delete the downloaded model
     */
    fun deleteModel(): Boolean = modelFile.delete()
}
