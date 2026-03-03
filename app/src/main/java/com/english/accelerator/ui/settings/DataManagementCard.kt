package com.english.accelerator.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
 * 数据管理卡片 - 自包含组件（可折叠）
 *
 * 功能：
 * - AI 模型数据管理
 * - 数据备份
 * - 数据恢复
 * - 清除缓存
 * - 重置应用
 */
@Composable
fun DataManagementCard(
    onOpenModelDirectory: () -> Unit = {},
    onClearModelCache: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 折叠/展开标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = null,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "数据管理",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }

        // 可折叠内容
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                Divider(color = Color(0xFFE2E8F0))

                // 数据备份
                DataManagementItem(
                    icon = Icons.Default.Backup,
                    title = "数据备份",
                    subtitle = "备份学习数据到本地",
                    onClick = { /* TODO: 执行数据备份 */ }
                )

                Divider(color = Color(0xFFE2E8F0))

                // 数据恢复
                DataManagementItem(
                    icon = Icons.Default.RestorePage,
                    title = "数据恢复",
                    subtitle = "从备份恢复学习数据",
                    onClick = { /* TODO: 执行数据恢复 */ }
                )

                Divider(color = Color(0xFFE2E8F0))

                // 清除缓存
                DataManagementItem(
                    icon = Icons.Default.CleaningServices,
                    title = "清除缓存",
                    subtitle = "清除应用缓存数据",
                    onClick = { /* TODO: 清除缓存 */ }
                )

                Divider(color = Color(0xFFE2E8F0))

                // 打开模型文件夹
                DataManagementItem(
                    icon = Icons.Default.FolderOpen,
                    title = "打开模型文件夹",
                    subtitle = "查看 AI 模型文件",
                    showArrow = false,
                    onClick = onOpenModelDirectory
                )

                Divider(color = Color(0xFFE2E8F0))

                // 清除模型缓存
                DataManagementItemWithButton(
                    icon = Icons.Default.DeleteOutline,
                    title = "清除模型缓存",
                    subtitle = "清除未完成的模型下载缓存",
                    buttonText = "清除",
                    onButtonClick = onClearModelCache
                )

                Divider(color = Color(0xFFE2E8F0))

                // 重置应用
                DataManagementItem(
                    icon = Icons.Default.RestartAlt,
                    title = "重置应用",
                    subtitle = "恢复应用到初始状态",
                    textColor = Color(0xFFEF4444),
                    onClick = { /* TODO: 重置应用 */ }
                )
            }
        }
    }
}

@Composable
private fun DataManagementItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    textColor: Color = Color(0xFF1E293B),
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
            tint = if (textColor == Color(0xFFEF4444)) textColor else Color(0xFF8B5CF6),
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
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

@Composable
private fun DataManagementItemWithButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    buttonText: String,
    onButtonClick: () -> Unit
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
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }
        OutlinedButton(
            onClick = onButtonClick,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFEF4444)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = buttonText,
                fontSize = 13.sp
            )
        }
    }
}
