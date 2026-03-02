package com.english.accelerator.ui.speaking

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 简单的语音输入测试组件
 * 用于验证长按手势逻辑
 */
@Composable
fun VoiceInputTestScreen() {
    var isRecording by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("等待操作...") }
    var messageCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 状态显示
        Text(
            text = "状态: $statusText",
            fontSize = 18.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "录音中: ${if (isRecording) "是" else "否"}",
            fontSize = 18.sp,
            color = if (isRecording) Color.Red else Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "发送消息数: $messageCount",
            fontSize = 18.sp,
            color = Color.Blue
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 测试输入框
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    if (isRecording) Color(0xFFBFDBFE) else Color(0xFFE2E8F0)
                )
                .pointerInput(Unit) {
                    awaitEachGesture {
                        statusText = "等待按下..."
                        val down = awaitFirstDown()
                        statusText = "按下检测到"

                        val longPressTimeout = 500L
                        val result = withTimeoutOrNull(longPressTimeout) {
                            waitForUpOrCancellation()
                        }

                        if (result == null) {
                            // 长按
                            statusText = "长按触发，开始录音"
                            isRecording = true

                            // 等待松开
                            statusText = "等待松开..."
                            val up = waitForUpOrCancellation()
                            statusText = "松开检测: ${up != null}"

                            if (up != null) {
                                statusText = "发送语音消息"
                                isRecording = false
                                messageCount++
                            } else {
                                statusText = "松开检测失败"
                                isRecording = false
                            }
                        } else {
                            // 短按
                            statusText = "短按检测到"
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isRecording) "正在录音..." else "长按说话",
                color = Color(0xFF64748B),
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "操作说明：\n短按 (<500ms) - 显示短按\n长按 (≥500ms) - 开始录音\n松开 - 发送消息",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
