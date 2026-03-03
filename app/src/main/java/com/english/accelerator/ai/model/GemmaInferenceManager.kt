package com.english.accelerator.ai.model

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.english.accelerator.ai.inference.InferenceResult
import com.english.accelerator.ai.inference.GrammarSuggestion
import com.english.accelerator.ai.inference.PromptTemplates
import com.english.accelerator.ai.inference.SuggestionType
import com.english.accelerator.utils.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Singleton manager for gemma-3n-E2B-it-litert-lm LLM inference
 * Handles model initialization, inference requests, and lifecycle management
 */
class GemmaInferenceManager private constructor(
    private val context: Context
) {
    private var llmInference: LlmInference? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val modelFile = File(context.filesDir, "models/gemma-3n-e2b-it-int4.litertlm")
    private val modelConfig = ModelConfig.getInstance()

    /**
     * Represents the current state of the model
     */
    sealed class ModelState {
        object NotDownloaded : ModelState()
        object Ready : ModelState()
        data class Error(val message: String) : ModelState()
    }

    private val _modelState = MutableStateFlow<ModelState>(ModelState.NotDownloaded)
    val modelState: StateFlow<ModelState> = _modelState.asStateFlow()

    companion object {
        private const val TAG = "GemmaInferenceManager"

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
        AppLogger.info(TAG, "Initializing GemmaInferenceManager")

        // Check if model is already downloaded
        if (isModelDownloaded()) {
            _modelState.value = ModelState.Ready
            AppLogger.info(TAG, "Model file found at: ${modelFile.absolutePath}")
        } else {
            AppLogger.warn(TAG, "Model file not found at: ${modelFile.absolutePath}")
        }
    }

    /**
     * Check if device has sufficient memory for gemma-3n-E2B-it-litert-lm
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
     * Check if the model is already downloaded
     */
    fun isModelDownloaded(): Boolean {
        val exists = modelFile.exists() && modelFile.length() > 0

        // 同步 ModelConfig 状态
        if (exists && !modelConfig.isModelDownloaded()) {
            modelConfig.markModelDownloaded(modelFile.absolutePath, modelFile.length())
            AppLogger.info(TAG, "Updated ModelConfig: model found at ${modelFile.absolutePath}")
        }

        return exists
    }

    /**
     * Initialize the LLM inference engine
     * Should be called after model is downloaded
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        try {
            AppLogger.info(TAG, "Starting model initialization")

            if (!isModelDownloaded()) {
                val error = "Model not downloaded"
                AppLogger.error(TAG, error)
                _modelState.value = ModelState.Error(error)
                modelConfig.markInitializationFailed(error)
                return@withContext
            }

            // Check memory availability
            if (!checkMemoryAvailability()) {
                val error = "内存不足。请关闭其他应用后重试。"
                AppLogger.error(TAG, "Insufficient memory for model initialization")
                _modelState.value = ModelState.Error(error)
                modelConfig.markInitializationFailed(error)
                return@withContext
            }

            AppLogger.info(TAG, "Building LLM inference options")
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(2048)
                .setTemperature(0.3f)
                .setTopK(40)
                .setRandomSeed(0)
                .build()

            AppLogger.info(TAG, "Creating LLM inference instance")
            llmInference = LlmInference.createFromOptions(context, options)
            _modelState.value = ModelState.Ready

            // 标记初始化成功
            modelConfig.markInitializationSuccess()
            AppLogger.info(TAG, "Model initialized successfully")
        } catch (e: Exception) {
            val error = "Failed to initialize model: ${e.message}"
            AppLogger.error(TAG, error)
            AppLogger.error(TAG, "Stack trace: ${e.stackTraceToString()}")
            _modelState.value = ModelState.Error(error)
            modelConfig.markInitializationFailed(error)
        }
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
                val error = "Model not initialized"
                AppLogger.error(TAG, error)
                return@withContext InferenceResult.Error(error)
            }

            AppLogger.debug(TAG, "Generating suggestions for type: $type")

            // Generate prompt based on type
            val prompt = when (type) {
                SuggestionType.GRAMMAR_CHECK -> PromptTemplates.grammarCheck(text)
                SuggestionType.WRITING_IMPROVEMENT -> PromptTemplates.writingSuggestion(text)
                SuggestionType.CONVERSATION_PRACTICE -> PromptTemplates.conversationPractice("general", text)
            }

            // Run inference
            AppLogger.debug(TAG, "Running inference")
            val response = inference.generateResponse(prompt)
            AppLogger.debug(TAG, "Inference completed, response length: ${response.length}")

            // Parse response into suggestions
            val suggestions = parseResponse(response, type)

            InferenceResult.Success(suggestions, response)
        } catch (e: Exception) {
            val error = "Inference failed: ${e.message}"
            AppLogger.error(TAG, error)
            AppLogger.error(TAG, "Stack trace: ${e.stackTraceToString()}")
            InferenceResult.Error(error)
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
        AppLogger.info(TAG, "Cleaning up resources")
        llmInference?.close()
        llmInference = null
        scope.cancel()
    }

    /**
     * Handle low memory situations
     */
    fun onLowMemory() {
        AppLogger.warn(TAG, "Low memory detected, releasing model")
        llmInference?.close()
        llmInference = null
        // Will reinitialize on next request
    }
}
