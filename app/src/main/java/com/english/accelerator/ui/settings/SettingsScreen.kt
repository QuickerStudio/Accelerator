package com.english.accelerator.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 模型状态显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Gemma 3n E2B 模型",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (modelState) {
                        is GemmaInferenceManager.ModelState.NotDownloaded -> "未下载"
                        is GemmaInferenceManager.ModelState.Downloading -> "下载中 ${(modelState.progress * 100).toInt()}%"
                        is GemmaInferenceManager.ModelState.Ready -> "已就绪"
                        is GemmaInferenceManager.ModelState.Error -> "错误"
                    },
                    fontSize = 14.sp,
                    color = when (modelState) {
                        is GemmaInferenceManager.ModelState.Ready -> Color(0xFF10B981)
                        is GemmaInferenceManager.ModelState.Error -> Color(0xFFEF4444)
                        else -> Color(0xFF64748B)
                    }
                )
            }

            // 状态图标
            Icon(
                imageVector = when (modelState) {
                    is GemmaInferenceManager.ModelState.NotDownloaded -> Icons.Default.CloudDownload
                    is GemmaInferenceManager.ModelState.Downloading -> Icons.Default.CloudDownload
                    is GemmaInferenceManager.ModelState.Ready -> Icons.Default.CheckCircle
                    is GemmaInferenceManager.ModelState.Error -> Icons.Default.Error
                },
                contentDescription = null,
                tint = when (modelState) {
                    is GemmaInferenceManager.ModelState.Ready -> Color(0xFF10B981)
                    is GemmaInferenceManager.ModelState.Error -> Color(0xFFEF4444)
                    else -> Color(0xFF8B5CF6)
                },
                modifier = Modifier.size(32.dp)
            )
        }

        // 下载进度条
        if (modelState is GemmaInferenceManager.ModelState.Downloading) {
            LinearProgressIndicator(
                progress = modelState.progress,
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = Color(0xFF8B5CF6),
                trackColor = Color(0xFFE2E8F0)
            )
        }

        // 模型信息
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModelInfoRow(label = "模型版本", value = "Gemma 3n E2B-it")
            ModelInfoRow(label = "模型大小", value = "~2-3 GB")
            ModelInfoRow(label = "上下文长度", value = "32K tokens")
            ModelInfoRow(label = "用途", value = "语法检查、写作建议")
            ModelInfoRow(label = "多模态支持", value = "文本、图像、音频、视频（即将推出）")
            ModelInfoRow(label = "下载线路", value = "双线路（国际/国内）")
            ModelInfoRow(label = "运行方式", value = "设备端推理")
        }

        // 操作按钮
        when (modelState) {
            is GemmaInferenceManager.ModelState.NotDownloaded -> {
                Button(
                    onClick = onDownload,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("下载模型")
                }
            }
            is GemmaInferenceManager.ModelState.Downloading -> {
                Button(
                    onClick = { /* TODO: 取消下载 */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF64748B)
                    ),
                    enabled = false
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("下载中...")
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
}

@Composable
private fun ModelInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B)
        )
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
