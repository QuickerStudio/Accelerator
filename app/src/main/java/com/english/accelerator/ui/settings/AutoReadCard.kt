package com.english.accelerator.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 自动朗读设置卡片 - 自包含组件
 *
 * 功能：
 * - 自动朗读开关
 * - 朗读速度设置
 * - 朗读音量设置
 * - 朗读语音选择
 */
@Composable
fun AutoReadCard() {
    var autoReadEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 自动朗读 - 开关
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = null,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "自动朗读",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = autoReadEnabled,
                onCheckedChange = { autoReadEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF8B5CF6),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFF94A3B8)
                )
            )
        }

        Divider(color = Color(0xFFE2E8F0))

        // 朗读速度 - 箭头
        SettingItemWithArrow(
            icon = Icons.Default.Speed,
            title = "朗读速度",
            subtitle = "设置朗读速度",
            onClick = { /* TODO: 进入朗读速度设置页面 */ }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 朗读音量 - 箭头
        SettingItemWithArrow(
            icon = Icons.Default.VolumeDown,
            title = "朗读音量",
            subtitle = "设置朗读音量",
            onClick = { /* TODO: 进入朗读音量设置页面 */ }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 朗读语音 - 箭头
        SettingItemWithArrow(
            icon = Icons.Default.RecordVoiceOver,
            title = "朗读语音",
            subtitle = "选择朗读语音",
            onClick = { /* TODO: 进入朗读语音选择页面 */ }
        )
    }
}

@Composable
private fun SettingItemWithArrow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8B5CF6),
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}
