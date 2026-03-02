package com.english.accelerator.ai

/**
 * Sealed class representing the result of an LLM inference operation
 */
sealed class InferenceResult {
    /**
     * Successful inference with suggestions
     */
    data class Success(
        val suggestions: List<GrammarSuggestion>,
        val rawResponse: String
    ) : InferenceResult()

    /**
     * Inference failed with an error
     */
    data class Error(val message: String) : InferenceResult()

    /**
     * Inference is in progress
     */
    object Loading : InferenceResult()
}
