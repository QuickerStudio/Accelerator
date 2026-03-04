package com.english.accelerator.ai.agent

import android.content.Context
import com.english.accelerator.ai.llm.InferenceEngine
import com.english.accelerator.ai.llm.InferenceConfig
import com.english.accelerator.utils.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Thread Title Agent - Generates concise conversation titles
 *
 * This agent analyzes the user's first message and generates a short,
 * descriptive title (max 10 characters) for the conversation thread.
 * Uses async streaming to avoid blocking the main conversation inference.
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
你是一个对话标题生成器。根据用户的第一条消息，生成一个非常简短的标题。

规则：
- 最多10个字符（包括空格）
- 根据用户消息的语言使用中文或英文
- 要简洁但有描述性
- 聚焦主要话题或意图
- 不要标点符号
- 示例：
  - "学习英语"
  - "日常对话"
  - "语法问题"
  - "单词练习"
  - "English"

只返回标题，不要其他内容。
""".trimIndent()

            val prompt = buildPromptString(systemPrompt, userMessage)

            // Use async streaming to avoid blocking main conversation
            var titleResult = ""
            inferenceEngine.generateAsync(prompt) { partialResult, done ->
                if (done) {
                    titleResult = partialResult
                }
            }

            // Clean and validate the title
            val cleanedTitle = cleanTitle(titleResult)

            AppLogger.info("ThreadTitleAgent", "Generated title: $cleanedTitle")
            Result.success(cleanedTitle)
        } catch (e: Exception) {
            AppLogger.error("ThreadTitleAgent", "Failed to generate title: ${e.message}", e)
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
