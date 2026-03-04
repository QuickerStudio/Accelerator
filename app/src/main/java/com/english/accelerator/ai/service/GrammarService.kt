package com.english.accelerator.ai.service

import android.content.Context
import com.english.accelerator.ai.core.InferenceConfig
import com.english.accelerator.ai.core.InferenceEngine
import com.english.accelerator.ai.prompt.GrammarPrompts
import com.english.accelerator.ai.model.GrammarSuggestion
import com.english.accelerator.ai.model.InferenceResult
import com.english.accelerator.utils.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Grammar checking service using sync inference
 * Parses structured grammar suggestions from LLM output
 */
class GrammarService private constructor(
    private val context: Context,
    private val engine: InferenceEngine
) {
    private val TAG = GrammarService::class.qualifiedName

    /**
     * Check grammar and return structured suggestions
     */
    suspend fun checkGrammar(text: String): InferenceResult = withContext(Dispatchers.Default) {
        try {
            AppLogger.debug(TAG, "Checking grammar for text: ${text.take(50)}...")

            val prompt = GrammarPrompts.grammarCheck(text)
            val response = engine.generateSync(prompt)

            AppLogger.debug(TAG, "Grammar check completed, response length: ${response.length}")

            // Parse response into structured suggestions
            val suggestions = parseGrammarResponse(response)

            InferenceResult.Success(suggestions, response)
        } catch (e: Exception) {
            val error = "Grammar check failed: ${e.message}"
            AppLogger.error(TAG, error, e)
            InferenceResult.Error(error)
        }
    }

    /**
     * Parse LLM response into structured grammar suggestions
     */
    private fun parseGrammarResponse(response: String): List<GrammarSuggestion> {
        val suggestions = mutableListOf<GrammarSuggestion>()

        // Parse response format:
        // ERROR: [description]
        // ORIGINAL: [original text]
        // CORRECTION: [corrected text]
        // EXPLANATION: [explanation]

        val lines = response.lines()
        var currentError: String? = null
        var currentOriginal: String? = null
        var currentCorrection: String? = null
        var currentExplanation: String? = null

        for (line in lines) {
            when {
                line.startsWith("ERROR:") -> {
                    // Save previous suggestion if exists
                    if (currentError != null && currentOriginal != null && currentCorrection != null) {
                        suggestions.add(
                            GrammarSuggestion(
                                type = com.english.accelerator.ai.inference.SuggestionType.GRAMMAR_CHECK,
                                original = currentOriginal,
                                suggestion = currentCorrection,
                                explanation = currentExplanation ?: ""
                            )
                        )
                    }
                    currentError = line.removePrefix("ERROR:").trim()
                }
                line.startsWith("ORIGINAL:") -> {
                    currentOriginal = line.removePrefix("ORIGINAL:").trim()
                }
                line.startsWith("CORRECTION:") -> {
                    currentCorrection = line.removePrefix("CORRECTION:").trim()
                }
                line.startsWith("EXPLANATION:") -> {
                    currentExplanation = line.removePrefix("EXPLANATION:").trim()
                }
            }
        }

        // Add last suggestion
        if (currentError != null && currentOriginal != null && currentCorrection != null) {
            suggestions.add(
                GrammarSuggestion(
                    type = com.english.accelerator.ai.inference.SuggestionType.GRAMMAR_CHECK,
                    original = currentOriginal,
                    suggestion = currentCorrection,
                    explanation = currentExplanation ?: ""
                )
            )
        }

        return suggestions
    }

    companion object {
        @Volatile
        private var instance: GrammarService? = null

        fun getInstance(context: Context): GrammarService {
            return instance ?: synchronized(this) {
                instance ?: run {
                    val config = InferenceConfig.forGemma3N(context)
                    val engine = InferenceEngine.getInstance(context, config)
                    GrammarService(context, engine).also { instance = it }
                }
            }
        }
    }
}
