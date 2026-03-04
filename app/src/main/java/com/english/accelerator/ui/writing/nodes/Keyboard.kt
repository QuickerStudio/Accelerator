package com.english.accelerator.ui.writing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimpleEnglishKeyboard(
    onKeyPress: (String) -> Unit,
    onBackspace: () -> Unit,
    onSpace: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isUpperCase by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE5E7EB))
            .padding(bottom = 15.dp)  // 底部留出15dp避免按钮太靠近屏幕边缘
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 关闭按钮行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF9CA3AF), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "关闭键盘",
                    tint = Color.White
                )
            }
        }

        // 第一行：Q-P
        KeyboardRow(
            keys = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
            isUpperCase = isUpperCase,
            onKeyPress = onKeyPress
        )

        // 第二行：A-L
        KeyboardRow(
            keys = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
            isUpperCase = isUpperCase,
            onKeyPress = onKeyPress
        )

        // 第三行：Shift + Z-M + Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Shift 键
            KeyButton(
                text = "⇧",
                onClick = { isUpperCase = !isUpperCase },
                modifier = Modifier.weight(1.5f),
                backgroundColor = if (isUpperCase) Color(0xFF3B82F6) else Color(0xFF9CA3AF)
            )

            // Z-M
            listOf("Z", "X", "C", "V", "B", "N", "M").forEach { key ->
                KeyButton(
                    text = if (isUpperCase) key else key.lowercase(),
                    onClick = { onKeyPress(if (isUpperCase) key else key.lowercase()) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Backspace 键
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .height(48.dp)
                    .background(Color(0xFF9CA3AF), RoundedCornerShape(8.dp))
                    .clickable { onBackspace() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "删除",
                    tint = Color.White
                )
            }
        }

        // 第四行：标点符号 + 换行 + 空格
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 常用标点
            listOf("?", "!", "'", ",", ".").forEach { key ->
                KeyButton(
                    text = key,
                    onClick = { onKeyPress(key) },
                    modifier = Modifier.weight(1f)
                )
            }

            // 换行键
            KeyButton(
                text = "↵",
                onClick = { onKeyPress("\n") },
                modifier = Modifier.weight(1f)
            )

            // 空格键
            Box(
                modifier = Modifier
                    .weight(3f)
                    .height(48.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .clickable { onSpace() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SpaceBar,
                    contentDescription = "空格",
                    tint = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun KeyboardRow(
    keys: List<String>,
    isUpperCase: Boolean,
    onKeyPress: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        keys.forEach { key ->
            KeyButton(
                text = if (isUpperCase) key else key.lowercase(),
                onClick = { onKeyPress(if (isUpperCase) key else key.lowercase()) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KeyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = if (backgroundColor == Color.White) Color(0xFF1F2937) else Color.White
        )
    }
}
