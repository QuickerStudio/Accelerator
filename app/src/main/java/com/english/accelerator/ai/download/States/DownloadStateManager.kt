package com.english.accelerator.ai.download.States

import android.content.Context
import com.english.accelerator.utils.AppLogger
import java.io.File

/**
 * 下载状态管理器
 *
 * 管理下载状态的持久化和恢复
 * 这是内部记忆系统，用于断点续传
 */
class DownloadStateManager(private val context: Context) {

    private val stateDir = File(context.filesDir, "download_states")
    private var currentState: DownloadState? = null

    companion object {
        private const val TAG = "DownloadStateManager"
    }

    init {
        stateDir.mkdirs()
        loadState()
    }

    /**
     * 加载保存的状态
     */
    private fun loadState() {
        currentState = DownloadState.load(stateDir)
        if (currentState != null) {
            AppLogger.info(TAG, "Loaded download state: ${currentState?.downloadedBytes} / ${currentState?.totalBytes} bytes")
        } else {
            AppLogger.info(TAG, "No saved download state found")
        }
    }

    /**
     * 获取当前状态
     */
    fun getState(): DownloadState? = currentState

    /**
     * 初始化新的下载状态
     */
    fun initializeDownload(modelPath: String, totalBytes: Long, downloadRoute: String) {
        currentState = DownloadState.create(modelPath, totalBytes, downloadRoute)
        saveState()
        AppLogger.info(TAG, "Initialized new download state for $modelPath")
    }

    /**
     * 更新下载进度
     */
    fun updateProgress(downloadedBytes: Long) {
        currentState = currentState?.updateProgress(downloadedBytes)
        saveState()
    }

    /**
     * 标记下载完成
     */
    fun markComplete() {
        currentState = currentState?.markComplete()
        saveState()
        AppLogger.info(TAG, "Download marked as complete")
    }

    /**
     * 标记为暂停
     */
    fun markPaused() {
        currentState = currentState?.markPaused()
        saveState()
        AppLogger.info(TAG, "Download marked as paused")
    }

    /**
     * 恢复下载
     */
    fun resume() {
        currentState = currentState?.resume()
        saveState()
        AppLogger.info(TAG, "Download resumed")
    }

    /**
     * 设置错误信息
     */
    fun setError(error: String) {
        currentState = currentState?.setError(error)
        saveState()
        AppLogger.error(TAG, "Download error: $error", null)
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        currentState = currentState?.clearError()
        saveState()
    }

    /**
     * 清除状态
     */
    fun clearState() {
        currentState = null
        val stateFile = File(stateDir, "download_state.json")
        if (stateFile.exists()) {
            stateFile.delete()
        }
        AppLogger.info(TAG, "Download state cleared")
    }

    /**
     * 保存状态到文件
     */
    private fun saveState() {
        currentState?.save(stateDir)
    }

    /**
     * 检查是否可以恢复下载
     */
    fun canResumeDownload(): Boolean {
        val state = currentState ?: return false
        return !state.isComplete && state.downloadedBytes > 0
    }

    /**
     * 获取已下载的字节数
     */
    fun getDownloadedBytes(): Long {
        return currentState?.downloadedBytes ?: 0L
    }
}
