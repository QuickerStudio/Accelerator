package com.english.accelerator.ai.llm

import com.google.mediapipe.tasks.genai.llminference.LlmInference.Backend
import java.io.File

/**
 * Model configuration for local LLM inference
 * NB: Make sure the filename is *unique* per model you use!
 * Weight caching is currently based on filename alone.
 */
enum class Model(
    val fileName: String,
    val preferredBackend: Backend?,
    val thinking: Boolean,
    val temperature: Float,
    val topK: Int,
    val topP: Float,
) {
    GEMMA_3N_E2B_IT_INT4(
        fileName = "gemma-3n-e2b-it-int4.litertlm",
        preferredBackend = Backend.GPU,
        thinking = false,
        temperature = 0.3f,
        topK = 40,
        topP = 0.95f
    );

    /**
     * Get the full path to the model file in internal storage
     */
    fun getPath(context: android.content.Context): String {
        return File(context.filesDir, "models/$fileName").absolutePath
    }
}
