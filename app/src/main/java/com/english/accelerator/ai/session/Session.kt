/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
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
