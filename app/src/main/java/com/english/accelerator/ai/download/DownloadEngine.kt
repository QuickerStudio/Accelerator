package com.english.accelerator.ai.download

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * 通用下载引擎
 *
 * 功能：
 * - 支持断点续传
 * - 支持暂停/恢复
 * - 实时进度和速度回调
 */
class DownloadEngine {
    @Volatile
    private var isPaused = false

    @Volatile
    private var isCancelled = false

    /**
     * 下载文件
     * @param url 下载地址
     * @param targetFile 目标文件
     * @param onProgress 进度回调 (已下载字节, 总字节, 速度 bytes/sec)
     * @return 下载结果
     */
    suspend fun download(
        url: String,
        targetFile: File,
        onProgress: (downloaded: Long, total: Long, speed: Long) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            // 重置状态
            isPaused = false
            isCancelled = false

            // 创建父目录
            targetFile.parentFile?.mkdirs()

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 30000
            connection.readTimeout = 30000

            // 断点续传：检查已下载的部分
            val existingSize = if (targetFile.exists()) targetFile.length() else 0L
            if (existingSize > 0) {
                connection.setRequestProperty("Range", "bytes=$existingSize-")
            }

            connection.connect()

            val responseCode = connection.responseCode
            val totalSize = if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                existingSize + connection.contentLength.toLong()
            } else {
                connection.contentLength.toLong()
            }

            connection.inputStream.use { input ->
                val append = responseCode == HttpURLConnection.HTTP_PARTIAL
                java.io.FileOutputStream(targetFile, append).use { output ->
                    val buffer = ByteArray(8192)
                    var downloaded = existingSize
                    var bytes: Int
                    var lastUpdateTime = System.currentTimeMillis()
                    var lastDownloaded = downloaded
                    var speed = 0L

                    // 初始进度回调
                    onProgress(downloaded, totalSize, speed)

                    while (input.read(buffer).also { bytes = it } != -1) {
                        // 检查是否取消
                        if (isCancelled) {
                            throw Exception("下载已取消")
                        }

                        // 暂停时等待
                        while (isPaused && !isCancelled) {
                            Thread.sleep(100)
                        }

                        output.write(buffer, 0, bytes)
                        downloaded += bytes

                        // 计算实时速度（每次都更新，但只在间隔>=1秒时重新计算）
                        val currentTime = System.currentTimeMillis()
                        val timeDiff = currentTime - lastUpdateTime
                        if (timeDiff >= 1000) {
                            speed = ((downloaded - lastDownloaded) * 1000) / timeDiff
                            lastUpdateTime = currentTime
                            lastDownloaded = downloaded
                        }

                        // 每次都回调进度，确保UI实时更新
                        onProgress(downloaded, totalSize, speed)
                    }

                    // 下载完成，最后一次回调确保进度为100%
                    onProgress(totalSize, totalSize, 0L)
                }
            }

            Result.success(targetFile)
        } catch (e: Exception) {
            // 保留部分下载用于续传（取消时才删除）
            if (isCancelled && targetFile.exists()) {
                targetFile.delete()
            }
            Result.failure(e)
        }
    }

    /**
     * 暂停下载
     */
    fun pause() {
        isPaused = true
    }

    /**
     * 恢复下载
     */
    fun resume() {
        isPaused = false
    }

    /**
     * 取消下载
     */
    fun cancel() {
        isCancelled = true
        isPaused = false
    }

    /**
     * 检查是否暂停
     */
    fun isPaused(): Boolean = isPaused
}
