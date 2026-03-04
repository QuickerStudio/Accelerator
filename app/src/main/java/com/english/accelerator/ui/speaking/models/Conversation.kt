package com.english.accelerator.ui.speaking.models

/**
 * 对话会话数据模型 - 用于历史记录显示
 */
data class Conversation(
    val id: String,
    val title: String,
    val timestamp: Long,
    val messageCount: Int,
    val preview: String
)
