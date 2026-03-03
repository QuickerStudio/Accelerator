package com.english.accelerator.ai.downloader

import android.content.Context
import java.io.File
import kotlin.math.abs

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
    val url: String,
    val supportsRange: Boolean = true,  // 是否支持断点续传
    val rangeChecked: Boolean = false   // 是否已检测过
)

/**
 * 下载状态枚举
 */
enum class DStatus {
    NOT_DOWNLOADED,  // 文件不存在
    PARTIAL,         // 文件部分下载
    COMPLETE         // 文件完整
}

/**
 * 下载状态监视器
 *
 * 负责监视下载文件目录的状态，并返回各种必要的状态信息给下载管理器
 */
class DStateMonitor(
    private val context: Context,
    private val configManager: DConfig  // 接收 DManager 传递的配置实例
) {

    private val modelFile: File
        get() = File(context.filesDir, "models/${configManager.getModelFileName()}")

    /**
     * 获取模型文件的当前状态
     */
    fun getFileStatus(): DStatus {
        return when {
            !modelFile.exists() -> DStatus.NOT_DOWNLOADED
            isFileComplete() -> DStatus.COMPLETE
            else -> DStatus.PARTIAL
        }
    }

    /**
     * 检查文件是否完整
     */
    fun isFileComplete(): Boolean {
        if (!modelFile.exists()) {
            return false
        }

        val fileSize = modelFile.length()
        val expectedSize = configManager.getExpectedModelSize()
        val sizeTolerance = configManager.getSizeTolerance()

        val sizeDiff = abs(fileSize - expectedSize)
        return sizeDiff <= sizeTolerance
    }

    /**
     * 检查文件是否存在
     */
    fun fileExists(): Boolean = modelFile.exists()

    /**
     * 获取文件当前大小
     */
    fun getFileSize(): Long = if (modelFile.exists()) modelFile.length() else 0L

    /**
     * 获取文件路径
     */
    fun getFilePath(): String = modelFile.absolutePath

    /**
     * 获取下载进度百分比
     */
    fun getProgressPercentage(): Float {
        val currentSize = getFileSize()
        val expectedSize = configManager.getExpectedModelSize()

        return if (expectedSize > 0) {
            (currentSize.toFloat() / expectedSize.toFloat() * 100f)
        } else {
            0f
        }
    }

    /**
     * 从Config.json获取当前下载状态
     */
    fun getCurrentState(): DState? {
        return configManager.getCurrentDownloadState()
    }

    /**
     * 获取完整的状态信息（结合文件系统和Config.json）
     */
    fun getFullState(): DStateInfo {
        val fileStatus = getFileStatus()
        val fileSize = getFileSize()
        val expectedSize = configManager.getExpectedModelSize()
        val configState = getCurrentState()

        return DStateInfo(
            fileStatus = fileStatus,
            filePath = getFilePath(),
            fileSize = fileSize,
            expectedSize = expectedSize,
            progressPercentage = getProgressPercentage(),
            configState = configState,
            isFileComplete = isFileComplete(),
            fileExists = fileExists()
        )
    }

    /**
     * 验证文件完整性（详细检查）
     */
    fun validateFile(): FileValidationResult {
        if (!modelFile.exists()) {
            return FileValidationResult(
                isValid = false,
                reason = "文件不存在"
            )
        }

        val fileSize = modelFile.length()
        val expectedSize = configManager.getExpectedModelSize()
        val sizeTolerance = configManager.getSizeTolerance()
        val sizeDiff = abs(fileSize - expectedSize)

        return when {
            sizeDiff <= sizeTolerance -> FileValidationResult(
                isValid = true,
                reason = "文件完整",
                fileSize = fileSize,
                expectedSize = expectedSize,
                sizeDiff = sizeDiff
            )
            fileSize < expectedSize -> FileValidationResult(
                isValid = false,
                reason = "文件不完整（部分下载）",
                fileSize = fileSize,
                expectedSize = expectedSize,
                sizeDiff = sizeDiff
            )
            else -> FileValidationResult(
                isValid = false,
                reason = "文件大小超出预期",
                fileSize = fileSize,
                expectedSize = expectedSize,
                sizeDiff = sizeDiff
            )
        }
    }

    /**
     * 删除模型文件
     */
    fun deleteFile(): Boolean {
        return try {
            if (modelFile.exists()) {
                modelFile.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * 完整状态信息
 */
data class DStateInfo(
    val fileStatus: DStatus,
    val filePath: String,
    val fileSize: Long,
    val expectedSize: Long,
    val progressPercentage: Float,
    val configState: DState?,
    val isFileComplete: Boolean,
    val fileExists: Boolean
)

/**
 * 文件验证结果
 */
data class FileValidationResult(
    val isValid: Boolean,
    val reason: String,
    val fileSize: Long = 0L,
    val expectedSize: Long = 0L,
    val sizeDiff: Long = 0L
)
