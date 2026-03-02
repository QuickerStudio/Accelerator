package com.english.accelerator.ai

/**
 * Prompt templates for different LLM inference tasks
 */
object PromptTemplates {
    /**
     * Generate a prompt for grammar checking
     */
    fun grammarCheck(text: String): String = """
You are a grammar correction assistant. Analyze the following text and provide corrections.

Text: "$text"

Provide corrections in this exact format:
CORRECTIONS:
- [original phrase] → [corrected phrase]: [brief reason]

If no corrections needed, respond with: "No corrections needed."
""".trimIndent()

    /**
     * Generate a prompt for writing suggestions
     */
    fun writingSuggestion(text: String, context: String = ""): String = """
You are a writing improvement assistant. Suggest improvements for clarity, style, and impact.

Text: "$text"
${if (context.isNotEmpty()) "Context: $context" else ""}

Provide 2-3 specific suggestions in this format:
SUGGESTIONS:
1. [suggestion with example]
2. [suggestion with example]
""".trimIndent()

    /**
     * Generate a prompt for conversation practice
     */
    fun conversationPractice(scenario: String, userMessage: String): String = """
You are a conversation practice partner. Scenario: $scenario

User: "$userMessage"

Respond naturally and then provide feedback on:
1. Grammar accuracy
2. Natural phrasing
3. Cultural appropriateness

Format:
RESPONSE: [your response]
FEEDBACK: [constructive feedback]
""".trimIndent()
}
