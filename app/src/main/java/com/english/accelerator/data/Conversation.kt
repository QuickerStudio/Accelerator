/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.data

/**
 * Represents a single turn in a conversation with the AI.
 * Used for storing conversation history in the speaking screen.
 */
data class ConversationTurn(
    val id: Long,
    val role: Role,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class Role {
        USER,
        ASSISTANT
    }
}

/**
 * Manager for conversation history in the speaking screen.
 * Stores conversation turns in memory.
 */
object ConversationManager {
    private val conversations = mutableListOf<ConversationTurn>()
    private var nextId = 1L

    /**
     * Get all conversation turns sorted by timestamp
     */
    fun getAllConversations(): List<ConversationTurn> {
        return conversations.sortedBy { it.timestamp }
    }

    /**
     * Add a new conversation turn
     */
    fun addConversation(role: ConversationTurn.Role, content: String): ConversationTurn {
        val turn = ConversationTurn(
            id = nextId++,
            role = role,
            content = content
        )
        conversations.add(turn)
        return turn
    }

    /**
     * Delete all conversation history
     */
    fun deleteAll() {
        conversations.clear()
    }

    /**
     * Get a specific conversation turn by ID
     */
    fun getConversationById(id: Long): ConversationTurn? {
        return conversations.find { it.id == id }
    }

    /**
     * Get the last N conversation turns
     */
    fun getRecentConversations(count: Int): List<ConversationTurn> {
        return conversations.sortedBy { it.timestamp }.takeLast(count)
    }
}
