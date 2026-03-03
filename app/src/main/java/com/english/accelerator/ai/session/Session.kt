package com.english.accelerator.ai.session

/**
 * 会话数据类
 *
 * 表示一个对话会话
 */
data class Session(
    val id: String,
    val title: String,
    val type: Type,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    enum class Type {
        CONVERSATION,
        VOCABULARY,
        WRITING
    }
}
