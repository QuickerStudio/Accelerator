package com.english.accelerator.ui.settings

/**
 * AI Model Management Card
 *
 * TODO:
 * - [ ] Implement resume download (断点续传)
 * - [ ] Add pause/cancel download functionality
 * - [ ] Support multiple model versions
 * - [ ] Add model update check
 * - [ ] Implement model compression/decompression
 * - [ ] Add download speed display
 * - [ ] Support model switching (Gemma 2B, 3n E2B, etc.)
 * - [ ] Add model performance metrics display
 * - [ ] Implement automatic cleanup of old models
 * - [ ] Add model integrity verification (checksum)
 *
 * Design Philosophy:
 * - Minimal UI: Only essential information displayed
 * - Progressive disclosure: Advanced features hidden until needed
 * - Clear status indication: Visual feedback for all states
 * - Safe deletion: Long press (10s) prevents accidental deletion
 * - Dual progress indicators: Circular (icon) + Linear (bar) for clarity
 *
 * Current Features:
 * - Download model by clicking cloud icon
 * - Real-time download progress (circular + linear indicators)
 * - Long press green checkmark for 10 seconds to delete model
 * - Error state with retry capability
 * - Dual-route download (Hugging Face + ModelScope fallback)
 *
 * Architecture:
 * - Separated from SettingsScreen for better maintainability
 * - Uses GemmaInferenceManager for state management
 * - Follows Material Design 3 guidelines
 * - Supports future expansion without breaking existing UI
 */

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.ai.GemmaInferenceManager
import kotlinx.coroutines.delay

/**
 * AI Model Management Card
 *
 * Features:
 * - Download model by clicking when not downloaded
 * - Show download progress with circular indicator and progress bar
 * - Display model status (Not Downloaded, Downloading, Ready, Error)
 * - Long press (10s) on green checkmark to delete model
 *
 * Future enhancements:
 * - Resume download support
 * - Pause/cancel download
 * - Model version management
 * - Storage optimization
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModelManagementCard(
    modelState: GemmaInferenceManager.ModelState,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onInitialize: () -> Unit,
    onPause: () -> Unit = {},
    onResume: () -> Unit = {},
    onSwitchRoute: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var longPressProgress by remember { mutableStateOf(0f) }
    var isLongPressing by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    // Long press timer
    LaunchedEffect(isLongPressing) {
        if (isLongPressing) {
            val startTime = System.currentTimeMillis()
            while (isLongPressing && longPressProgress < 1f) {
                delay(50)
                val elapsed = System.currentTimeMillis() - startTime
                longPressProgress = (elapsed / 10000f).coerceIn(0f, 1f)

                if (longPressProgress >= 1f) {
                    showDeleteDialog = true
                    isLongPressing = false
                    longPressProgress = 0f
                }
            }
        } else {
            longPressProgress = 0f
        }
    }

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
                        // 绿色圆形背景 + 对号（支持长按删除）
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .combinedClickable(
                                    onClick = { /* 短按无操作 */ },
                                    onLongClick = { isLongPressing = true },
                                    onLongClickLabel = "长按10秒删除模型"
                                )
                                .background(Color(0xFF10B981), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // 长按进度环
                            if (longPressProgress > 0f) {
                                CircularProgressIndicator(
                                    progress = longPressProgress,
                                    modifier = Modifier.size(32.dp),
                                    color = Color(0xFFEF4444),
                                    strokeWidth = 3.dp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "已下载",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    is GemmaInferenceManager.ModelState.Error -> {
                        // 错误图标（可点击重试）
                        IconButton(onClick = onDownload) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "下载失败，点击重试",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(32.dp)
                            )
                        }
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

            // 下载控制按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 暂停/恢复按钮
                Button(
                    onClick = {
                        if (isPaused) {
                            onResume()
                            isPaused = false
                        } else {
                            onPause()
                            isPaused = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPaused) Color(0xFF10B981) else Color(0xFFF59E0B)
                    )
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "恢复" else "暂停",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isPaused) "恢复" else "暂停")
                }

                // 切换线路按钮
                Button(
                    onClick = onSwitchRoute,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "切换线路",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("切换线路")
                }
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除模型") },
            text = {
                Text("确定要删除 AI 模型吗？删除后需要重新下载才能使用 AI 功能。\n\n模型大小：~2-3 GB")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
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
