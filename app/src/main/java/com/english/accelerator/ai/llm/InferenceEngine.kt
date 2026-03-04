package com.english.accelerator.ai.llm

import android.content.Context
import com.english.accelerator.utils.AppLogger
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.ProgressListener
import java.io.File
import kotlin.math.max
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Configuration for LLM inference engine
 */
data class InferenceConfig(
    val modelPath: String,
    val maxTokens: Int = 2048,
    val decodeTokenOffset: Int = 256
) {
    companion object {
        fun forGemma3N(context: Context): InferenceConfig {
            val modelPath = File(context.filesDir, "models/gemma-3n-e2b-it-int4.litertlm").absolutePath
            return InferenceConfig(
                modelPath = modelPath,
                maxTokens = 2048,
                decodeTokenOffset = 256
            )
        }
    }
}

/**
 * LLM 推理引擎 - 专注核心功能
 *
 * 职责：
 * - 模型加载和初始化
 * - 异步流式推理
 * - Token 计数
 * - 线程安全
 */
class InferenceEngine private constructor(
    private val context: Context,
    private val config: InferenceConfig
) {
    private lateinit var llmInference: LlmInference
    private val TAG = "InferenceEngine"
    private val inferenceMutex = Mutex()

    init {
        if (!modelExists()) {
            throw ModelLoadFailException("Model not found at: ${config.modelPath}")
        }
        createEngine()
    }

    /**
     * 异步流式推理 - 唯一的推理方法
     */
    suspend fun generateAsync(
        prompt: String,
        onPartialResult: (String, Boolean) -> Unit
    ): Result<String> = inferenceMutex.withLock {
        suspendCancellableCoroutine { continuation ->
            try {
                var isCompleted = false

                val progressListener = ProgressListener<String> { partialResult, done ->
                    try {
                        onPartialResult(partialResult, done)
                        if (done) isCompleted = true
                    } catch (e: Exception) {
                        AppLogger.error(TAG, "Error in progress listener: ${e.message}", e)
                    }
                }

                val future = llmInference.generateResponseAsync(prompt, progressListener)

                future.addListener({
                    try {
                        val result = future.get() ?: ""

                        if (!isCompleted) {
                            onPartialResult(result, true)
                        }

                        continuation.resume(Result.success(result))
                    } catch (e: Exception) {
                        AppLogger.error(TAG, "Async generation failed: ${e.message}", e)
                        continuation.resume(Result.failure(e))
                    }
                }, { it.run() })

                continuation.invokeOnCancellation {
                    future.cancel(true)
                }
            } catch (e: Exception) {
                AppLogger.error(TAG, "Failed to start async inference: ${e.message}", e)
                continuation.resume(Result.failure(e))
            }
        }
    }

    /**
     * 估算剩余 token 数量
     */
    fun estimateTokensRemaining(prompt: String): Int {
        if (prompt.isEmpty()) return config.maxTokens

        val usedTokens = llmInference.sizeInTokens(prompt)
        val remainingTokens = config.maxTokens - usedTokens - config.decodeTokenOffset
        return max(0, remainingTokens)
    }

    /**
     * 检查引擎是否就绪
     */
    fun isReady(): Boolean = ::llmInference.isInitialized

    /**
     * 关闭并释放资源
     */
    fun close() {
        if (::llmInference.isInitialized) {
            llmInference.close()
        }
    }

    private fun createEngine() {
        val inferenceOptions = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(config.modelPath)
            .setMaxTokens(config.maxTokens)
            .build()

        try {
            llmInference = LlmInference.createFromOptions(context, inferenceOptions)
            AppLogger.info(TAG, "InferenceEngine initialized: maxTokens=${config.maxTokens}")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to create engine: ${e.message}", e)
            throw ModelLoadFailException("Failed to load model")
        }
    }

    private fun modelExists(): Boolean = File(config.modelPath).exists()

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

class ModelLoadFailException(message: String) : Exception(message)
