package com.english.accelerator.ui.settings

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 模型下载卡片
 *
 * 功能：
 * - 动画标题（智能老师 ⇄ 智慧之源）
 * - 下载按钮（下载/暂停/继续/重试）
 * - 长按5秒清空缓存（带环状进度条倒计时）
 * - 线路切换按钮（下载时锁定）
 * - 进度条和网速显示
 * - 下载完成后显示：加载模型 + 清除模型按钮
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModelDownloadCard(
    isDownloaded: Boolean,
    isDownloading: Boolean,
    isPaused: Boolean,
    isError: Boolean,
    downloadProgress: Float,
    downloadSpeed: Long,
    currentRoute: String,
    onDownloadClick: () -> Unit,
    onSwitchRoute: () -> Unit,
    onDelete: () -> Unit,
    onLoadModel: () -> Unit,
    onClearCache: () -> Unit  // 新增：清空缓存回调
) {
    var currentTitle by remember { mutableStateOf("智能老师") }

    // 长按清空缓存的状态
    var isLongPressing by remember { mutableStateOf(false) }
    var longPressProgress by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    // 标题动画切换
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentTitle = if (currentTitle == "智能老师") "智慧之源" else "智能老师"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 第一行：动画标题 + 线路切换按钮 + 下载按钮/对勾按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：动画标题 + 线路切换按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 动画标题
                Text(
                    text = currentTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B5CF6)
                )

                // 线路切换按钮（下载完成后隐藏）
                if (!isDownloaded) {
                    TextButton(
                        onClick = onSwitchRoute,
                        enabled = !isDownloading || isPaused,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF8B5CF6),
                            disabledContentColor = Color(0xFF94A3B8)
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "切换线路",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentRoute,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 右侧：下载按钮或模型管理按钮
            when {
                // 已下载：显示加载模型和清除模型按钮
                isDownloaded -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 加载模型按钮
                        Button(
                            onClick = onLoadModel,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8B5CF6)
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "加载模型",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "加载模型",
                                fontSize = 14.sp
                            )
                        }

                        // 清除模型按钮
                        OutlinedButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFEF4444)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "清除模型",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "清除",
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // 未下载或下载中：显示下载按钮（带长按清空缓存功能）
                else -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 下载/暂停按钮（带长按清空缓存功能）
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 环状进度条（长按时显示）
                            if (isLongPressing) {
                                CircularProgressIndicator(
                                    progress = { longPressProgress },
                                    modifier = Modifier.size(48.dp),
                                    color = Color(0xFFEF4444),
                                    strokeWidth = 3.dp,
                                    trackColor = Color(0xFFE2E8F0)
                                )
                            }

                            // 按钮
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = {
                                                // 短按：正常点击
                                                onDownloadClick()
                                            },
                                            onPress = {
                                                // 长按开始
                                                if (isPaused || isDownloading) {
                                                    isLongPressing = true
                                                    longPressProgress = 0f

                                                    // 启动倒计时协程
                                                    val job = coroutineScope.launch {
                                                        val totalDuration = 5000L // 5秒
                                                        val updateInterval = 50L // 每50ms更新一次
                                                        val steps = totalDuration / updateInterval

                                                        for (i in 1..steps) {
                                                            if (!isLongPressing) break
                                                            delay(updateInterval)
                                                            longPressProgress = i.toFloat() / steps.toFloat()
                                                        }

                                                        // 倒计时完成，执行清空缓存
                                                        if (isLongPressing && longPressProgress >= 1f) {
                                                            onClearCache()
                                                        }

                                                        // 重置状态
                                                        isLongPressing = false
                                                        longPressProgress = 0f
                                                    }

                                                    // 等待手指抬起
                                                    tryAwaitRelease()

                                                    // 手指抬起，取消倒计时
                                                    isLongPressing = false
                                                    longPressProgress = 0f
                                                    job.cancel()
                                                }
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isLongPressing -> Icons.Default.Delete
                                        isError -> Icons.Default.Refresh
                                        isPaused -> Icons.Default.PlayArrow
                                        isDownloading -> Icons.Default.Pause
                                        else -> Icons.Default.CloudDownload
                                    },
                                    contentDescription = when {
                                        isLongPressing -> "清空缓存"
                                        isError -> "重试"
                                        isPaused -> "继续"
                                        isDownloading -> "暂停"
                                        else -> "下载"
                                    },
                                    tint = when {
                                        isLongPressing -> Color(0xFFEF4444)
                                        isError -> Color(0xFFEF4444)
                                        isPaused -> Color(0xFF10B981)
                                        isDownloading -> Color(0xFFF59E0B)
                                        else -> Color(0xFF8B5CF6)
                                    },
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        // 状态文本
                        Text(
                            text = when {
                                isLongPressing -> "松开取消"
                                isDownloaded -> "已就绪"
                                isError -> "请重试"
                                isPaused -> "已暂停"
                                isDownloading -> "正在下载..."
                                else -> "开始下载"
                            },
                            fontSize = 14.sp,
                            color = when {
                                isLongPressing -> Color(0xFFEF4444)
                                isDownloaded -> Color(0xFF10B981)
                                isError -> Color(0xFFEF4444)
                                else -> Color(0xFF64748B)
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 进度条和网速（仅在下载时显示）
        if (isDownloading && !isDownloaded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LinearProgressIndicator(
                    progress = { downloadProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color(0xFF8B5CF6),
                    trackColor = Color(0xFFE2E8F0)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = String.format("%.2f%%", downloadProgress * 100),
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = formatSpeed(downloadSpeed),
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

/**
 * 格式化下载速度
 */
private fun formatSpeed(bytesPerSecond: Long): String {
    return when {
        bytesPerSecond < 1024 -> "${bytesPerSecond} B/s"
        bytesPerSecond < 1024 * 1024 -> String.format("%.1f KB/s", bytesPerSecond / 1024.0)
        else -> String.format("%.1f MB/s", bytesPerSecond / (1024.0 * 1024.0))
    }
}
