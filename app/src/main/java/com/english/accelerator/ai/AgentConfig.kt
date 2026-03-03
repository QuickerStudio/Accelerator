package com.english.accelerator.ai

/**
 * Configuration for an AI agent including its system prompt and generation parameters.
 */
data class AgentConfig(
    val role: AgentRole,
    val systemPrompt: String,
    val temperature: Float = role.defaultTemperature,
    val maxTokens: Int = role.defaultMaxTokens
)
