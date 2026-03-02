package com.english.accelerator.ai

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Singleton manager for Gemma-2B LLM inference
 * Handles model initialization, inference requests, and lifecycle management
 */
class GemmaInferenceManager private constructor(
    private val context: Context
) {
    private var llmInference: LlmInference? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val modelDownloadManager = ModelDownloadManager(context)

    /**
     * Represents the current state of the model
     */
    sealed class ModelState {
        object NotDownloaded : ModelState()
        data class Downloading(val progress: Float) : ModelState()
        object Ready : ModelState()
        data class Error(val message: String) : ModelState()
    }

    private val _modelState = MutableStateFlow<ModelState>(ModelState.NotDownloaded)
    val modelState: StateFlow<ModelState> = _modelState.asStateFlow()

    companion object {
        @Volatile
        private var instance: GemmaInferenceManager? = null

        /**
         * Initialize the singleton instance
         */
        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = GemmaInferenceManager(context.applicationContext)
                    }
                }
            }
        }

        /**
         * Get the singleton instance
         * @throws IllegalStateException if not initialized
         */
        fun getInstance(): GemmaInferenceManager {
            return instance ?: throw IllegalStateException(
                "GemmaInferenceManager not initialized. Call init() first."
            )
        }
    }

    init {
        // Check if model is already downloaded
        if (modelDownloadManager.isModelDownloaded()) {
            _modelState.value = ModelState.Ready
        }
    }

    /**
     * Check if device has sufficient memory for Gemma 3n E2B
     */
    private fun checkMemoryAvailability(): Boolean {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val availableMemory = maxMemory - usedMemory

        // Require at least 3.5GB free for Gemma 3n E2B
        return availableMemory > 3_500_000_000L
    }

    /**
     * Initialize the LLM inference engine
     * Should be called after model is downloaded
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        try {
            if (!modelDownloadManager.isModelDownloaded()) {
                _modelState.value = ModelState.Error("Model not downloaded")
                return@withContext
            }

            // Check memory availability
            if (!checkMemoryAvailability()) {
                _modelState.value = ModelState.Error("内存不足。请关闭其他应用后重试。")
                return@withContext
            }

            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelDownloadManager.getModelPath())
                .setMaxTokens(2048)  // Increased from 512 for Gemma 3n E2B
                .setTemperature(0.3f)
                .setTopK(40)
                .setRandomSeed(0)
                .build()

            llmInference = LlmInference.createFromOptions(context, options)
            _modelState.value = ModelState.Ready
        } catch (e: Exception) {
            _modelState.value = ModelState.Error("Failed to initialize model: ${e.message}")
        }
    }

    /**
     * Download the model with progress tracking
     */
    suspend fun downloadModel() = withContext(Dispatchers.IO) {
        try {
            _modelState.value = ModelState.Downloading(0f)

            modelDownloadManager.downloadModel { progress ->
                _modelState.value = ModelState.Downloading(progress)
            }.onSuccess {
                initialize()
            }.onFailure { error ->
                _modelState.value = ModelState.Error("Download failed: ${error.message}")
            }
        } catch (e: Exception) {
            _modelState.value = ModelState.Error("Download error: ${e.message}")
        }
    }

    /**
     * Pause the current download
     */
    fun pauseDownload() {
        modelDownloadManager.pauseDownload()
    }

    /**
     * Resume the paused download
     */
    fun resumeDownload() {
        modelDownloadManager.resumeDownload()
    }

    /**
     * Cancel the current download
     */
    fun cancelDownload() {
        modelDownloadManager.cancelDownload()
        _modelState.value = ModelState.NotDownloaded
    }

    /**
     * Generate suggestions based on the input text and suggestion type
     */
    suspend fun generateSuggestions(
        text: String,
        type: SuggestionType
    ): InferenceResult = withContext(Dispatchers.Default) {
        try {
            val inference = llmInference
            if (inference == null) {
                return@withContext InferenceResult.Error("Model not initialized")
            }

            // Generate prompt based on type
            val prompt = when (type) {
                SuggestionType.GRAMMAR_CHECK -> PromptTemplates.grammarCheck(text)
                SuggestionType.WRITING_IMPROVEMENT -> PromptTemplates.writingSuggestion(text)
                SuggestionType.CONVERSATION_PRACTICE -> PromptTemplates.conversationPractice("general", text)
            }

            // Run inference
            val response = inference.generateResponse(prompt)

            // Parse response into suggestions
            val suggestions = parseResponse(response, type)

            InferenceResult.Success(suggestions, response)
        } catch (e: Exception) {
            InferenceResult.Error("Inference failed: ${e.message}")
        }
    }

    /**
     * Parse the LLM response into structured suggestions
     */
    private fun parseResponse(response: String, type: SuggestionType): List<GrammarSuggestion> {
        val suggestions = mutableListOf<GrammarSuggestion>()

        when (type) {
            SuggestionType.GRAMMAR_CHECK -> {
                // Parse grammar corrections
                val correctionPattern = Regex("""\[(.+?)\] → \[(.+?)\]: (.+)""")
                correctionPattern.findAll(response).forEach { match ->
                    val (original, corrected, reason) = match.destructured
                    suggestions.add(
                        GrammarSuggestion(
                            original = original.trim(),
                            corrected = corrected.trim(),
                            reason = reason.trim()
                        )
                    )
                }
            }
            SuggestionType.WRITING_IMPROVEMENT -> {
                // Parse writing suggestions
                val suggestionPattern = Regex("""(\d+)\.\s+(.+)""")
                suggestionPattern.findAll(response).forEach { match ->
                    val (_, suggestion) = match.destructured
                    suggestions.add(
                        GrammarSuggestion(
                            original = "",
                            corrected = suggestion.trim(),
                            reason = "Writing improvement"
                        )
                    )
                }
            }
            SuggestionType.CONVERSATION_PRACTICE -> {
                // Parse conversation feedback
                val feedbackPattern = Regex("""FEEDBACK:\s*(.+)""", RegexOption.DOT_MATCHES_ALL)
                feedbackPattern.find(response)?.let { match ->
                    val feedback = match.groupValues[1].trim()
                    suggestions.add(
                        GrammarSuggestion(
                            original = "",
                            corrected = feedback,
                            reason = "Conversation feedback"
                        )
                    )
                }
            }
        }

        return suggestions
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        llmInference?.close()
        llmInference = null
        scope.cancel()
    }

    /**
     * Handle low memory situations
     */
    fun onLowMemory() {
        llmInference?.close()
        llmInference = null
        // Will reinitialize on next request
    }
}
