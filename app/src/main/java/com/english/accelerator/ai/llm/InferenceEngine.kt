package com.english.accelerator.ai.llm

import android.content.Context
import com.english.accelerator.utils.AppLogger
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession.LlmInferenceSessionOptions
import com.google.mediapipe.tasks.genai.llminference.ProgressListener
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
    private lateinit var llmInferenceSession: LlmInferenceSession
    private val TAG = InferenceEngine::class.qualifiedName

    init {
        if (!modelExists()) {
            throw IllegalArgumentException("Model not found at path: ${config.modelPath}")
        }

        createEngine()
        createSession()
    }

    /**
     * Close and release resources
     */
    fun close() {
        llmInferenceSession.close()
        llmInference.close()
    }

    /**
     * Reset session (clear conversation history)
     */
    fun resetSession() {
        llmInferenceSession.close()
        createSession()
    }

    /**
     * Async streaming inference for chat
     */
    fun generateAsync(prompt: String, progressListener: ProgressListener<String>): ListenableFuture<String> {
        llmInferenceSession.addQueryChunk(prompt)
        return llmInferenceSession.generateResponseAsync(progressListener)
    }

    /**
     * Sync inference for grammar/writing services
     */
    fun generateSync(prompt: String): String {
        llmInferenceSession.addQueryChunk(prompt)
        return llmInferenceSession.generateResponse()
    }

    /**
     * Estimate remaining tokens in context
     */
    fun estimateTokensRemaining(context: String): Int {
        if (context.isEmpty()) return -1

        val sizeOfAllMessages = llmInferenceSession.sizeInTokens(context)
        val remainingTokens = config.maxTokens - sizeOfAllMessages - config.decodeTokenOffset
        return max(0, remainingTokens)
    }

    private fun createEngine() {
        val inferenceOptions = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(config.modelPath)
            .setMaxTokens(config.maxTokens)
            .apply { config.preferredBackend?.let { setPreferredBackend(it) } }
            .build()

        try {
            llmInference = LlmInference.createFromOptions(context, inferenceOptions)
            AppLogger.info(TAG, "LLM inference engine created successfully")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to create LLM inference engine: ${e.message}", e)
            throw ModelLoadFailException()
        }
    }

    private fun createSession() {
        val sessionOptions = LlmInferenceSessionOptions.builder()
            .setTemperature(config.temperature)
            .setTopK(config.topK)
            .setTopP(config.topP)
            .build()

        try {
            llmInferenceSession = LlmInferenceSession.createFromOptions(llmInference, sessionOptions)
            AppLogger.info(TAG, "LLM inference session created successfully")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to create LLM inference session: ${e.message}", e)
            throw ModelSessionCreateFailException()
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
class ModelSessionCreateFailException : Exception("Failed to create model session, please try again")
