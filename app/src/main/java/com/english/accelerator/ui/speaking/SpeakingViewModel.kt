package com.english.accelerator.ui.speaking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.english.accelerator.ai.agent.AgentRole
import com.english.accelerator.ai.agent.AgentService
import com.english.accelerator.ai.agent.AgentServiceImpl
import com.english.accelerator.ai.model.GemmaInferenceManager
import com.english.accelerator.ai.agent.Message
import com.english.accelerator.data.ConversationManager
import com.english.accelerator.data.ConversationTurn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Speaking Screen.
 * Manages conversation state and integrates with the Agent system.
 */
class SpeakingViewModel : ViewModel() {

    private val inferenceManager = GemmaInferenceManager.getInstance()
    private val agentService: AgentService = AgentServiceImpl(inferenceManager)

    private val _messages = MutableStateFlow<List<ConversationTurn>>(emptyList())
    val messages: StateFlow<List<ConversationTurn>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Switch to Speaking Partner agent on initialization
        viewModelScope.launch {
            agentService.switchAgent(AgentRole.SPEAKING_PARTNER)
            loadConversationHistory()
        }
    }

    /**
     * Load conversation history from ConversationManager
     */
    private fun loadConversationHistory() {
        _messages.value = ConversationManager.getAllConversations()
    }

    /**
     * Send a message and get AI response
     */
    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        viewModelScope.launch {
            try {
                // Add user message
                val userTurn = ConversationManager.addConversation(
                    role = ConversationTurn.Role.USER,
                    content = userInput
                )
                _messages.value = ConversationManager.getAllConversations()

                // Show loading state
                _isLoading.value = true
                _error.value = null

                // Build context from recent conversation history
                val context = buildContextFromHistory()

                // Generate AI response using agent service
                val result = agentService.generate(userInput, context)

                result.fold(
                    onSuccess = { response ->
                        // Add AI response
                        ConversationManager.addConversation(
                            role = ConversationTurn.Role.ASSISTANT,
                            content = response
                        )
                        _messages.value = ConversationManager.getAllConversations()
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Failed to generate response"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Generate welcome message when conversation is empty
     */
    fun generateWelcomeMessage() {
        if (_messages.value.isNotEmpty()) return

        viewModelScope.launch {
            try {
                _isLoading.value = true

                val welcomePrompt = "Greet the user and introduce yourself as their English speaking partner. Ask what they'd like to talk about today."
                val result = agentService.generate(welcomePrompt, emptyList())

                result.fold(
                    onSuccess = { response ->
                        ConversationManager.addConversation(
                            role = ConversationTurn.Role.ASSISTANT,
                            content = response
                        )
                        _messages.value = ConversationManager.getAllConversations()
                    },
                    onFailure = { exception ->
                        // Fallback to default welcome message
                        ConversationManager.addConversation(
                            role = ConversationTurn.Role.ASSISTANT,
                            content = "Hello! I'm your English conversation partner. Let's practice together! What would you like to talk about today?"
                        )
                        _messages.value = ConversationManager.getAllConversations()
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear all conversation history
     */
    fun clearConversation() {
        ConversationManager.deleteAll()
        _messages.value = emptyList()
    }

    /**
     * Build context from conversation history for the agent
     */
    private fun buildContextFromHistory(): List<Message> {
        // Get recent conversation turns (last 10 messages = 5 turns)
        val recentTurns = ConversationManager.getRecentConversations(10)

        return recentTurns.map { turn ->
            Message(
                role = when (turn.role) {
                    ConversationTurn.Role.USER -> "user"
                    ConversationTurn.Role.ASSISTANT -> "assistant"
                },
                content = turn.content
            )
        }
    }

    /**
     * Switch to a different agent role
     */
    fun switchAgent(role: AgentRole) {
        viewModelScope.launch {
            agentService.switchAgent(role)
        }
    }

    /**
     * Get the current agent role
     */
    fun getCurrentAgent(): AgentRole {
        return agentService.getCurrentAgent()
    }
}
