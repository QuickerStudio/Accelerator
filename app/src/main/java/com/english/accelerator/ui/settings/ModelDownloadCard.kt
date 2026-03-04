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
import com.english.accelerator.ai.downloader.DownloadService
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

    // 初始化状态 - 从 DManager 获取真实状态
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
    var lastSpeedUpdateTime by remember { mutableStateOf(0L) }
    var currentRoute by remember { mutableStateOf(dManager.getCurrentRouteName()) }
    var supportsRange by remember { mutableStateOf(true) } // 默认假设支持

    // 从配置文件获取下载状态，而不是从 DEngine
    var isDownloading by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    // 从 downloadStatus 派生所有 UI 状态
    val isDownloaded = downloadStatus == DStatus.COMPLETE
    val hasCache = downloadStatus == DStatus.PARTIAL

    // 定时刷新进度和状态（每秒检测文件大小计算百分比）
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            val state = dManager.getFullState()
            if (state.expectedSize > 0) {
                downloadProgress = state.fileSize.toFloat() / state.expectedSize.toFloat()
            }
            downloadStatus = dManager.getDStatus()
        }
    }

    // 单独的状态同步（从配置文件读取按钮状态）
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            val configState = dManager.getFullState().configState
            if (configState != null) {
                isPaused = configState.isPaused
                isDownloading = !configState.isPaused && !configState.isComplete && downloadStatus == DStatus.PARTIAL
            }

            // 检查当前线路是否支持断点续传
            val routes = dManager.getDownloadRoutes()
            val currentRouteInfo = routes.find { it.name == currentRoute }
            if (currentRouteInfo != null && currentRouteInfo.rangeChecked) {
                supportsRange = currentRouteInfo.supportsRange
            }
        }
    }

    // 内部业务逻辑
    fun handleDownloadClick() {
        if (isDownloading) {
            // 暂停下载
            dManager.pauseDownload()
            isDownloading = false
            isPaused = true
        } else if (isPaused) {
            // 恢复下载
            isDownloading = true
            isPaused = false
            dManager.resumeDownload()
            scope.launch {
                dManager.downloadModel { downloaded, total, speed ->
                    // 降低网速更新频率，避免数字跳动太快（每2秒更新一次）
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastSpeedUpdateTime >= 2000) {
                        downloadSpeed = speed
                        lastSpeedUpdateTime = currentTime
                    }
                }.onSuccess {
                    isDownloading = false
                    downloadStatus = dManager.getDStatus()
                }.onFailure {
                    isDownloading = false
                    downloadStatus = dManager.getDStatus()
                }
            }
        } else {
            // 开始下载
            isDownloading = true
            scope.launch {
                dManager.downloadModel { downloaded, total, speed ->
                    // 降低网速更新频率，避免数字跳动太快（每2秒更新一次）
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastSpeedUpdateTime >= 2000) {
                        downloadSpeed = speed
                        lastSpeedUpdateTime = currentTime
                    }
                }.onSuccess {
                    isDownloading = false
                    downloadStatus = dManager.getDStatus()
                }.onFailure {
                    isDownloading = false
                    downloadStatus = dManager.getDStatus()
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

    // 单行布局：标题 + 进度/网速 + 线路切换按钮 + 下载控制按钮
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：动画标题 + 进度/网速
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B5CF6)
                )

                // 显示进度百分比和网速（下载中或暂停时）
                if (isDownloading || isPaused) {
                    Text(
                        text = String.format("%.3f%%", downloadProgress * 100),
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )

                    if (isDownloading && downloadSpeed > 0) {
                        Text(
                            text = formatSpeed(downloadSpeed),
                            fontSize = 14.sp,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }

            // 右侧：线路切换按钮 + 下载控制按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            // 下载控制按钮区域
            when {
                // 已下载：显示加载模型和清除模型按钮
                isDownloaded -> {
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

                // 未下载或下载中：显示下载按钮
                else -> {
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
                }
            }
        }

        // 副标题提示：断点续传支持状态
        if (!isDownloaded) {
            Text(
                text = if (supportsRange) {
                    "✓ 支持断点续传"
                } else {
                    "⚠ 不支持断点续传，暂停会重新下载"
                },
                fontSize = 12.sp,
                color = if (supportsRange) Color(0xFF10B981) else Color(0xFFF59E0B),
                modifier = Modifier.padding(start = 4.dp)
            )
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
