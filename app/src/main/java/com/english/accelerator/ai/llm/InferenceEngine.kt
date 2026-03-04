package com.english.accelerator.ai.llm

import android.content.Context
import com.english.accelerator.utils.AppLogger
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File
import kotlin.math.max

/**
 * Unified LLM inference engine for offline use
 * Based on official MediaPipe LLM inference architecture (Engine + Session)
 *
 * Provides two inference modes:
 * - generateAsync(): Async streaming inference for chat
 * - generateSync(): Sync inference for grammar/writing services
 */
class InferenceEngine private constructor(
    private val context: Context,
    private val config: InferenceConfig
) {
    private lateinit var llmInference: LlmInference
    private val TAG: String = InferenceEngine::class.qualifiedName ?: "InferenceEngine"

    init {
        if (!modelExists()) {
            throw IllegalArgumentException("Model not found at path: ${config.modelPath}")
        }

        createEngine()
    }

    /**
     * Close and release resources
     */
    fun close() {
        llmInference.close()
    }

    /**
     * Sync inference - the only inference method
     */
    fun generateSync(prompt: String): String {
        try {
            return llmInference.generateResponse(prompt) ?: ""
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to generate response: ${e.message}", e)
            throw e
        }
    }

    /**
     * Estimate remaining tokens in context
     */
    fun estimateTokensRemaining(context: String): Int {
        if (context.isEmpty()) return -1

        val sizeOfAllMessages = llmInference.sizeInTokens(context)
        val remainingTokens = config.maxTokens - sizeOfAllMessages - config.decodeTokenOffset
        return max(0, remainingTokens)
    }

    /**
     * Check if model is loaded and ready
     */
    fun isReady(): Boolean {
        return ::llmInference.isInitialized
    }

    private fun createEngine() {
        val inferenceOptions = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(config.modelPath)
            .setMaxTokens(config.maxTokens)
            .build()

        try {
            llmInference = LlmInference.createFromOptions(context, inferenceOptions)
            AppLogger.info(TAG, "LLM inference engine created successfully")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to create LLM inference engine: ${e.message}", e)
            throw ModelLoadFailException()
        }
    }

    private fun modelExists(): Boolean {
        return File(config.modelPath).exists()
    }

    companion object {
        @Volatile
        private var instance: InferenceEngine? = null

        fun getInstance(context: Context, config: InferenceConfig): InferenceEngine {
            return instance ?: synchronized(this) {
                instance ?: InferenceEngine(context, config).also { instance = it }
            }
        }

        fun resetInstance(context: Context, config: InferenceConfig): InferenceEngine {
            instance?.close()
            return InferenceEngine(context, config).also { instance = it }
        }
    }
}

class ModelLoadFailException : Exception("Failed to load model, please try again")

/**
 * Model state for UI observation
 */
sealed class ModelState {
    object Idle : ModelState()
    object Loading : ModelState()
    object Ready : ModelState()
    data class Error(val message: String) : ModelState()
}
