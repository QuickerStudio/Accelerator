package com.english.accelerator.ai.llm

import android.content.Context
import com.english.accelerator.utils.AppLogger
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.ProgressListener
import java.io.File
import kotlin.math.max
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

    // Mutex to prevent concurrent inference calls
    private val inferenceMutex = Mutex()

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
     * Thread-safe: uses mutex to prevent concurrent calls
     */
    suspend fun generateSync(prompt: String): String {
        return inferenceMutex.withLock {
            try {
                llmInference.generateResponse(prompt) ?: ""
            } catch (e: Exception) {
                AppLogger.error(TAG, "Failed to generate response: ${e.message}", e)
                throw e
            }
        }
    }

    /**
     * Async streaming inference - generates response token by token
     * Thread-safe: uses mutex to prevent concurrent calls
     * @param prompt The input prompt
     * @param onPartialResult Callback for each generated token chunk (partialResult, done)
     * @return The complete generated response
     */
    suspend fun generateAsync(
        prompt: String,
        onPartialResult: (String, Boolean) -> Unit
    ): String = inferenceMutex.withLock {
        suspendCancellableCoroutine { continuation ->
            try {
                var isCompleted = false

                val progressListener = ProgressListener<String> { partialResult, done ->
                    try {
                        onPartialResult(partialResult, done)

                        // Mark as completed when done=true
                        if (done) {
                            isCompleted = true
                        }
                    } catch (e: Exception) {
                        AppLogger.error(TAG, "Error in progress listener: ${e.message}", e)
                    }
                }

                val future = llmInference.generateResponseAsync(prompt, progressListener)

                future.addListener({
                    try {
                        val result = future.get() ?: ""

                        // Ensure we call the callback one final time with done=true
                        if (!isCompleted) {
                            onPartialResult(result, true)
                        }

                        continuation.resume(result)
                    } catch (e: Exception) {
                        AppLogger.error(TAG, "Failed to generate async response: ${e.message}", e)
                        continuation.resumeWithException(e)
                    }
                }, { it.run() })

                continuation.invokeOnCancellation {
                    future.cancel(true)
                }
            } catch (e: Exception) {
                AppLogger.error(TAG, "Failed to start async inference: ${e.message}", e)
                continuation.resumeWithException(e)
            }
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
            AppLogger.info(TAG, "LLM inference engine created successfully with maxTokens=${config.maxTokens}")
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
