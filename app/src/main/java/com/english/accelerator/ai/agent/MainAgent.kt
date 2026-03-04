/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ai.agent

import android.content.Context
import com.english.accelerator.ai.llm.InferenceConfig
import com.english.accelerator.ai.llm.InferenceEngine
import com.english.accelerator.utils.AppLogger

/**
 * Message for conversation context
 */
data class Message(
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 主 Agent - 负责推理逻辑和提示词构建
 */
class MainAgent(private val context: Context) {

    private val TAG = "MainAgent"
    private val engine: InferenceEngine by lazy {
        val config = InferenceConfig.forGemma3N(context)
        InferenceEngine.getInstance(context, config)
    }

    /**
     * 执行推理
     */
    suspend fun execute(
        systemPrompt: String,
        history: List<Message>,
        userInput: String,
        onPartialResult: (String, Boolean) -> Unit
    ): Result<String> {
        // 构建完整提示词
        val fullPrompt = buildPrompt(systemPrompt, history, userInput)

        // 检查 token 限制
        val tokensRemaining = engine.estimateTokensRemaining(fullPrompt)
        if (tokensRemaining < 100) {
            return Result.failure(Exception("Context too long, remaining tokens: $tokensRemaining"))
        }

        // 执行推理
        return engine.generateAsync(fullPrompt) { partial, done ->
            val cleaned = cleanResponse(partial)
            onPartialResult(cleaned, done)
        }.map { cleanResponse(it) }
    }

    /**
     * 构建提示词
     */
    private fun buildPrompt(
        systemPrompt: String,
        history: List<Message>,
        userInput: String
    ): String {
        val messages = mutableListOf<Message>()

        // 添加系统提示词
        messages.add(Message(role = "system", content = systemPrompt))

        // 添加压缩后的历史记录（保留最近 10 条）
        val compressedHistory = if (history.size > 10) {
            history.takeLast(10)
        } else {
            history
        }
        messages.addAll(compressedHistory)

        // 添加当前用户输入
        messages.add(Message(role = "user", content = userInput))

        // 格式化为模型期望的格式
        return messages.joinToString("\n") { message ->
            "<|im_start|>${message.role}\n${message.content}\n<|im_end|>"
        } + "\n<|im_start|>assistant\n"
    }

    /**
     * 清理响应
     */
    private fun cleanResponse(response: String): String {
        var cleaned = response

        val stopTokens = listOf(
            "<|im_end|>",
            "<|im_start|>",
            "<end_of_turn>",
            "<llm_end>",
            "<eos>",
            "</s>",
            "[INST]",
            "[/INST]"
        )

        var firstStopIndex = cleaned.length
        for (token in stopTokens) {
            val index = cleaned.indexOf(token)
            if (index != -1 && index < firstStopIndex) {
                firstStopIndex = index
            }
        }

        if (firstStopIndex < cleaned.length) {
            cleaned = cleaned.substring(0, firstStopIndex)
        }

        return cleaned.trim()
    }

    /**
     * 检查引擎是否就绪
     */
    fun isReady(): Boolean = engine.isReady()
}
