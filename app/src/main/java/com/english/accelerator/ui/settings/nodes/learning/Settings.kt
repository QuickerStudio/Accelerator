/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
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
 * 学习设置卡片 - 自包含组件
 *
 * 功能：
 * - 学习提醒开关
 * - 夜晚免打扰开关
 * - 每日学习目标设置
 * - 学习计划设置
 * - 学习统计显示设置
 */
@Composable
fun LearningSettingsCard(
    onNavigateToLearningPlan: () -> Unit = {},
    onNavigateToLearningStats: () -> Unit = {},
    onNavigateToLearningGoals: () -> Unit = {}
) {
    // 内部状态管理
    var learningReminderEnabled by remember { mutableStateOf(false) }
    var nightDoNotDisturbEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 学习提醒 - 开关
        SettingItemWithSwitch(
            icon = Icons.Default.Alarm,
            title = "学习提醒",
            checked = learningReminderEnabled,
            onCheckedChange = { learningReminderEnabled = it }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 夜晚免打扰 - 开关
        SettingItemWithSwitch(
            icon = Icons.Default.NightlightRound,
            title = "夜晚免打扰",
            subtitle = "关闭夜晚的学习提醒和自动朗读",
            checked = nightDoNotDisturbEnabled,
            onCheckedChange = { nightDoNotDisturbEnabled = it }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 每日学习目标 - 箭头
        SettingItemWithArrow(
            icon = Icons.Default.TrackChanges,
            title = "每日学习目标",
            subtitle = "设置单词、写作、对话的每日目标",
            onClick = onNavigateToLearningGoals
        )

        Divider(color = Color(0xFFE2E8F0))

        // 学习计划 - 箭头
        SettingItemWithArrow(
            icon = Icons.Default.CalendarToday,
            title = "学习计划",
            subtitle = "设置学习时间和提醒",
            onClick = onNavigateToLearningPlan
        )

        Divider(color = Color(0xFFE2E8F0))

        // 学习统计显示 - 箭头
        SettingItemWithArrow(
            icon = Icons.Default.BarChart,
            title = "学习统计显示",
            subtitle = "查看学习统计数据",
            onClick = onNavigateToLearningStats
        )
    }
}

/**
 * 带开关的设置项
 */
@Composable
private fun SettingItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF8B5CF6),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFF94A3B8)
            )
        )
    }
}

/**
 * 带箭头的设置项
 */
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
