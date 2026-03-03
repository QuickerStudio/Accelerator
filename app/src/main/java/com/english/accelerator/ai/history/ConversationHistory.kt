package com.english.accelerator.ai.history

import com.english.accelerator.ai.agent.Message

/**
 * 对话历史数据模型
 *
 * 表示一个会话中的完整对话历史
 */
data class ConversationHistory(
    val sessionId: String,
    val messages: List<Message>,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * 添加消息到历史记录
     */
    fun addMessage(message: Message): ConversationHistory {
        return copy(
            messages = messages + message,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * 获取最后 N 条消息
     */
    fun getLastMessages(count: Int): List<Message> {
        return messages.takeLast(count)
    }

    /**
     * 获取消息总数
     */
    fun getMessageCount(): Int {
        return messages.size
    }

    /**
     * 清空历史记录
     */
    fun clear(): ConversationHistory {
        return copy(
            messages = emptyList(),
            updatedAt = System.currentTimeMillis()
        )
    }
}
