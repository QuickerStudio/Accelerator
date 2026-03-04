package com.english.accelerator.ai.prompt

/**
 * Grammar checking prompt templates
 */
object GrammarPrompts {
    /**
     * Generate grammar check prompt
     */
    fun grammarCheck(text: String): String {
        return """
You are an English grammar expert. Analyze the following text and identify grammar errors.

For each error, provide:
ERROR: [brief description of the error type]
ORIGINAL: [the incorrect text]
CORRECTION: [the corrected text]
EXPLANATION: [why it's wrong and how to fix it]

Text to analyze:
$text

Provide your analysis:
        """.trimIndent()
    }
}
