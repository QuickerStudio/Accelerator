/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ai.downloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * 网络检查和服务器能力检测
 *
 * 职责：
 * - 检查网络连接状态
 * - 检测服务器是否支持断点续传（Range 请求）
 * - 生成下载配置参数
 */
class DPing {

    /**
     * 检查服务器能力
     *
     * @param url 服务器地址
     * @param timeout 超时时间（毫秒）
     * @return 服务器能力检测结果
     */
    suspend fun checkServer(url: String, timeout: Int = 10000): ServerCapability = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.requestMethod = "HEAD"  // 使用 HEAD 请求，不下载内容

            // 发送 Range 请求测试服务器是否支持断点续传
            connection.setRequestProperty("Range", "bytes=0-0")

            connection.connect()

            val responseCode = connection.responseCode
            val contentLength = connection.contentLength.toLong()
            val acceptRanges = connection.getHeaderField("Accept-Ranges")
            val contentRange = connection.getHeaderField("Content-Range")

            connection.disconnect()

            // 分析服务器能力
            val supportsRange = when {
                // 方式1: 服务器返回 206 Partial Content
                responseCode == HttpURLConnection.HTTP_PARTIAL -> true
                // 方式2: 服务器返回 Accept-Ranges: bytes
                acceptRanges?.equals("bytes", ignoreCase = true) == true -> true
                // 方式3: 服务器返回 Content-Range 响应头
                contentRange != null -> true
                // 其他情况：不支持
                else -> false
            }

            ServerCapability(
                isReachable = true,
                supportsRange = supportsRange,
                contentLength = contentLength,
                responseCode = responseCode,
                acceptRanges = acceptRanges,
                contentRange = contentRange,
                recommendedChunkSize = calculateChunkSize(contentLength),
                errorMessage = null
            )
        } catch (e: Exception) {
            // 网络不可达或其他错误
            ServerCapability(
                isReachable = false,
                supportsRange = false,
                contentLength = -1L,
                responseCode = -1,
                acceptRanges = null,
                contentRange = null,
                recommendedChunkSize = 8192,
                errorMessage = e.message ?: "网络连接失败"
            )
        }
    }

    /**
     * 快速检查网络连接
     *
     * @param url 服务器地址
     * @param timeout 超时时间（毫秒）
     * @return 是否可达
     */
    suspend fun quickCheck(url: String, timeout: Int = 5000): Boolean = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.requestMethod = "HEAD"

            connection.connect()
            val isReachable = connection.responseCode in 200..299

            connection.disconnect()
            isReachable
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 根据文件大小计算推荐的分块大小
     *
     * @param contentLength 文件总大小
     * @return 推荐的分块大小（字节）
     */
    private fun calculateChunkSize(contentLength: Long): Int {
        return when {
            contentLength < 0 -> 8192  // 未知大小，使用默认值
            contentLength < 1024 * 1024 -> 4096  // < 1MB: 4KB
            contentLength < 10 * 1024 * 1024 -> 8192  // < 10MB: 8KB
            contentLength < 100 * 1024 * 1024 -> 16384  // < 100MB: 16KB
            contentLength < 1024 * 1024 * 1024 -> 32768  // < 1GB: 32KB
            else -> 65536  // >= 1GB: 64KB
        }
    }

    /**
     * 生成下载配置
     *
     * @param capability 服务器能力
     * @return 下载配置
     */
    fun generateDownloadConfig(capability: ServerCapability): DownloadConfig {
        return DownloadConfig(
            supportsRange = capability.supportsRange,
            chunkSize = capability.recommendedChunkSize,
            maxRetries = if (capability.isReachable) 3 else 0,
            timeoutMs = if (capability.isReachable) 30000 else 5000,
            useAppendMode = capability.supportsRange,
            shouldDeleteOnRetry = !capability.supportsRange
        )
    }
}

/**
 * 服务器能力检测结果
 */
data class ServerCapability(
    val isReachable: Boolean,              // 网络是否可达
    val supportsRange: Boolean,            // 是否支持断点续传
    val contentLength: Long,               // 文件总大小（-1 表示未知）
    val responseCode: Int,                 // HTTP 响应码
    val acceptRanges: String?,             // Accept-Ranges 响应头
    val contentRange: String?,             // Content-Range 响应头
    val recommendedChunkSize: Int,         // 推荐的分块大小
    val errorMessage: String?              // 错误信息
) {
    /**
     * 是否可以开始下载
     */
    fun canDownload(): Boolean = isReachable && responseCode in 200..299

    /**
     * 获取人类可读的状态描述
     */
    fun getStatusDescription(): String {
        return when {
            !isReachable -> "网络不可达: ${errorMessage ?: "未知错误"}"
            !canDownload() -> "服务器响应异常: HTTP $responseCode"
            supportsRange -> "✓ 服务器支持断点续传"
            else -> "⚠️ 服务器不支持断点续传，将使用完整下载模式"
        }
    }
}

/**
 * 下载配置
 */
data class DownloadConfig(
    val supportsRange: Boolean,            // 是否支持断点续传
    val chunkSize: Int,                    // 分块大小
    val maxRetries: Int,                   // 最大重试次数
    val timeoutMs: Int,                    // 超时时间
    val useAppendMode: Boolean,            // 是否使用追加模式
    val shouldDeleteOnRetry: Boolean       // 重试时是否删除旧文件
)
