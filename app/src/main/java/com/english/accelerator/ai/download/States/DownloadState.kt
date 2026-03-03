package com.english.accelerator.ai.download.States

import com.google.gson.Gson
import java.io.File

/**
 * 下载状态数据类
 *
 * 用于断点续传和状态管理的内部记忆系统
 */
data class DownloadState(
    val modelPath: String,
    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val isComplete: Boolean = false,
    val isPaused: Boolean = false,
    val lastUpdateTime: Long = System.currentTimeMillis(),
    val downloadRoute: String = "HUGGINGFACE",
    val errorMessage: String? = null
) {
    companion object {
        private const val STATE_FILE_NAME = "download_state.json"

        /**
         * 从文件加载状态
         */
        fun load(stateDir: File): DownloadState? {
            val stateFile = File(stateDir, STATE_FILE_NAME)
            if (!stateFile.exists()) {
                return null
            }

            return try {
                val json = stateFile.readText()
                Gson().fromJson(json, DownloadState::class.java)
            } catch (e: Exception) {
                null
            }
        }

        /**
         * 创建新的下载状态
         */
        fun create(modelPath: String, totalBytes: Long, downloadRoute: String): DownloadState {
            return DownloadState(
                modelPath = modelPath,
                totalBytes = totalBytes,
                downloadRoute = downloadRoute
            )
        }
    }

    /**
     * 保存状态到文件
     */
    fun save(stateDir: File): Boolean {
        return try {
            stateDir.mkdirs()
            val stateFile = File(stateDir, STATE_FILE_NAME)
            val json = Gson().toJson(this)
            stateFile.writeText(json)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 更新下载进度
     */
    fun updateProgress(downloadedBytes: Long): DownloadState {
        return copy(
            downloadedBytes = downloadedBytes,
            lastUpdateTime = System.currentTimeMillis()
        )
    }

    /**
     * 标记为完成
     */
    fun markComplete(): DownloadState {
        return copy(
            isComplete = true,
            downloadedBytes = totalBytes,
            lastUpdateTime = System.currentTimeMillis()
        )
    }

    /**
     * 标记为暂停
     */
    fun markPaused(): DownloadState {
        return copy(
            isPaused = true,
            lastUpdateTime = System.currentTimeMillis()
        )
    }

    /**
     * 恢复下载
     */
    fun resume(): DownloadState {
        return copy(
            isPaused = false,
            lastUpdateTime = System.currentTimeMillis()
        )
    }

    /**
     * 设置错误信息
     */
    fun setError(error: String): DownloadState {
        return copy(
            errorMessage = error,
            lastUpdateTime = System.currentTimeMillis()
        )
    }

    /**
     * 清除错误信息
     */
    fun clearError(): DownloadState {
        return copy(
            errorMessage = null,
            lastUpdateTime = System.currentTimeMillis()
        )
    }

    /**
     * 获取下载进度百分比
     */
    fun getProgressPercentage(): Float {
        return if (totalBytes > 0) {
            (downloadedBytes.toFloat() / totalBytes.toFloat()) * 100f
        } else {
            0f
        }
    }
}
