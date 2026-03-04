package com.english.accelerator.ui.speaking

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.english.accelerator.ai.agent.AgentRole
import com.english.accelerator.ai.agent.AgentServiceImpl
import com.english.accelerator.ai.agent.Message as AgentMessage
import com.english.accelerator.ai.history.HistoryManager
import com.english.accelerator.ai.session.Session
import com.english.accelerator.ai.session.SessionManager
import com.english.accelerator.utils.AppLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * ViewModel for conversation screen
 * Manages conversation state, history, and AI agent interactions
 */
class ConversationViewModel(private val context: Context) : ViewModel() {

    private val TAG = "ConversationViewModel"

    private val agentService = AgentServiceImpl(context)
    private val sessionManager = SessionManager.getInstance()
    private val historyManager = HistoryManager.getInstance()

    // Current conversation messages
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Current session
    private val _currentSession = MutableStateFlow<Session?>(null)
    val currentSession: StateFlow<Session?> = _currentSession.asStateFlow()

    // Current agent role
    private val _currentAgent = MutableStateFlow(AgentRole.SPEAKING_PARTNER)
    val currentAgent: StateFlow<AgentRole> = _currentAgent.asStateFlow()

    // Active inference job (for cancellation)
    private var inferenceJob: Job? = null

    init {
        AppLogger.info(TAG, "ConversationViewModel initialized")
        initializeSession()
    }

    /**
     * Initialize or restore session
     */
    private fun initializeSession() {
        viewModelScope.launch {
            try {
                // Try to restore current session
                val currentSession = sessionManager.currentSession.value

                if (currentSession != null && currentSession.type == Session.Type.CONVERSATION) {
                    _currentSession.value = currentSession
                    loadSessionHistory(currentSession.id)
                    AppLogger.info(TAG, "Restored session: ${currentSession.id}")
                } else {
                    // Create new session
                    createNewSession()
                }
            } catch (e: Exception) {
                AppLogger.error(TAG, "Failed to initialize session", e)
                createNewSession()
            }
        }
    }

    /**
     * Create a new conversation session
     */
    fun createNewSession() {
        viewModelScope.launch {
            try {
                val session = sessionManager.createSession(
                    title = "对话 ${System.currentTimeMillis()}",
                    type = Session.Type.CONVERSATION
                )
                _currentSession.value = session
                _messages.value = emptyList()

                // Add welcome message
                addWelcomeMessage()

                AppLogger.info(TAG, "Created new session: ${session.id}")
            } catch (e: Exception) {
                AppLogger.error(TAG, "Failed to create new session", e)
            }
        }
    }

    /**
     * Load conversation history from a session
     */
    private fun loadSessionHistory(sessionId: String) {
        viewModelScope.launch {
            try {
                val history = historyManager.getHistory(sessionId)
                if (history != null && history.messages.isNotEmpty()) {
                    // Convert AgentMessage to UI Message
                    _messages.value = history.messages.map { agentMsg ->
                        Message(
                            id = UUID.randomUUID().toString(),
                            content = agentMsg.content,
                            isFromUser = agentMsg.role == "user",
                            timestamp = agentMsg.timestamp
                        )
                    }
                    AppLogger.info(TAG, "Loaded ${history.messages.size} messages from session: $sessionId")
                } else {
                    // No history, add welcome message
                    addWelcomeMessage()
                }
            } catch (e: Exception) {
                AppLogger.error(TAG, "Failed to load session history", e)
                addWelcomeMessage()
            }
        }
    }

    /**
     * Add welcome message
     */
    private fun addWelcomeMessage() {
        val welcomeMessage = Message(
            content = "Hello! I'm your English conversation partner. Let's practice together! What would you like to talk about today?",
            isFromUser = false
        )
        _messages.value = listOf(welcomeMessage)
    }

    /**
     * Send a user message and get AI response with streaming
     */
    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        val sessionId = _currentSession.value?.id ?: run {
            AppLogger.error(TAG, "No active session")
            return
        }

        // Cancel any ongoing inference
        inferenceJob?.cancel()

