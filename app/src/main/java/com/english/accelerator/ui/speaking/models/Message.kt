package com.english.accelerator.ui.speaking.models

/**
 * 消息数据模型 - 用于 UI 显示
 */
data class Message(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val inferenceStats: InferenceStats? = null
)

data class InferenceStats(
    val startTime: Long,
    val endTime: Long,
    val tokensGenerated: Int,
    val memoryUsedMB: Long
) {
    val durationSeconds: Float get() = (endTime - startTime) / 1000f
    val tokensPerSecond: Float get() = if (durationSeconds > 0) tokensGenerated / durationSeconds else 0f
}
