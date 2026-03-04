package com.english.accelerator.ai.agent

import android.content.Context
import com.english.accelerator.ai.llm.InferenceEngine
import com.english.accelerator.ai.llm.InferenceConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Thread Title Agent - Generates concise conversation titles
 *
 * This agent analyzes the user's first message and generates a short,
 * descriptive title (max 10 characters) for the conversation thread.
 */
class ThreadTitleAgent(private val context: Context) {

    private val inferenceEngine: InferenceEngine by lazy {
        val config = InferenceConfig.forGemma3N(context)
        InferenceEngine.getInstance(context, config)
    }

    /**
     * Generate a thread title based on the user's first message
     * @param userMessage The user's first message in the conversation
     * @return A concise title (max 10 characters)
     */
    suspend fun generateTitle(userMessage: String): Result<String> = withContext(Dispatchers.Default) {
        try {
            val systemPrompt = """
You are a thread title generator. Your task is to create a very short, concise title for a conversation based on the user's first message.

Rules:
- Maximum 10 characters (including spaces)
- Use Chinese or English based on the user's message language
- Be descriptive but extremely brief
- Focus on the main topic or intent
- No punctuation marks
- Examples:
  - "学习英语" (for English learning)
  - "日常对话" (for daily conversation)
  - "语法问题" (for grammar questions)
  - "单词练习" (for vocabulary practice)
  - "English" (for general English topics)

Respond with ONLY the title, nothing else.
""".trimIndent()

            val prompt = buildPromptString(systemPrompt, userMessage)
            val rawTitle = inferenceEngine.generateSync(prompt)

            // Clean and validate the title
            val cleanedTitle = cleanTitle(rawTitle)

            Result.success(cleanedTitle)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Clean and validate the generated title
     */
    private fun cleanTitle(title: String): String {
        var cleaned = title.trim()

        // Remove common stop tokens
        val stopTokens = listOf(
            "<|im_end|>",
            "<|im_start|>",
            "<end_of_turn>",
            "<eos>",
            "</s>"
        )

        for (token in stopTokens) {
            cleaned = cleaned.replace(token, "")
        }

        // Remove quotes if present
        cleaned = cleaned.trim('"', '\'', ' ')

        // Limit to 10 characters
        if (cleaned.length > 10) {
            cleaned = cleaned.substring(0, 10)
        }

        // Fallback if empty
        if (cleaned.isEmpty()) {
            cleaned = "新对话"
        }

        return cleaned
    }

    /**
     * Build prompt string in the format expected by the LLM
     */
    private fun buildPromptString(systemPrompt: String, userMessage: String): String {
        return """
<|im_start|>system
$systemPrompt
<|im_end|>
<|im_start|>user
$userMessage
<|im_end|>
<|im_start|>assistant
""".trimIndent()
    }
}
