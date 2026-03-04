package com.english.accelerator.ai.agent.tools

import com.english.accelerator.ai.agent.Message
import com.english.accelerator.ai.history.HistoryManager
import com.english.accelerator.utils.AppLogger

/**
 * Message Tool - 管理消息历史
 */
class MessageTool(private val historyManager: HistoryManager) {
    private val TAG = "MessageTool"

    fun saveUserMessage(sessionId: String, content: String): Result<Unit> {
        return try {
            historyManager.addMessage(
                sessionId = sessionId,
                message = Message(role = "user", content = content)
            )
            AppLogger.info(TAG, "Saved user message for session: $sessionId")
            Result.success(Unit)
        } catch (e: Exception) {
            AppLogger.error(TAG, "Save user message failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun saveAssistantMessage(sessionId: String, content: String): Result<Unit> {
        return try {
            historyManager.addMessage(
                sessionId = sessionId,
                message = Message(role = "assistant", content = content)
            )
            AppLogger.info(TAG, "Saved assistant message for session: $sessionId")
            Result.success(Unit)
        } catch (e: Exception) {
            AppLogger.error(TAG, "Save assistant message failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
