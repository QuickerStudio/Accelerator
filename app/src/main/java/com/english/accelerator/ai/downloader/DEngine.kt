/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ai.downloader

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
class DEngine {
    @Volatile
    private var isPaused = false

    @Volatile
    private var isCancelled = false

    @Volatile
    private var isDownloading = false

    /**
     * 下载文件
     * @param url 下载地址
     * @param targetFile 目标文件
     * @param onProgress 进度回调 (已下载字节, 总字节, 速度 bytes/sec)
     * @param onRangeSupportDetected 断点续传支持检测回调 (url, supportsRange)
     * @return 下载结果
     */
    suspend fun download(
        url: String,
        targetFile: File,
        onProgress: (downloaded: Long, total: Long, speed: Long) -> Unit,
        onRangeSupportDetected: ((String, Boolean) -> Unit)? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            // 设置下载状态
            isDownloading = true
            isCancelled = false
            // 不重置 isPaused，保留暂停状态以支持恢复

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

            // 🔧 修复：检查服务器是否支持断点续传
            val supportsRange: Boolean
            val totalSize: Long
            val append: Boolean
            val startOffset: Long

            when (responseCode) {
                HttpURLConnection.HTTP_PARTIAL -> {
                    // 206 Partial Content - 服务器支持断点续传
                    supportsRange = true
                    append = true
                    startOffset = existingSize

                    // 从 Content-Range 响应头获取总大小
                    val contentRange = connection.getHeaderField("Content-Range")
                    totalSize = if (contentRange != null) {
                        // 格式：bytes <start>-<end>/<total>
                        // 例如：bytes 1024-3655827455/3655827456
                        val parts = contentRange.split("/")
                        if (parts.size == 2) {
                            parts[1].toLongOrNull() ?: (existingSize + connection.contentLength.toLong())
                        } else {
                            existingSize + connection.contentLength.toLong()
                        }
                    } else {
                        existingSize + connection.contentLength.toLong()
                    }
                }
                HttpURLConnection.HTTP_OK -> {
                    // 200 OK - 服务器不支持断点续传或返回完整内容
                    if (existingSize > 0 && !isPaused) {
                        // 只有在非暂停状态下才删除文件
                        // 如果是暂停后恢复，保留已下载的内容
                        supportsRange = false
                        targetFile.delete()
                        println("⚠️ 服务器不支持断点续传，删除旧文件重新下载")
                    } else if (existingSize > 0 && isPaused) {
                        // 暂停后恢复，但服务器不支持断点续传
                        throw Exception("服务器不支持断点续传，无法恢复下载。请重新开始下载。")
                    } else {
                        // 全新下载
                        supportsRange = true  // 无法判断，假设支持
                    }
                    totalSize = connection.contentLength.toLong()
                    append = false
                    startOffset = 0L
                }
                else -> {
                    throw Exception("下载失败: HTTP $responseCode")
                }
            }

            // 通知调用者服务器是否支持断点续传
            onRangeSupportDetected?.invoke(url, supportsRange)

            connection.inputStream.use { input ->
                java.io.FileOutputStream(targetFile, append).use { output ->
                    val buffer = ByteArray(8192)
                    var downloaded = startOffset
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
        } finally {
            isDownloading = false
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

    /**
     * 检查是否正在下载
     */
    fun isDownloading(): Boolean = isDownloading
}
