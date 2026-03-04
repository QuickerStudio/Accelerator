/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ai.agent

import android.content.Context
import com.english.accelerator.ai.agent.tools.MessageTool
import com.english.accelerator.ai.agent.tools.ReadTool
import com.english.accelerator.ai.agent.tools.UpdateTitleTool
import com.english.accelerator.ai.agent.tools.WriteTool
import com.english.accelerator.ai.history.HistoryManager
import com.english.accelerator.ai.session.SessionManager
import com.english.accelerator.utils.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Agent 服务 - UI 交互层接口
 *
 * 职责：
 * - 管理会话和历史记录
 * - 协调 Agent 和 Tools
 * - 处理线程切换
 * - 执行工具调用
 */
class AgentService(private val context: Context) {

    private val TAG = "AgentService"
    private val agent = MainAgent(context)
    private val sessionManager = SessionManager.getInstance()
    private val historyManager = HistoryManager.getInstance()

    // Tools
    private val readTool = ReadTool()
    private val writeTool = WriteTool()
    private val updateTitleTool = UpdateTitleTool(sessionManager)
    private val messageTool = MessageTool(historyManager)

    /**
     * 生成响应 - 主要方法
     */
    suspend fun generateResponse(
        sessionId: String,
        userInput: String,
        systemPrompt: String,
        onPartialResult: (String, Boolean) -> Unit
    ): Result<String> = withContext(Dispatchers.Default) {
        try {
            // 获取历史记录
            val history = historyManager.getLastMessages(sessionId, 10)

            // 保存用户消息
            messageTool.saveUserMessage(sessionId, userInput)

            // 执行推理
            val result = agent.execute(
                systemPrompt = systemPrompt,
                history = history,
                userInput = userInput,
                onPartialResult = onPartialResult
            )

            // 保存 AI 响应
            result.onSuccess { response ->
                messageTool.saveAssistantMessage(sessionId, response)
            }

            result
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to generate response: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * 生成对话标题
     */
    suspend fun generateTitle(
        sessionId: String,
        firstMessage: String
    ): Result<String> = withContext(Dispatchers.Default) {
        try {
            var title = ""
            val result = agent.execute(
                systemPrompt = Prompts.THREAD_TITLE,
                history = emptyList(),
                userInput = firstMessage,
                onPartialResult = { partial, _ -> title = partial }
            )

            result.onSuccess {
                updateTitleTool.updateTitle(sessionId, title)
            }

            result
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to generate title: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * 读取数据 - 工具调用
     */
    suspend fun readData(key: String): Result<String> = withContext(Dispatchers.IO) {
        readTool.execute(key)
    }

    /**
     * 写入数据 - 工具调用
     */
    suspend fun writeData(key: String, value: String): Result<Unit> = withContext(Dispatchers.IO) {
        writeTool.execute(key, value)
    }

    /**
     * 检查服务是否就绪
     */
    fun isReady(): Boolean = agent.isReady()
}
