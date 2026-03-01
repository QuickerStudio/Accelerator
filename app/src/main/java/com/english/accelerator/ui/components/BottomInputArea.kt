package com.english.accelerator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomInputArea(
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 相机按钮
        IconButton(
            onClick = { /* TODO: 打开相机 */ },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "相机",
                tint = Color(0xFF64748B),
                modifier = Modifier.size(20.dp)
            )
        }

        // 文本输入框
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            placeholder = {
                Text(
                    text = "发消息或按住说话...",
                    color = Color(0xFF94A3B8)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F5F9),
                unfocusedContainerColor = Color(0xFFF1F5F9),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(24.dp)
        )

        // 上传按钮
        IconButton(
            onClick = { /* TODO: 上传文件 */ },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "上传",
                tint = Color(0xFF64748B),
                modifier = Modifier.size(20.dp)
            )
        }

        // 发送按钮
        IconButton(
            onClick = { /* TODO: 发送消息 */ },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (inputText.isNotEmpty()) Color(0xFF3B82F6) else Color(0xFFF1F5F9)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "发送",
                tint = if (inputText.isNotEmpty()) Color.White else Color(0xFF64748B),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
