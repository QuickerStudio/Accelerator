package com.english.accelerator.ai.agent

/**
 * Service for managing AI agent roles and generating responses.
 * Supports switching between different agent personalities via system prompts.
 */
interface AgentService {
    /**
     * Get the current active agent role
     */
    fun getCurrentAgent(): AgentRole

    /**
     * Switch to a different agent role
     */
    suspend fun switchAgent(agent: AgentRole): Result<Unit>

    /**
     * Get the current system prompt being used
     */
    fun getCurrentPrompt(): String

    /**
     * Update the custom system prompt
     */
    suspend fun updateCustomPrompt(prompt: String): Result<Unit>

    /**
     * Reset to the preset system prompt for the current agent
     */
    suspend fun resetToPreset(): Result<Unit>

    /**
     * Generate a response using the current agent
     * @param userInput The user's input message
     * @param context Previous conversation messages for context
     */
    suspend fun generate(
        userInput: String,
        context: List<Message> = emptyList()
    ): Result<String>
}
