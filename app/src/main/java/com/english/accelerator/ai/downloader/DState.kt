package com.english.accelerator.ai.downloader

/**
 * 下载状态数据类
 */
data class DState(
    val modelPath: String,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val isComplete: Boolean,
    val isPaused: Boolean,
    val lastUpdateTime: Long,
    val downloadRoute: String,
    val errorMessage: String?
)

/**
 * 下载线路信息
 */
data class DRoute(
    val name: String,
    val displayName: String,
    val url: String
)

/**
 * 下载状态枚举
 */
enum class DStatus {
    NOT_DOWNLOADED,  // 文件不存在
    PARTIAL,         // 文件部分下载
    COMPLETE         // 文件完整
}
