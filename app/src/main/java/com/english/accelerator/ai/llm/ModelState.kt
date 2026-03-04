package com.english.accelerator.ai.llm

/**
 * Represents the state of the AI model
 */
sealed class ModelState {
    object Idle : ModelState()
    object Loading : ModelState()
    object Ready : ModelState()
    data class Error(val message: String) : ModelState()
}
