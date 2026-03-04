package com.english.accelerator.ui.speaking.models

import java.util.UUID

/**
 * 消息数据模型
 */
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val inferenceStats: InferenceStats? = null
)

/**
 * 推理性能统计
 */
data class InferenceStats(
    val startTime: Long,
    val endTime: Long,
    val tokensGenerated: Int,
    val memoryUsedMB: Long
) {
    val durationSeconds: Float
        get() = (endTime - startTime) / 1000f

    val tokensPerSecond: Float
        get() = if (durationSeconds > 0) tokensGenerated / durationSeconds else 0f
}

/**
 * 对话数据模型
 */
data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val preview: String,
    val timestamp: Long = System.currentTimeMillis(),
    val messageCount: Int = 0
)
