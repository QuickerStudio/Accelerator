/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ui.settings

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 关于卡片 - 自包含组件
 *
 * 功能：
 * - 显示应用版本信息
 * - 公司信息
 * - 用户协议
 * - 检查更新
 */
@Composable
fun AboutCard() {
    val context = LocalContext.current
    var showVersionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 版本信息
        AboutItem(
            icon = Icons.Default.Info,
            title = "版本信息",
            subtitle = "v0.5.0",
            showArrow = false,
            onClick = { showVersionDialog = true }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 公司信息
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "公司信息",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "QuickerStudio",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Divider(color = Color(0xFFE2E8F0))

        // 用户协议
        AboutItem(
            icon = Icons.Default.Description,
            title = "用户协议",
            subtitle = "查看用户协议和隐私政策",
            showArrow = false,
            onClick = { /* TODO: 显示用户协议 */ }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 检查更新
        AboutItem(
            icon = Icons.Default.SystemUpdate,
            title = "检查更新",
            subtitle = "查看最新版本",
            showArrow = false,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/QuickerStudio/Accelerator"))
                context.startActivity(intent)
            }
        )
    }

    // 版本历史对话框
    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            title = { Text("版本历史") },
            text = {
                Column {
                    Text(
                        text = "v0.5.0 (当前版本)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• 实现模块化设置界面\n• 添加 AI 模型管理功能\n• 优化下载体验",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "v0.4.0",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• 初始版本发布",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showVersionDialog = false }) {
                    Text("关闭", color = Color(0xFF8B5CF6))
                }
            }
        )
    }
}

@Composable
private fun AboutItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showArrow: Boolean = true,
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
        if (showArrow) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
