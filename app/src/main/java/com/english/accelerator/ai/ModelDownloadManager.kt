package com.english.accelerator.ai

import android.content.Context
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
 * - Smart route switching based on network latency
 * - Pause/Resume download support
 * - Manual route switching
 */
class ModelDownloadManager(private val context: Context) {
    // Gemma 3n E2B-it standard model (unified across all sources)
    private val primaryModelUrl = "https://huggingface.co/google/gemma-3n-E2B-it/resolve/main/gemma-3n-E2B-it.task"
    private val fallbackModelUrl = "https://www.modelscope.cn/models/google/gemma-3n-E2B-it/resolve/master/gemma-3n-E2B-it.task"

    private val modelFile = File(context.filesDir, "models/gemma-3n-e2b-it.task")

    // Download control
    @Volatile
    private var isPaused = false
    @Volatile
    private var isCancelled = false

    // Current route
    enum class DownloadRoute {
        PRIMARY,    // HuggingFace
        FALLBACK    // ModelScope
    }

    private var currentRoute: DownloadRoute = DownloadRoute.PRIMARY

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
     * - Checks if model already exists (skip download)
     * - Pings both routes and selects faster one
     * - Falls back to alternative route if download fails
     * - Supports pause/resume with resume capability
     *
     * @param onProgress Callback with download progress (0.0 to 1.0) and speed (bytes/sec)
     * @param forceRoute Optional: force a specific download route
     * @return Result with the model file or error
     */
    suspend fun downloadModel(
        onProgress: (Float, Long) -> Unit,
        forceRoute: DownloadRoute? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        // Reset control flags
        isPaused = false
        isCancelled = false

        // Check if model already exists
        if (isModelDownloaded()) {
            return@withContext Result.success(modelFile)
        }

        // Create models directory
        modelFile.parentFile?.mkdirs()

        // Select route
        val selectedUrl = if (forceRoute != null) {
            currentRoute = forceRoute
            when (forceRoute) {
                DownloadRoute.PRIMARY -> primaryModelUrl
                DownloadRoute.FALLBACK -> fallbackModelUrl
            }
        } else {
            // Auto-select based on ping
            val bestRoute = selectBestRoute()
            currentRoute = if (bestRoute == primaryModelUrl) DownloadRoute.PRIMARY else DownloadRoute.FALLBACK
            bestRoute
        }

        val alternativeUrl = if (selectedUrl == primaryModelUrl) fallbackModelUrl else primaryModelUrl

        // Try selected route first
        val result = tryDownload(selectedUrl, onProgress)
        if (result.isSuccess) {
            return@withContext result
        }

        // Fall back to alternative route if not cancelled
        if (!isCancelled) {
            val fallbackResult = tryDownload(alternativeUrl, onProgress)
            if (fallbackResult.isSuccess) {
                return@withContext fallbackResult
            }
        }

        // Both failed or cancelled
        if (isCancelled) {
            Result.failure(Exception("Download cancelled"))
        } else {
            Result.failure(
                Exception("Download failed from both sources. Selected: ${result.exceptionOrNull()?.message}")
            )
        }
    }

    /**
     * Pause the current download
     */
    fun pauseDownload() {
        isPaused = true
    }

    /**
     * Resume the paused download
     */
    fun resumeDownload() {
        isPaused = false
    }

    /**
     * Cancel the current download
     */
    fun cancelDownload() {
        isCancelled = true
        isPaused = false
    }

    /**
     * Check if download is paused
     */
    fun isPaused(): Boolean = isPaused

    /**
     * Get current download route
     */
    fun getCurrentRoute(): DownloadRoute = currentRoute

    /**
     * Get route name for display
     */
    fun getRouteName(route: DownloadRoute): String = when (route) {
        DownloadRoute.PRIMARY -> "HuggingFace"
        DownloadRoute.FALLBACK -> "ModelScope (国内)"
    }

    /**
     * Attempt to download from a specific URL with resume support
     */
    private suspend fun tryDownload(
        urlString: String,
        onProgress: (Float, Long) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 30000 // 30 seconds
            connection.readTimeout = 30000

            // Check if partial file exists for resume
            val existingSize = if (modelFile.exists()) modelFile.length() else 0L
            if (existingSize > 0) {
                connection.setRequestProperty("Range", "bytes=$existingSize-")
            }

            connection.connect()

            val responseCode = connection.responseCode
            val totalSize = if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                // Resume download
                existingSize + connection.contentLength.toLong()
            } else {
                // Fresh download
                connection.contentLength.toLong()
            }

            connection.inputStream.use { input ->
                modelFile.outputStream(responseCode == HttpURLConnection.HTTP_PARTIAL).use { output ->
                    val buffer = ByteArray(8192)
                    var downloaded = existingSize
                    var bytes: Int
                    var lastUpdateTime = System.currentTimeMillis()
                    var lastDownloaded = downloaded
                    var speed = 0L

                    while (input.read(buffer).also { bytes = it } != -1) {
                        // Check if cancelled
                        if (isCancelled) {
                            throw Exception("Download cancelled")
                        }

                        // Wait if paused
                        while (isPaused && !isCancelled) {
                            Thread.sleep(100)
                        }

                        output.write(buffer, 0, bytes)
                        downloaded += bytes

                        // Calculate speed every second
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastUpdateTime >= 1000) {
                            speed = ((downloaded - lastDownloaded) * 1000) / (currentTime - lastUpdateTime)
                            lastUpdateTime = currentTime
                            lastDownloaded = downloaded
                        }

                        if (totalSize > 0) {
                            onProgress(downloaded.toFloat() / totalSize, speed)
                        }
                    }
                }
            }

            Result.success(modelFile)
        } catch (e: Exception) {
            // Keep partial download for resume (don't delete unless cancelled)
            if (isCancelled && modelFile.exists()) {
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
