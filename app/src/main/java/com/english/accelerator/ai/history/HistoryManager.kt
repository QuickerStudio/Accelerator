package com.english.accelerator.ai.history

import android.content.Context
import com.english.accelerator.ai.agent.Message
import com.english.accelerator.utils.AppLogger
import com.english.accelerator.utils.DConfig
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 历史记录管理器
 *
 * 功能：
 * - 保存和加载对话历史
 * - 按会话管理历史记录
 * - 历史记录查询和导出
 * - 自动清理过期历史
 */
class HistoryManager private constructor(context: Context) {

    private val configManager = DConfig.getInstance()
    private val histories = mutableMapOf<String, ConversationHistory>()

    private val _currentHistory = MutableStateFlow<ConversationHistory?>(null)
    val currentHistory: StateFlow<ConversationHistory?> = _currentHistory.asStateFlow()

    companion object {
        private const val TAG = "HistoryManager"
        private const val KEY_HISTORIES = "conversation_histories"
        private const val MAX_MESSAGES_PER_SESSION = 1000

        @Volatile
        private var instance: HistoryManager? = null

        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = HistoryManager(context.applicationContext)
                    }
                }
            }
        }

        fun getInstance(): HistoryManager {
            return instance ?: throw IllegalStateException(
                "HistoryManager not initialized. Call init() first."
            )
        }
    }

    init {
        AppLogger.info(TAG, "Initializing HistoryManager")
        loadHistories()
    }

    /**
     * 添加消息到指定会话的历史记录
     */
    fun addMessage(sessionId: String, message: Message) {
        val history = histories.getOrPut(sessionId) {
            ConversationHistory(sessionId = sessionId, messages = emptyList())
        }

        val updatedHistory = history.addMessage(message)
        histories[sessionId] = updatedHistory

        // 如果是当前会话，更新当前历史
        if (_currentHistory.value?.sessionId == sessionId) {
            _currentHistory.value = updatedHistory
        }

        // 限制消息数量
        if (updatedHistory.messages.size > MAX_MESSAGES_PER_SESSION) {
            trimHistory(sessionId)
        }

        saveHistories()
        AppLogger.debug(TAG, "Added message to session: $sessionId")
    }

    /**
     * 获取指定会话的历史记录
     */
    fun getHistory(sessionId: String): ConversationHistory? {
        return histories[sessionId]
    }

    /**
     * 获取指定会话的最后 N 条消息
     */
    fun getLastMessages(sessionId: String, count: Int): List<Message> {
        return histories[sessionId]?.getLastMessages(count) ?: emptyList()
    }

    /**
     * 切换到指定会话的历史记录
     */
    fun switchToHistory(sessionId: String) {
        _currentHistory.value = histories[sessionId]
        AppLogger.debug(TAG, "Switched to history: $sessionId")
    }

    /**
     * 清空指定会话的历史记录
     */
    fun clearHistory(sessionId: String) {
        val history = histories[sessionId]
        if (history != null) {
            histories[sessionId] = history.clear()

            if (_currentHistory.value?.sessionId == sessionId) {
                _currentHistory.value = histories[sessionId]
            }

            saveHistories()
            AppLogger.info(TAG, "Cleared history for session: $sessionId")
        }
    }

    /**
     * 删除指定会话的历史记录
     */
    fun deleteHistory(sessionId: String) {
        histories.remove(sessionId)

        if (_currentHistory.value?.sessionId == sessionId) {
            _currentHistory.value = null
        }

        saveHistories()
        AppLogger.info(TAG, "Deleted history for session: $sessionId")
    }

    /**
     * 修剪历史记录，保留最新的消息
     */
    private fun trimHistory(sessionId: String) {
        val history = histories[sessionId] ?: return
        val trimmedMessages = history.messages.takeLast(MAX_MESSAGES_PER_SESSION)

        histories[sessionId] = history.copy(
            messages = trimmedMessages,
            updatedAt = System.currentTimeMillis()
        )

        AppLogger.info(TAG, "Trimmed history for session: $sessionId")
    }

    /**
     * 导出指定会话的历史记录
     */
    fun exportHistory(sessionId: String): String? {
        val history = histories[sessionId] ?: return null

        return buildString {
            appendLine("会话 ID: $sessionId")
            appendLine("消息数量: ${history.messages.size}")
            appendLine("创建时间: ${history.createdAt}")
            appendLine("更新时间: ${history.updatedAt}")
            appendLine()
            appendLine("对话记录：")
            appendLine("=" .repeat(50))

            history.messages.forEach { message ->
                appendLine()
                appendLine("角色: ${message.role}")
                appendLine("时间: ${message.timestamp}")
                appendLine("内容: ${message.content}")
                appendLine("-".repeat(50))
            }
        }
    }

    /**
     * 获取所有会话的历史记录摘要
     */
    fun getAllHistoriesSummary(): Map<String, Int> {
        return histories.mapValues { it.value.getMessageCount() }
    }

    /**
     * 保存历史记录到持久化存储
     */
    private fun saveHistories() {
        try {
            configManager.putList(KEY_HISTORIES, histories.values.toList())
            AppLogger.debug(TAG, "Saved ${histories.size} conversation histories")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to save histories", e)
        }
    }

    /**
     * 从持久化存储加载历史记录
     */
    private fun loadHistories() {
        try {
            val type = object : TypeToken<List<ConversationHistory>>() {}.type
            val loadedHistories = configManager.getList<ConversationHistory>(KEY_HISTORIES, type)

            if (loadedHistories != null) {
                histories.clear()
                loadedHistories.forEach { history ->
                    histories[history.sessionId] = history
                }
                AppLogger.info(TAG, "Loaded ${histories.size} conversation histories")
            } else {
                AppLogger.info(TAG, "No saved histories found")
            }
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to load histories", e)
        }
    }

    /**
     * 清除所有历史记录
     */
    fun clearAllHistories() {
        histories.clear()
        _currentHistory.value = null
        saveHistories()
        AppLogger.info(TAG, "Cleared all histories")
    }
}
