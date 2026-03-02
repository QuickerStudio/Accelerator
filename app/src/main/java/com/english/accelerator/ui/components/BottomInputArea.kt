package com.english.accelerator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomInputArea(
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 文本输入区域 70%
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(start = 52.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            ) {
                BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black
                    ),
                    decorationBox = { innerTextField ->
                        if (inputText.isEmpty()) {
                            Text(
                                text = "发消息或按住说话...",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )

                // 相机按钮
                IconButton(
                    onClick = { /* TODO: 打开相机 */ },
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterStart)
                        .offset(x = (-44).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "相机",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 语音按钮 30%
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(if (isRecording) Color(0xFFBFDBFE) else Color(0xFFE2E8F0))
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            isRecording = true
                            focusManager.clearFocus()
                            val up = waitForUpOrCancellation()
                            if (up != null) {
                                isRecording = false
                                // TODO: 处理语音输入
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "语音输入",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 发送按钮
        IconButton(
            onClick = { /* TODO: 发送消息 */ },
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-120).dp)
                .clip(CircleShape)
                .background(
                    if (inputText.isNotEmpty()) Color(0xFF3B82F6) else Color(0xFFCBD5E1)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "发送",
                tint = if (inputText.isNotEmpty()) Color.White else Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
