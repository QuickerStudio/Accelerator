package com.english.accelerator.ui.settings

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.ai.downloader.DManager
import com.english.accelerator.ai.downloader.DStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 模型下载卡片 - 自包含组件
 *
 * 功能：
 * - 内部管理所有下载状态和逻辑
 * - 动画标题（智能老师 ⇄ 智慧之源）
 * - 下载按钮（下载/暂停/继续/重试）
 * - 清空缓存按钮（有缓存文件时显示）
 * - 线路切换按钮（下载时锁定）
 * - 进度显示和网速显示
 * - 下载完成后显示：加载模型 + 清除模型按钮
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModelDownloadCard(
    onLoadModel: () -> Unit,
    onOpenDirectory: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dManager = remember { DManager(context) }

    // 初始化状态
    var downloadStatus by remember { mutableStateOf(dManager.getDStatus()) }
    var downloadProgress by remember {
        val state = dManager.getFullState()
        mutableStateOf(
            if (state.fileSize > 0 && state.expectedSize > 0) {
                state.fileSize.toFloat() / state.expectedSize.toFloat()
            } else {
                0f
            }
        )
    }
    var downloadSpeed by remember { mutableStateOf(0L) }
    var currentRoute by remember { mutableStateOf(dManager.getCurrentRouteName()) }

    // 从 downloadStatus 派生所有 UI 状态
    val isDownloaded = downloadStatus == DStatus.COMPLETE
    val isDownloading = downloadStatus == DStatus.DOWNLOADING
    val isPaused = downloadStatus == DStatus.PARTIAL
    val hasCache = isPaused || isDownloading

    // 定时刷新进度（仅在下载中或暂停时）
    LaunchedEffect(downloadStatus) {
        while (downloadStatus == DStatus.DOWNLOADING || downloadStatus == DStatus.PARTIAL) {
            delay(1000)
            val state = dManager.getFullState()
            if (state.expectedSize > 0) {
                downloadProgress = state.fileSize.toFloat() / state.expectedSize.toFloat()
            }
            downloadStatus = dManager.getDStatus()
        }
    }

    // 内部业务逻辑
    fun handleDownloadClick() {
        when (downloadStatus) {
            DStatus.PARTIAL -> {
                // 继续下载
                scope.launch {
                    downloadStatus = DStatus.DOWNLOADING
                    dManager.downloadModel { downloaded, total, speed ->
                        downloadSpeed = speed
                    }.onSuccess {
                        downloadStatus = dManager.getDStatus()
                    }.onFailure {
                        downloadStatus = dManager.getDStatus()
                    }
                }
            }
            DStatus.DOWNLOADING -> {
                // 暂停
                dManager.pauseDownload()
                downloadStatus = DStatus.PARTIAL
            }
            else -> {
                // 开始下载
                scope.launch {
                    downloadStatus = DStatus.DOWNLOADING
                    dManager.downloadModel { downloaded, total, speed ->
                        downloadSpeed = speed
                    }.onSuccess {
                        downloadStatus = dManager.getDStatus()
                    }.onFailure {
                        downloadStatus = dManager.getDStatus()
                    }
                }
            }
        }
    }

    fun handleSwitchRoute() {
        dManager.switchRoute()
        currentRoute = dManager.getCurrentRouteName()
    }

    fun handleDelete() {
        dManager.deleteModel()
        downloadStatus = dManager.getDStatus()
    }

    fun handleClearCache() {
        scope.launch {
            dManager.cancelDownload()
            dManager.deleteModel()
            downloadSpeed = 0L
            downloadStatus = dManager.getDStatus()
        }
    }

    var currentTitle by remember { mutableStateOf("智能老师") }

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
        // 第一行：动画标题 + 线路切换按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
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
                    onClick = ::handleSwitchRoute,
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

        // 第二行：按钮和状态
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                            onClick = ::handleDelete,
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

                // 未下载或下载中：显示下载按钮
                else -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 清空缓存按钮（有缓存文件时显示）
                        if (hasCache) {
                            IconButton(
                                onClick = ::handleClearCache,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "清空缓存",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // 🔧 临时：打开目录按钮（有缓存文件时显示）
                        if (hasCache) {
                            IconButton(
                                onClick = onOpenDirectory,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = "打开目录",
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // 下载/暂停/继续按钮
                        IconButton(
                            onClick = ::handleDownloadClick,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = when {
                                    isPaused -> Icons.Default.PlayArrow
                                    isDownloading -> Icons.Default.Pause
                                    else -> Icons.Default.CloudDownload
                                },
                                contentDescription = when {
                                    isPaused -> "继续"
                                    isDownloading -> "暂停"
                                    else -> "下载"
                                },
                                tint = when {
                                    isPaused -> Color(0xFF10B981)
                                    isDownloading -> Color(0xFFF59E0B)
                                    else -> Color(0xFF8B5CF6)
                                },
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // 状态文本
                        Text(
                            text = when {
                                isDownloaded -> "已就绪"
                                isPaused -> "已暂停 ${String.format("%.2f%%", downloadProgress * 100)}"
                                isDownloading -> "正在下载... ${String.format("%.2f%%", downloadProgress * 100)} ${formatSpeed(downloadSpeed)}"
                                else -> "开始下载"
                            },
                            fontSize = 14.sp,
                            color = when {
                                isDownloaded -> Color(0xFF10B981)
                                else -> Color(0xFF64748B)
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
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
