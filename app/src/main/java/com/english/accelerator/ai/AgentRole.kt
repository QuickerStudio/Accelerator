package com.english.accelerator.ai

/**
 * Defines the different AI agent roles available in the application.
 * Each role has specific capabilities and behavior defined by its system prompt.
 */
enum class AgentRole(
    val displayName: String,
    val defaultTemperature: Float,
    val defaultMaxTokens: Int
) {
    VOCABULARY_TUTOR(
        displayName = "单词学习助手",
        defaultTemperature = 0.7f,
        defaultMaxTokens = 512
    ),
    GRAMMAR_CHECKER(
        displayName = "语法检查助手",
        defaultTemperature = 0.3f,
        defaultMaxTokens = 1024
    ),
    ESSAY_REVIEWER(
        displayName = "作文批改老师",
        defaultTemperature = 0.5f,
        defaultMaxTokens = 2048
    ),
    SPEAKING_PARTNER(
        displayName = "口语陪练伙伴",
        defaultTemperature = 0.8f,
        defaultMaxTokens = 512
    ),
    LEARNING_PLANNER(
        displayName = "学习规划师",
        defaultTemperature = 0.5f,
        defaultMaxTokens = 1024
    )
}
