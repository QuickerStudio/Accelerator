package com.english.accelerator.ai.agent

/**
 * Represents a single message in a conversation with the AI.
 * Used for building conversation context and prompt strings.
 */
data class Message(
    val role: String,  // "system", "user", or "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