        viewModelScope.launch {
            try {
                // Add user message to UI
                val userMessage = Message(content = userInput, isFromUser = true)
                _messages.value = _messages.value + userMessage

                // Save user message to history
                historyManager.addMessage(
                    sessionId = sessionId,
                    message = AgentMessage(role = "user", content = userInput)
                )

                // Set loading state
                _isLoading.value = true

                // Get conversation context from history
                val context = historyManager.getLastMessages(sessionId, 10)

                // Collect performance metrics
                val startTime = System.currentTimeMillis()
                val runtime = Runtime.getRuntime()
                val memoryBefore = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

                // Create a placeholder message for streaming (start with empty content)
                val streamingMessageId = UUID.randomUUID().toString()
                val streamingMessage = Message(
                    id = streamingMessageId,
                    content = "",
                    isFromUser = false
                )
                _messages.value = _messages.value + streamingMessage

                // Generate AI response using AgentService with streaming
                var lastUpdateTime = 0L
                val updateIntervalMs = 50L // 限制更新频率为每 50ms

                inferenceJob = launch {
                    val result = agentService.generateStreaming(
                        userInput = userInput,
                        context = context
                    ) { partialResult, done ->
                        val currentTime = System.currentTimeMillis()

                        // 防抖：只在间隔足够长或完成时更新 UI
                        if (partialResult.isNotEmpty() &&
                            (done || currentTime - lastUpdateTime >= updateIntervalMs)) {
                            lastUpdateTime = currentTime

                            _messages.value = _messages.value.map { msg ->
                                if (msg.id == streamingMessageId) {
                                    msg.copy(content = partialResult)
                                } else {
                                    msg
                                }
                            }
                        }
                    }

                    val endTime = System.currentTimeMillis()
                    val memoryAfter = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
                    val memoryUsed = memoryAfter - memoryBefore

                    result.fold(
                        onSuccess = { response ->
                            // Estimate tokens generated (rough approximation: 1 token ≈ 4 characters)
                            val tokensGenerated = response.length / 4

                            // Create inference stats
                            val stats = InferenceStats(
                                startTime = startTime,
                                endTime = endTime,
                                tokensGenerated = tokensGenerated,
                                memoryUsedMB = memoryUsed
                            )

                            // Update the final message with stats
                            _messages.value = _messages.value.map { msg ->
                                if (msg.id == streamingMessageId) {
                                    msg.copy(
                                        content = response,
                                        inferenceStats = stats
                                    )
                                } else {
                                    msg
                                }
                            }

                            // Save AI response to history
                            historyManager.addMessage(
                                sessionId = sessionId,
                                message = AgentMessage(role = "assistant", content = response)
                            )

                            AppLogger.info(TAG, "AI response generated in ${stats.durationSeconds}s, " +
                                    "${stats.tokensPerSecond} tokens/s, ${stats.memoryUsedMB}MB memory")
                        },
                        onFailure = { error ->
                            // Replace streaming message with error message
                            _messages.value = _messages.value.map { msg ->
                                if (msg.id == streamingMessageId) {
                                    msg.copy(content = "抱歉，发生了错误：${error.message}")
                                } else {
                                    msg
                                }
                            }

                            AppLogger.error(TAG, "Failed to generate AI response", error)
                        }
                    )
                }

                inferenceJob?.join()
            } catch (e: Exception) {
                AppLogger.error(TAG, "Error in sendMessage", e)
            } finally {
                _isLoading.value = false
                inferenceJob = null
            }
        }
    }

    /**
     * Switch to a different agent role
     */
    fun switchAgent(agent: AgentRole) {
        viewModelScope.launch {
            try {
                agentService.switchAgent(agent)
                _currentAgent.value = agent
                AppLogger.info(TAG, "Switched to agent: ${agent.name}")
            } catch (e: Exception) {
                AppLogger.error(TAG, "Failed to switch agent", e)
            }
        }
    }

    /**
     * Clear current conversation
     */
    fun clearConversation() {
        val sessionId = _currentSession.value?.id ?: return
        viewModelScope.launch {
            try {
                historyManager.clearHistory(sessionId)
                _messages.value = emptyList()
                addWelcomeMessage()
                AppLogger.info(TAG, "Cleared conversation for session: $sessionId")
            } catch (e: Exception) {
                AppLogger.error(TAG, "Failed to clear conversation", e)
            }
        }
    }

    /**
     * Cancel ongoing inference
     */
    fun cancelInference() {
        inferenceJob?.cancel()
        inferenceJob = null
        _isLoading.value = false
        AppLogger.info(TAG, "Cancelled ongoing inference")
    }

    override fun onCleared() {
        super.onCleared()
        inferenceJob?.cancel()
        AppLogger.info(TAG, "ConversationViewModel cleared")
    }
}
