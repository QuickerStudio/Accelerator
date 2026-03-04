package com.english.accelerator.ai.agent

import android.content.Context
import com.english.accelerator.ai.llm.InferenceEngine
import com.english.accelerator.ai.llm.InferenceConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of AgentService that manages AI agent roles and generates responses.
 * Uses InferenceEngine (official MediaPipe architecture) for LLM inference with different system prompts.
 */
class AgentServiceImpl(
    private val context: Context
) : AgentService {

    private val inferenceEngine: InferenceEngine by lazy {
        val config = InferenceConfig.forGemma3N(context)
        InferenceEngine.getInstance(context, config)
    }

    private var currentAgent: AgentRole = AgentRole.VOCABULARY_TUTOR
    private var promptMode: PromptMode = PromptMode.PRESET
    private var customPrompt: String = ""

    override fun getCurrentAgent(): AgentRole = currentAgent

    override suspend fun switchAgent(agent: AgentRole): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            currentAgent = agent
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentPrompt(): String {
        return when (promptMode) {
            PromptMode.PRESET -> getPresetPrompt(currentAgent)
            PromptMode.CUSTOM -> customPrompt.ifEmpty { getPresetPrompt(currentAgent) }
        }
    }

    override suspend fun updateCustomPrompt(prompt: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            customPrompt = prompt
            promptMode = PromptMode.CUSTOM
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetToPreset(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            promptMode = PromptMode.PRESET
            customPrompt = ""
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generate response with streaming support
     * @param userInput User's input message
     * @param context Conversation history
     * @param onPartialResult Callback for each generated token chunk
     * @return Result containing the complete response
     */
    suspend fun generateStreaming(
        userInput: String,
        context: List<Message>,
        onPartialResult: (String, Boolean) -> Unit
    ): Result<String> = withContext(Dispatchers.Default) {
        try {
            val systemPrompt = getCurrentPrompt()
            val messages = buildContext(systemPrompt, context, userInput)
            val promptString = buildPromptString(messages)

            // Use InferenceEngine for async streaming inference
            val rawResponse = inferenceEngine.generateAsync(promptString) { partialResult, done ->
                // Clean partial result and pass to callback
                val cleaned = cleanResponse(partialResult)
                onPartialResult(cleaned, done)
            }

            // Clean up final response
            val cleanedResponse = cleanResponse(rawResponse)

            Result.success(cleanedResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Clean up model response by removing special tokens and stop sequences
     */
    private fun cleanResponse(response: String): String {
        var cleaned = response

        // Remove common stop tokens and special sequences
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

        // Find the first occurrence of any stop token
        var firstStopIndex = cleaned.length
        for (token in stopTokens) {
            val index = cleaned.indexOf(token)
            if (index != -1 && index < firstStopIndex) {
                firstStopIndex = index
            }
        }

        // Truncate at the first stop token
        if (firstStopIndex < cleaned.length) {
            cleaned = cleaned.substring(0, firstStopIndex)
        }

        // Remove any trailing whitespace
        cleaned = cleaned.trim()

        return cleaned
    }

    /**
     * Build conversation context with system prompt and history
     */
    private fun buildContext(
        systemPrompt: String,
        context: List<Message>,
        userInput: String
    ): List<Message> {
        val messages = mutableListOf<Message>()

        // Add system prompt
        messages.add(Message(role = "system", content = systemPrompt))

        // Add compressed context if needed
        val compressedContext = compressContext(context)
        messages.addAll(compressedContext)

        // Add current user input
        messages.add(Message(role = "user", content = userInput))

        return messages
    }

    /**
     * Compress context to keep only recent messages
     */
    private fun compressContext(messages: List<Message>): List<Message> {
        // Keep last 10 messages (5 turns) to maintain context
        return if (messages.size > 10) {
            messages.takeLast(10)
        } else {
            messages
        }
    }

    /**
     * Build prompt string in the format expected by the LLM
     */
    private fun buildPromptString(messages: List<Message>): String {
        return messages.joinToString("\n") { message ->
            "<|im_start|>${message.role}\n${message.content}\n<|im_end|>"
        } + "\n<|im_start|>assistant\n"
    }

    /**
     * Get the preset system prompt for a given agent role
     */
    private fun getPresetPrompt(role: AgentRole): String {
        return AgentPrompts.getPrompt(role)
    }
}
