package com.english.accelerator.ai.llm

import java.io.File

/**
 * Configuration for LLM inference engine
 */
data class InferenceConfig(
    val modelPath: String,
    val maxTokens: Int = 2048,
    val decodeTokenOffset: Int = 256,
    val temperature: Float = 0.3f,
    val topK: Int = 40,
    val topP: Float = 0.95f
) {
    companion object {
        /**
         * Create config for Gemma 3N E2B INT4 model
         */
        fun forGemma3N(context: android.content.Context): InferenceConfig {
            val modelPath = File(context.filesDir, "models/gemma-3n-e2b-it-int4.litertlm").absolutePath
            return InferenceConfig(
                modelPath = modelPath,
                maxTokens = 2048,
                decodeTokenOffset = 256,
                temperature = 0.3f,
                topK = 40,
                topP = 0.95f
            )
        }
    }
}
