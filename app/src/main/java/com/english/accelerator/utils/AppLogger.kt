package com.english.accelerator.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 应用日志系统
 *
 * 功能：
 * - 持久化日志到文件
 * - 分级日志（DEBUG, INFO, WARN, ERROR）
 * - 自动日志轮转（按大小和时间）
 * - 日志查询和导出
 */
object AppLogger {

    private const val TAG = "Accelerator"
    private const val LOG_DIR = "logs"
    private const val MAX_LOG_FILE_SIZE = 5 * 1024 * 1024 // 5MB
    private const val MAX_LOG_FILES = 10

    private lateinit var logDir: File
    private lateinit var currentLogFile: File
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    enum class Level {
        DEBUG, INFO, WARN, ERROR
    }

    fun init(context: Context) {
        logDir = File(context.filesDir, LOG_DIR)
        if (!logDir.exists()) {
            logDir.mkdirs()
        }

        // 创建或获取当前日志文件
        currentLogFile = File(logDir, "app_${getCurrentDate()}.log")

        // 清理旧日志
        cleanOldLogs()

        info("AppLogger", "Logger initialized")
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun debug(tag: String, message: String) {
        log(Level.DEBUG, tag, message)
    }

    fun info(tag: String, message: String) {
        log(Level.INFO, tag, message)
    }

    fun warn(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.WARN, tag, message, throwable)
    }

    fun error(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.ERROR, tag, message, throwable)
    }

    private fun log(level: Level, tag: String, message: String, throwable: Throwable? = null) {
        val timestamp = dateFormat.format(Date())
        val logMessage = buildString {
            append("[$timestamp] ")
            append("[${level.name}] ")
            append("[$tag] ")
            append(message)
            if (throwable != null) {
                append("\n")
                append(throwable.stackTraceToString())
            }
        }

        // 输出到 Logcat
        when (level) {
            Level.DEBUG -> Log.d(TAG, "[$tag] $message", throwable)
            Level.INFO -> Log.i(TAG, "[$tag] $message", throwable)
            Level.WARN -> Log.w(TAG, "[$tag] $message", throwable)
            Level.ERROR -> Log.e(TAG, "[$tag] $message", throwable)
        }

        // 写入文件
        writeToFile(logMessage)
    }

    private fun writeToFile(message: String) {
        try {
            // 检查文件大小，如果超过限制则轮转
            if (currentLogFile.exists() && currentLogFile.length() > MAX_LOG_FILE_SIZE) {
                rotateLogFile()
            }

            currentLogFile.appendText(message + "\n")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write log to file", e)
        }
    }

    private fun rotateLogFile() {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
        val rotatedFile = File(logDir, "app_${timestamp}.log")
        currentLogFile.renameTo(rotatedFile)
        currentLogFile = File(logDir, "app_${getCurrentDate()}.log")
    }

    private fun cleanOldLogs() {
        val logFiles = logDir.listFiles()?.sortedByDescending { it.lastModified() } ?: return

        // 保留最近的 MAX_LOG_FILES 个文件
        logFiles.drop(MAX_LOG_FILES).forEach { file ->
            file.delete()
            Log.d(TAG, "Deleted old log file: ${file.name}")
        }
    }

    /**
     * 获取所有日志文件
     */
    fun getLogFiles(): List<File> {
        return logDir.listFiles()?.sortedByDescending { it.lastModified() }?.toList() ?: emptyList()
    }

    /**
     * 获取最新的日志内容
     */
    fun getRecentLogs(lines: Int = 100): String {
        return try {
            currentLogFile.readLines().takeLast(lines).joinToString("\n")
        } catch (e: Exception) {
            "Failed to read logs: ${e.message}"
        }
    }

    /**
     * 导出所有日志到指定文件
     */
    fun exportLogs(targetFile: File): Boolean {
        return try {
            val allLogs = getLogFiles().flatMap { it.readLines() }
            targetFile.writeText(allLogs.joinToString("\n"))
            true
        } catch (e: Exception) {
            error("AppLogger", "Failed to export logs", e)
            false
        }
    }

    /**
     * 清除所有日志
     */
    fun clearAllLogs() {
        logDir.listFiles()?.forEach { it.delete() }
        currentLogFile = File(logDir, "app_${getCurrentDate()}.log")
        info("AppLogger", "All logs cleared")
    }
}
