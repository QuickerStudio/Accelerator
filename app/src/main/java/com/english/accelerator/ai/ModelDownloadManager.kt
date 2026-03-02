package com.english.accelerator.ai

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.measureTimeMillis

/**
 * Manages downloading and storing the Gemma 3n E2B-it model
 *
 * Features:
 * - Dual-route download with automatic ping-based selection
 * - Local file detection (Desktop, Downloads, etc.)
 * - Smart route switching based on network latency
 * - Resume download support (TODO)
 */
class ModelDownloadManager(private val context: Context) {
    // Gemma 3n E2B-it standard model (unified across all sources)
    private val primaryModelUrl = "https://huggingface.co/google/gemma-3n-E2B-it/resolve/main/gemma-3n-E2B-it.task"
    private val fallbackModelUrl = "https://www.modelscope.cn/models/google/gemma-3n-E2B-it/resolve/master/gemma-3n-E2B-it.task"

    private val modelFile = File(context.filesDir, "models/gemma-3n-e2b-it.task")

    // Common local paths to check for existing model
    private val localSearchPaths = listOf(
        // Desktop
        File(Environment.getExternalStorageDirectory(), "Desktop/gemma-3n-E2B-it"),
        // Downloads
        File(Environment.getExternalStorageDirectory(), "Download/gemma-3n-E2B-it"),
        File(Environment.getExternalStorageDirectory(), "Downloads/gemma-3n-E2B-it"),
        // Documents
        File(Environment.getExternalStorageDirectory(), "Documents/gemma-3n-E2B-it")
    )

    /**
     * Check if model exists locally (in app storage or common locations)
     * @return Pair<Boolean, String?> - (exists, path if found)
     */
    suspend fun checkLocalModel(): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        // Check app storage first
        if (isModelDownloaded()) {
            return@withContext Pair(true, modelFile.absolutePath)
        }

        // Check common local paths
        for (searchPath in localSearchPaths) {
            if (searchPath.exists() && searchPath.isDirectory) {
                // Look for .task file in the directory
                val taskFile = searchPath.listFiles()?.find { it.extension == "task" }
                if (taskFile != null && taskFile.length() > 100_000_000) { // At least 100MB
                    return@withContext Pair(true, taskFile.absolutePath)
                }
            }
        }

        Pair(false, null)
    }

    /**
     * Copy local model file to app storage
     */
    suspend fun copyLocalModel(sourcePath: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val sourceFile = File(sourcePath)
            if (!sourceFile.exists()) {
                return@withContext Result.failure(Exception("Source file not found: $sourcePath"))
            }

            // Create destination directory
            modelFile.parentFile?.mkdirs()

            // Copy file
            sourceFile.inputStream().use { input ->
                modelFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            Result.success(modelFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Ping a URL to measure latency
     * @return Latency in milliseconds, or Long.MAX_VALUE if unreachable
     */
    private suspend fun pingUrl(urlString: String): Long = withContext(Dispatchers.IO) {
        try {
            val latency = measureTimeMillis {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "HEAD"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()
                connection.disconnect()
            }
            latency
        } catch (e: Exception) {
            Long.MAX_VALUE // Unreachable
        }
    }

    /**
     * Select best download route based on ping latency
     * @return Selected URL (primary or fallback)
     */
    private suspend fun selectBestRoute(): String = withContext(Dispatchers.IO) {
        // Ping both routes
        val primaryLatency = pingUrl(primaryModelUrl)
        val fallbackLatency = pingUrl(fallbackModelUrl)

        // Select faster route
        if (primaryLatency < fallbackLatency) {
            primaryModelUrl
        } else {
            fallbackModelUrl
        }
    }

    /**
     * Download the model with progress tracking and automatic route selection
     * - Checks local files first
     * - Pings both routes and selects faster one
     * - Falls back to alternative route if download fails
     *
     * @param onProgress Callback with download progress (0.0 to 1.0)
     * @return Result with the model file or error
     */
    suspend fun downloadModel(
        onProgress: (Float) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        // Step 1: Check if model already exists locally
        val (localExists, localPath) = checkLocalModel()
        if (localExists && localPath != null) {
            // Copy local file to app storage
            return@withContext copyLocalModel(localPath)
        }

        // Step 2: Create models directory
        modelFile.parentFile?.mkdirs()

        // Step 3: Select best route based on ping
        val bestRoute = selectBestRoute()
        val alternativeRoute = if (bestRoute == primaryModelUrl) fallbackModelUrl else primaryModelUrl

        // Step 4: Try best route first
        val bestResult = tryDownload(bestRoute, onProgress)
        if (bestResult.isSuccess) {
            return@withContext bestResult
        }

        // Step 5: Fall back to alternative route
        val fallbackResult = tryDownload(alternativeRoute, onProgress)
        if (fallbackResult.isSuccess) {
            return@withContext fallbackResult
        }

        // Both failed
        Result.failure(
            Exception("Download failed from both sources. Best route: ${bestResult.exceptionOrNull()?.message}, Alternative: ${fallbackResult.exceptionOrNull()?.message}")
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
