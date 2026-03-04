package com.english.accelerator.ai.service

import android.content.Context
import com.english.accelerator.ai.core.InferenceConfig
import com.english.accelerator.ai.core.InferenceEngine
import com.english.accelerator.utils.AppLogger
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.ProgressListener

/**
 * Chat service using async streaming inference
 * Provides real-time streaming responses for conversational AI
 */
class ChatService private constructor(
    private val context: Context,
    private val engine: InferenceEngine
) {
    private val TAG = ChatService::class.qualifiedName

    /**
     * Generate async streaming response for chat
     * @param prompt User message
     * @param progressListener Callback for streaming text chunks
     * @return Future with complete response
     */
    fun generateAsync(
        prompt: String,
        progressListener: ProgressListener<String>
    ): ListenableFuture<String> {
        AppLogger.debug(TAG, "Generating async response for prompt: ${prompt.take(50)}...")
        return engine.generateAsync(prompt, progressListener)
    }

    /**
     * Reset conversation session (clear history)
     */
    fun resetSession() {
        AppLogger.info(TAG, "Resetting chat session")
        engine.resetSession()
    }

    /**
     * Estimate remaining tokens in context
     */
    fun estimateTokensRemaining(context: String): Int {
        return engine.estimateTokensRemaining(context)
    }

    companion object {
        @Volatile
        private var instance: ChatService? = null

        fun getInstance(context: Context): ChatService {
            return instance ?: synchronized(this) {
                instance ?: run {
                    val config = InferenceConfig.forGemma3N(context)
                    val engine = InferenceEngine.getInstance(context, config)
                    ChatService(context, engine).also { instance = it }
                }
            }
        }
    }
}
