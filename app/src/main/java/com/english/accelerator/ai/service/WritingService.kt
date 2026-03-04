package com.english.accelerator.ai.service

import android.content.Context
import com.english.accelerator.ai.core.InferenceConfig
import com.english.accelerator.ai.core.InferenceEngine
import com.english.accelerator.ai.prompt.WritingPrompts
import com.english.accelerator.ai.model.GrammarSuggestion
import com.english.accelerator.ai.model.InferenceResult
import com.english.accelerator.utils.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Writing improvement service using sync inference
 * Provides structured writing suggestions and improvements
 */
class WritingService private constructor(
    private val context: Context,
    private val engine: InferenceEngine
) {
    private val TAG = WritingService::class.qualifiedName

    /**
     * Get writing improvement suggestions
     */
    suspend fun improveSuggestions(text: String): InferenceResult = withContext(Dispatchers.Default) {
        try {
            AppLogger.debug(TAG, "Getting writing suggestions for text: ${text.take(50)}...")

            val prompt = WritingPrompts.writingSuggestion(text)
            val response = engine.generateSync(prompt)

            AppLogger.debug(TAG, "Writing suggestions completed, response length: ${response.length}")

            // Parse response into structured suggestions
            val suggestions = parseWritingResponse(response)

            InferenceResult.Success(suggestions, response)
        } catch (e: Exception) {
            val error = "Writing suggestions failed: ${e.message}"
            AppLogger.error(TAG, error, e)
            InferenceResult.Error(error)
        }
    }

    /**
     * Parse LLM response into structured writing suggestions
     */
    private fun parseWritingResponse(response: String): List<GrammarSuggestion> {
        val suggestions = mutableListOf<GrammarSuggestion>()

        // Parse response format:
        // SUGGESTION: [type]
        // ORIGINAL: [original text]
        // IMPROVED: [improved text]
        // REASON: [explanation]

        val lines = response.lines()
        var currentType: String? = null
        var currentOriginal: String? = null
        var currentImproved: String? = null
        var currentReason: String? = null

        for (line in lines) {
            when {
                line.startsWith("SUGGESTION:") -> {
                    // Save previous suggestion if exists
                    if (currentType != null && currentOriginal != null && currentImproved != null) {
                        suggestions.add(
                            GrammarSuggestion(
                                type = com.english.accelerator.ai.inference.SuggestionType.WRITING_IMPROVEMENT,
                                original = currentOriginal,
                                suggestion = currentImproved,
                                explanation = currentReason ?: ""
                            )
                        )
                    }
                    currentType = line.removePrefix("SUGGESTION:").trim()
                }
                line.startsWith("ORIGINAL:") -> {
                    currentOriginal = line.removePrefix("ORIGINAL:").trim()
                }
                line.startsWith("IMPROVED:") -> {
                    currentImproved = line.removePrefix("IMPROVED:").trim()
                }
                line.startsWith("REASON:") -> {
                    currentReason = line.removePrefix("REASON:").trim()
                }
            }
        }

        // Add last suggestion
        if (currentType != null && currentOriginal != null && currentImproved != null) {
            suggestions.add(
                GrammarSuggestion(
                    type = com.english.accelerator.ai.inference.SuggestionType.WRITING_IMPROVEMENT,
                    original = currentOriginal,
                    suggestion = currentImproved,
                    explanation = currentReason ?: ""
                )
            )
        }

        return suggestions
    }

    companion object {
        @Volatile
        private var instance: WritingService? = null

        fun getInstance(context: Context): WritingService {
            return instance ?: synchronized(this) {
                instance ?: run {
                    val config = InferenceConfig.forGemma3N(context)
                    val engine = InferenceEngine.getInstance(context, config)
                    WritingService(context, engine).also { instance = it }
                }
            }
        }
    }
}
