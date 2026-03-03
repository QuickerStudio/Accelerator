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
 * 权限管理卡片 - 自包含组件
 *
 * 功能：
 * - 存储权限管理
 * - 通知权限管理
 * - 麦克风权限管理
 */
@Composable
fun PermissionsCard() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 存储权限
        PermissionItem(
            icon = Icons.Default.Storage,
            title = "存储权限",
            subtitle = "用于保存学习数据和模型文件",
            status = "已授权",
            onClick = { /* TODO: 打开权限设置 */ }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 通知权限
        PermissionItem(
            icon = Icons.Default.Notifications,
            title = "通知权限",
            subtitle = "用于发送学习提醒",
            status = "已授权",
            onClick = { /* TODO: 打开权限设置 */ }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 麦克风权限
        PermissionItem(
            icon = Icons.Default.Mic,
            title = "麦克风权限",
            subtitle = "用于语音练习功能",
            status = "未授权",
            onClick = { /* TODO: 打开权限设置 */ }
        )
    }
}

@Composable
private fun PermissionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    status: String,
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
        Text(
            text = status,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (status == "已授权") Color(0xFF10B981) else Color(0xFF64748B)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}
