package com.english.accelerator.ai.agent

import com.english.accelerator.ai.model.GemmaInferenceManager
import com.english.accelerator.ai.inference.InferenceResult
import com.english.accelerator.ai.inference.SuggestionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of AgentService that manages AI agent roles and generates responses.
 * Uses GemmaInferenceManager for LLM inference with different system prompts.
 */
class AgentServiceImpl(
    private val inferenceManager: GemmaInferenceManager
) : AgentService {

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

    override suspend fun generate(
        userInput: String,
        context: List<Message>
    ): Result<String> = withContext(Dispatchers.Default) {
        try {
            val systemPrompt = getCurrentPrompt()
            val messages = buildContext(systemPrompt, context, userInput)
            val promptString = buildPromptString(messages)

            // Use inference manager to generate response
            val response = inferenceManager.generateResponse(promptString)

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    /**
     * Generate response using the inference manager
     */
    private suspend fun GemmaInferenceManager.generateResponse(prompt: String): String {
        // For now, use the existing generateSuggestions method
        // This will be refined when we integrate properly
        val result = generateSuggestions(prompt, SuggestionType.CONVERSATION_PRACTICE)
        return when (result) {
            is InferenceResult.Success -> result.rawResponse
            is InferenceResult.Error -> throw Exception(result.message)
            InferenceResult.Loading -> throw Exception("Inference is still loading")
        }
    }
}
