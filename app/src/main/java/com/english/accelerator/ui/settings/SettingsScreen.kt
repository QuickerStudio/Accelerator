package com.english.accelerator.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.english.accelerator.ai.GemmaInferenceManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()
    val gemmaManager = remember { GemmaInferenceManager.getInstance() }
    val modelState by gemmaManager.modelState.collectAsState()
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        Text(
            text = "设置",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // AI 模型管理部分
        SettingsSection(title = "AI 模型管理") {
            ModelManagementCard(
                modelState = modelState,
                onDownload = {
                    scope.launch {
                        gemmaManager.downloadModel()
                    }
                },
                onDelete = {
                    showDeleteDialog = true
                },
                onInitialize = {
                    scope.launch {
                        gemmaManager.initialize()
                    }
                }
            )
        }

        // 学习设置部分
        SettingsSection(title = "学习设置") {
            SettingsItem(
                icon = Icons.Default.Book,
                title = "单词复习比例",
                subtitle = "每 8 个新单词插入 1 个复习单词",
                onClick = { /* TODO: 打开复习比例设置 */ }
            )
            Divider(color = Color(0xFFE2E8F0))
            SettingsItem(
                icon = Icons.Default.Timer,
                title = "学习提醒",
                subtitle = "设置每日学习提醒时间",
                onClick = { /* TODO: 打开提醒设置 */ }
            )
        }

        // 关于部分
        SettingsSection(title = "关于") {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "版本信息",
                subtitle = "v0.5.0",
                onClick = { /* TODO: 显示版本详情 */ }
            )
            Divider(color = Color(0xFFE2E8F0))
            SettingsItem(
                icon = Icons.Default.Code,
                title = "开源许可",
                subtitle = "查看开源组件许可信息",
                onClick = { /* TODO: 显示许可信息 */ }
            )
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除模型") },
            text = { Text("确定要删除 AI 模型吗？删除后需要重新下载才能使用 AI 功能。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: 实现删除功能
                        showDeleteDialog = false
                    }
                ) {
                    Text("删除", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ModelManagementCard(
    modelState: GemmaInferenceManager.ModelState,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onInitialize: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 主行：标题 + 状态 + 图标
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = modelState is GemmaInferenceManager.ModelState.NotDownloaded,
                    onClick = onDownload
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：标题 + 状态
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI 模型",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (modelState) {
                        is GemmaInferenceManager.ModelState.NotDownloaded -> "点击下载"
                        is GemmaInferenceManager.ModelState.Downloading -> "下载中 ${(modelState.progress * 100).toInt()}%"
                        is GemmaInferenceManager.ModelState.Ready -> "Gemma 3n E2B"
                        is GemmaInferenceManager.ModelState.Error -> "下载失败"
                    },
                    fontSize = 14.sp,
                    color = when (modelState) {
                        is GemmaInferenceManager.ModelState.Ready -> Color(0xFF10B981)
                        is GemmaInferenceManager.ModelState.Error -> Color(0xFFEF4444)
                        else -> Color(0xFF64748B)
                    }
                )
            }

            // 右侧：状态图标
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                when (modelState) {
                    is GemmaInferenceManager.ModelState.NotDownloaded -> {
                        // 云朵图标（未下载）
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = "下载模型",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    is GemmaInferenceManager.ModelState.Downloading -> {
                        // 下载进度圆环
                        CircularProgressIndicator(
                            progress = modelState.progress,
                            modifier = Modifier.size(32.dp),
                            color = Color(0xFF8B5CF6),
                            strokeWidth = 3.dp
                        )
                    }
                    is GemmaInferenceManager.ModelState.Ready -> {
                        // 绿色圆形背景 + 对号
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF10B981), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "已下载",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    is GemmaInferenceManager.ModelState.Error -> {
                        // 错误图标
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "下载失败",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // 下载进度条（仅在下载时显示）
        if (modelState is GemmaInferenceManager.ModelState.Downloading) {
            LinearProgressIndicator(
                progress = modelState.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFF8B5CF6),
                trackColor = Color(0xFFE2E8F0)
            )
        }
    }
}

                }
            }
            is GemmaInferenceManager.ModelState.Ready -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFEF4444)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("删除")
                    }
                    Button(
                        onClick = onInitialize,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("重新初始化")
                    }
                }
            }
            is GemmaInferenceManager.ModelState.Error -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "错误: ${modelState.message}",
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444)
                    )
                    Button(
                        onClick = onDownload,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B5CF6)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("重试下载")
                    }
                }
            }
        }
    }

@Composable
private fun SettingsItem(
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
