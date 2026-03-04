package com.english.accelerator.ui.speaking

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.english.accelerator.ai.agent.AgentService
import com.english.accelerator.ai.agent.Prompts
import com.english.accelerator.ai.history.HistoryManager
import com.english.accelerator.ai.session.Session
import com.english.accelerator.ai.session.SessionManager
import com.english.accelerator.ui.speaking.models.InferenceStats
import com.english.accelerator.ui.speaking.models.Message
import com.english.accelerator.utils.AppLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * 对话 ViewModel - 简化版
 */
class VM(private val context: Context) : ViewModel() {

    private val TAG = "ConversationVM"
    private val agentService = AgentService(context)
    private val sessionManager = SessionManager.getInstance()
    private val historyManager = HistoryManager.getInstance()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentSession = MutableStateFlow<Session?>(null)
    val currentSession: StateFlow<Session?> = _currentSession.asStateFlow()

    private var inferenceJob: Job? = null

    init {
        initSession()
    }

    private fun initSession() {
        viewModelScope.launch {
            try {
                val current = sessionManager.currentSession.value
                if (current != null && current.type == Session.Type.CONVERSATION) {
                    _currentSession.value = current
                    loadHistory(current.id)
                } else {
                    createSession()
                }
            } catch (e: Exception) {
                AppLogger.error(TAG, "Init failed", e)
                createSession()
            }
        }
    }

    fun createSession() {
        viewModelScope.launch {
            try {
                val session = sessionManager.createSession("对话", Session.Type.CONVERSATION)
                _currentSession.value = session
                _messages.value = emptyList()
            } catch (e: Exception) {
                AppLogger.error(TAG, "Create session failed", e)
            }
        }
    }

    private fun loadHistory(sessionId: String) {
        viewModelScope.launch {
            try {
                val history = historyManager.getHistory(sessionId)
                _messages.value = history?.messages?.map {
                    Message(
                        id = UUID.randomUUID().toString(),
                        content = it.content,
                        isFromUser = it.role == "user",
                        timestamp = it.timestamp
                    )
                } ?: emptyList()
            } catch (e: Exception) {
                AppLogger.error(TAG, "Load history failed", e)
            }
        }
    }

    fun send(input: String) {
        if (input.isBlank()) return

        val sessionId = _currentSession.value?.id ?: return
        inferenceJob?.cancel()

        viewModelScope.launch {
            try {
                val userMsg = Message(content = input, isFromUser = true)
                _messages.value = _messages.value + userMsg

                _isLoading.value = true

                val startTime = System.currentTimeMillis()
                val runtime = Runtime.getRuntime()
                val memBefore = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

                val streamingId = UUID.randomUUID().toString()
                val streamingMsg = Message(id = streamingId, content = "", isFromUser = false)
                _messages.value = _messages.value + streamingMsg

                var lastUpdate = 0L
                val interval = 50L

                inferenceJob = launch {
                    val result = agentService.generateResponse(
                        sessionId = sessionId,
                        userInput = input,
                        systemPrompt = Prompts.SPEAKING_PARTNER
                    ) { partial, done ->
                        val now = System.currentTimeMillis()
                        if (partial.isNotEmpty() && (done || now - lastUpdate >= interval)) {
                            lastUpdate = now
                            _messages.value = _messages.value.map {
                                if (it.id == streamingId) it.copy(content = partial) else it
                            }
                        }
                    }

                    val endTime = System.currentTimeMillis()
                    val memAfter = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

                    result.fold(
                        onSuccess = { response ->
                            val stats = InferenceStats(
                                startTime = startTime,
                                endTime = endTime,
                                tokensGenerated = response.length / 4,
                                memoryUsedMB = memAfter - memBefore
                            )
                            _messages.value = _messages.value.map {
                                if (it.id == streamingId) it.copy(content = response, inferenceStats = stats) else it
                            }
                        },
                        onFailure = { error ->
                            _messages.value = _messages.value.map {
                                if (it.id == streamingId) it.copy(content = "错误：${error.message}") else it
                            }
                        }
                    )
                }

                inferenceJob?.join()
            } catch (e: Exception) {
                AppLogger.error(TAG, "Send failed", e)
            } finally {
                _isLoading.value = false
                inferenceJob = null
            }
        }
    }

    fun clear() {
        val sessionId = _currentSession.value?.id ?: return
        viewModelScope.launch {
            historyManager.clearHistory(sessionId)
            _messages.value = emptyList()
        }
    }

    fun cancel() {
        inferenceJob?.cancel()
        inferenceJob = null
        _isLoading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        inferenceJob?.cancel()
    }
}
