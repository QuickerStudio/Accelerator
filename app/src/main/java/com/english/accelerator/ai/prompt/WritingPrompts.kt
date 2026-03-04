package com.english.accelerator.ai.prompt

/**
 * Writing improvement prompt templates
 */
object WritingPrompts {
    /**
     * Generate writing improvement prompt
     */
    fun writingSuggestion(text: String): String {
        return """
You are an English writing coach. Analyze the following text and suggest improvements for clarity, style, and effectiveness.

For each suggestion, provide:
SUGGESTION: [type of improvement: vocabulary, sentence structure, clarity, etc.]
ORIGINAL: [the original text]
IMPROVED: [the improved version]
REASON: [why this improvement makes the text better]

Text to improve:
$text

Provide your suggestions:
        """.trimIndent()
    }
}
