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
 * 关于卡片 - 自包含组件
 *
 * 功能：
 * - 显示应用版本信息
 * - 显示开源许可信息
 * - 未来可扩展更多关于信息
 */
@Composable
fun AboutCard() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 版本信息
        AboutItem(
            icon = Icons.Default.Info,
            title = "版本信息",
            subtitle = "v0.5.0",
            onClick = { /* TODO: 显示版本详情 */ }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 开源许可
        AboutItem(
            icon = Icons.Default.Code,
            title = "开源许可",
            subtitle = "查看开源组件许可信息",
            onClick = { /* TODO: 显示许可信息 */ }
        )
    }
}

@Composable
private fun AboutItem(
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
