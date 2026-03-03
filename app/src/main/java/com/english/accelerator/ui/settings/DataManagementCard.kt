package com.english.accelerator.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.utils.DataExportImportHelper
import kotlinx.coroutines.launch

/**
 * 数据管理卡片 - 自包含组件（可折叠）
 *
 * 功能：
 * - 数据备份/恢复
 * - AI 模型数据管理
 * - 清除缓存
 * - 重置应用
 */
@Composable
fun DataManagementCard(
    onOpenModelDirectory: () -> Unit = {},
    onClearModelCache: () -> Unit = {}
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastColor by remember { mutableStateOf(Color.White) }
    val scope = rememberCoroutineScope()

    // 文件选择器 - 导入数据
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = DataExportImportHelper.importData(context, it)
                result.onSuccess { message ->
                    toastMessage = message
                    toastColor = Color(0xFFDCFCE7)
                    showToast = true
                }.onFailure { error ->
                    toastMessage = "导入失败: ${error.message}"
                    toastColor = Color(0xFFFEE2E2)
                    showToast = true
                }
            }
        }
    }

    // 文件创建器 - 导出数据
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = DataExportImportHelper.exportData(context)
                result.onSuccess { file ->
                    // 复制文件到用户选择的位置
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        file.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    toastMessage = "数据导出成功"
                    toastColor = Color(0xFFDCFCE7)
                    showToast = true
                }.onFailure { error ->
                    toastMessage = "导出失败: ${error.message}"
                    toastColor = Color(0xFFFEE2E2)
                    showToast = true
                }
            }
        }
    }

    Box {
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
                        onClick = {
                            val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
                            exportLauncher.launch("Accelerator_Backup_$timestamp.Accele")
                        }
                    )

                    Divider(color = Color(0xFFE2E8F0))

                    // 数据恢复
                    DataManagementItem(
                        icon = Icons.Default.RestorePage,
                        title = "数据恢复",
                        subtitle = "从备份恢复学习数据",
                        onClick = {
                            importLauncher.launch("*/*")
                        }
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

                    // 打开用户数据文件夹
                    DataManagementItem(
                        icon = Icons.Default.FolderOpen,
                        title = "打开用户数据文件夹",
                        subtitle = "查看应用数据文件",
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
                        onButtonClick = { showClearCacheDialog = true }
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

        // Toast 提示
        if (showToast) {
            androidx.compose.material3.Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = toastColor,
                contentColor = Color(0xFF1E293B)
            ) {
                Text(toastMessage)
            }
        }
    }

    // 清除模型缓存确认对话框
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("确认清除") },
            text = { Text("确定要清除模型缓存吗？这将删除未完成的模型下载文件。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearCacheDialog = false
                        onClearModelCache()
                    }
                ) {
                    Text("确定", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("取消", color = Color(0xFF8B5CF6))
                }
            }
        )
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
