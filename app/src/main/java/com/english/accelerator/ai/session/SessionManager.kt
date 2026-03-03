package com.english.accelerator.ai.session

import android.content.Context
import com.english.accelerator.ai.agent.AgentRole
import com.english.accelerator.utils.AppLogger
import com.english.accelerator.utils.ConfigManager
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 会话管理器
 *
 * 功能：
 * - 创建、删除、切换会话
 * - 会话列表管理
 * - 会话持久化存储
 * - 当前活动会话跟踪
 */
class SessionManager private constructor(context: Context) {

    private val configManager = ConfigManager.getInstance()
    private val sessions = mutableListOf<Session>()

    private val _currentSession = MutableStateFlow<Session?>(null)
    val currentSession: StateFlow<Session?> = _currentSession.asStateFlow()

    private val _sessionList = MutableStateFlow<List<Session>>(emptyList())
    val sessionList: StateFlow<List<Session>> = _sessionList.asStateFlow()

    companion object {
        private const val TAG = "SessionManager"
        private const val KEY_SESSIONS = "sessions"
        private const val KEY_CURRENT_SESSION_ID = "current_session_id"

        @Volatile
        private var instance: SessionManager? = null

        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = SessionManager(context.applicationContext)
                    }
                }
            }
        }

        fun getInstance(): SessionManager {
            return instance ?: throw IllegalStateException(
                "SessionManager not initialized. Call init() first."
            )
        }
    }

    init {
        AppLogger.info(TAG, "Initializing SessionManager")
        loadSessions()
    }

    /**
     * 创建新会话
     */
    fun createSession(title: String, agentRole: AgentRole): Session {
        val session = Session(
            title = title,
            agentRole = agentRole
        )

        sessions.add(session)
        _currentSession.value = session
        _sessionList.value = sessions.toList()

        saveSessions()
        AppLogger.info(TAG, "Created new session: ${session.id} - $title")

        return session
    }

    /**
     * 切换到指定会话
     */
    fun switchToSession(sessionId: String): Boolean {
        val session = sessions.find { it.id == sessionId }
        if (session != null) {
            _currentSession.value = session
            configManager.putString(KEY_CURRENT_SESSION_ID, sessionId)
            AppLogger.info(TAG, "Switched to session: $sessionId")
            return true
        }
        AppLogger.warn(TAG, "Session not found: $sessionId")
        return false
    }

    /**
     * 删除会话
     */
    fun deleteSession(sessionId: String): Boolean {
        val session = sessions.find { it.id == sessionId }
        if (session != null) {
            sessions.remove(session)

            // 如果删除的是当前会话，切换到最新的会话
            if (_currentSession.value?.id == sessionId) {
                _currentSession.value = sessions.lastOrNull()
                _currentSession.value?.let {
                    configManager.putString(KEY_CURRENT_SESSION_ID, it.id)
                }
            }

            _sessionList.value = sessions.toList()
            saveSessions()
            AppLogger.info(TAG, "Deleted session: $sessionId")
            return true
        }
        AppLogger.warn(TAG, "Session not found for deletion: $sessionId")
        return false
    }

    /**
     * 更新会话
     */
    fun updateSession(session: Session) {
        val index = sessions.indexOfFirst { it.id == session.id }
        if (index != -1) {
            sessions[index] = session

            if (_currentSession.value?.id == session.id) {
                _currentSession.value = session
            }

            _sessionList.value = sessions.toList()
            saveSessions()
            AppLogger.debug(TAG, "Updated session: ${session.id}")
        }
    }

    /**
     * 获取所有会话
     */
    fun getAllSessions(): List<Session> {
        return sessions.toList()
    }

    /**
     * 获取指定会话
     */
    fun getSession(sessionId: String): Session? {
        return sessions.find { it.id == sessionId }
    }

    /**
     * 保存会话到持久化存储
     */
    private fun saveSessions() {
        try {
            configManager.putList(KEY_SESSIONS, sessions)
            AppLogger.debug(TAG, "Saved ${sessions.size} sessions")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to save sessions", e)
        }
    }

    /**
     * 从持久化存储加载会话
     */
    private fun loadSessions() {
        try {
            val type = object : TypeToken<List<Session>>() {}.type
            val loadedSessions = configManager.getList<Session>(KEY_SESSIONS, type)

            if (loadedSessions != null) {
                sessions.clear()
                sessions.addAll(loadedSessions)
                _sessionList.value = sessions.toList()

                // 恢复当前会话
                val currentSessionId = configManager.getString(KEY_CURRENT_SESSION_ID)
                if (currentSessionId != null) {
                    _currentSession.value = sessions.find { it.id == currentSessionId }
                }

                AppLogger.info(TAG, "Loaded ${sessions.size} sessions")
            } else {
                AppLogger.info(TAG, "No saved sessions found")
            }
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to load sessions", e)
        }
    }

    /**
     * 清除所有会话
     */
    fun clearAllSessions() {
        sessions.clear()
        _currentSession.value = null
        _sessionList.value = emptyList()
        saveSessions()
        AppLogger.info(TAG, "Cleared all sessions")
    }
}
