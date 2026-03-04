package com.english.accelerator.ai.agent.tools

import com.english.accelerator.ai.session.SessionManager
import com.english.accelerator.utils.AppLogger

/**
 * Update Title Tool - 更新会话标题
 */
class UpdateTitleTool(private val sessionManager: SessionManager) {
    private val TAG = "UpdateTitleTool"

    fun updateTitle(sessionId: String, title: String): Result<Unit> {
        return try {
            val session = sessionManager.getSession(sessionId)
            if (session != null) {
                val updatedSession = session.copy(title = title)
                sessionManager.updateSession(updatedSession)
                AppLogger.info(TAG, "Updated title for session $sessionId: $title")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Session not found: $sessionId"))
            }
        } catch (e: Exception) {
            AppLogger.error(TAG, "Update title failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
