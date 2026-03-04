package com.english.accelerator.ai.llm

/**
 * Represents a grammar suggestion with correction details
 */
data class GrammarSuggestion(
    val original: String,
    val corrected: String,
    val reason: String,
    val startIndex: Int = -1,
    val endIndex: Int = -1
)

/**
 * Types of suggestions the LLM can provide
 */
enum class SuggestionType {
    GRAMMAR_CHECK,
    WRITING_IMPROVEMENT,
    CONVERSATION_PRACTICE
}
