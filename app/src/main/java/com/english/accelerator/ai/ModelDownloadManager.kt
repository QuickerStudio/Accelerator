package com.english.accelerator.ai

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Manages downloading and storing the Gemma-2B model
 */
class ModelDownloadManager(private val context: Context) {
    private val modelUrl = "https://storage.googleapis.com/mediapipe-models/llm/gemma-2b-it-gpu-int4.bin"
    private val modelFile = File(context.filesDir, "models/gemma-2b.bin")

    /**
     * Download the model with progress tracking
     * @param onProgress Callback with download progress (0.0 to 1.0)
     * @return Result with the model file or error
     */
    suspend fun downloadModel(
        onProgress: (Float) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            // Create models directory if it doesn't exist
            modelFile.parentFile?.mkdirs()

            val url = URL(modelUrl)
            val connection = url.openConnection() as HttpURLConnection
            val totalSize = connection.contentLength.toLong()

            connection.inputStream.use { input ->
                modelFile.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var downloaded = 0L
                    var bytes: Int

                    while (input.read(buffer).also { bytes = it } != -1) {
                        output.write(buffer, 0, bytes)
                        downloaded += bytes
                        onProgress(downloaded.toFloat() / totalSize)
                    }
                }
            }

            Result.success(modelFile)
        } catch (e: Exception) {
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
