package com.english.accelerator.ui.speaking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 底部输入区域
 */
@Composable
fun InputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            // 文本输入框
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(start = 52.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxSize(),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF1E293B)
                    ),
                    maxLines = 2,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (text.isEmpty()) {
                                Text(
                                    text = "发消息",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // 相机按钮
                IconButton(
                    onClick = onCamera,
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

            // 语音按钮
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFE2E8F0))
                    .clickable { focusManager.clearFocus() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "语音",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 发送按钮
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-120).dp)
                .clip(CircleShape)
                .background(if (text.isNotEmpty()) Color(0xFF3B82F6) else Color(0xFFCBD5E1)),
            enabled = text.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "发送",
                tint = if (text.isNotEmpty()) Color.White else Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
