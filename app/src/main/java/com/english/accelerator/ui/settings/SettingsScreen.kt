package com.english.accelerator.ui.settings

/**
 * Settings Screen
 *
 * TODO:
 * - Add more settings sections (Learning, Notifications, etc.)
 * - Implement settings persistence with DataStore
 * - Add settings search functionality
 * - Add settings backup/restore
 *
 * Design:
 * - Clean, minimal design following Material Design 3
 * - Grouped settings with section titles
 * - AI Model Management Card extracted to separate file for better maintainability
 * - Future: Support for multiple models, model versioning, and advanced download options
 */

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
import com.english.accelerator.ai.model.GemmaInferenceManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val gemmaManager = remember { GemmaInferenceManager.getInstance() }
    val modelState by gemmaManager.modelState.collectAsState()
    val modelDownloadManager = remember { com.english.accelerator.ai.downloader.DManager(context) }
    val scope = rememberCoroutineScope()

    // 从 DConfig 恢复下载状态
    val savedState = remember { modelDownloadManager.getFullState() }

    var isDownloading by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(savedState.configState?.isPaused ?: false) }
    var isError by remember { mutableStateOf(false) }
    var downloadProgress by remember {
        mutableStateOf(
            if (savedState.fileSize > 0 && savedState.expectedSize > 0) {
                savedState.fileSize.toFloat() / savedState.expectedSize.toFloat()
            } else {
                0f
            }
        )
    }
    var downloadSpeed by remember { mutableStateOf(0L) }
    var currentRoute by remember { mutableStateOf(modelDownloadManager.getCurrentRouteName()) }

    // 使用新的下载状态判断
    var downloadStatus by remember { mutableStateOf(modelDownloadManager.getDStatus()) }
    val isDownloadComplete = downloadStatus == com.english.accelerator.ai.downloader.DStatus.COMPLETE

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
            ModelDownloadCard(
                isDownloaded = isDownloadComplete,
                isDownloading = isDownloading,
                isPaused = isPaused,
                isError = isError,
                downloadProgress = downloadProgress,
                downloadSpeed = downloadSpeed,
                currentRoute = currentRoute,
                hasCache = downloadProgress > 0f || downloadStatus == com.english.accelerator.ai.downloader.DStatus.PARTIAL,
                onDownloadClick = {
                    when {
                        isError -> {
                            // 重试
                            isError = false
                            downloadStatus = com.english.accelerator.ai.downloader.DStatus.NOT_DOWNLOADED
                            scope.launch {
                                isDownloading = true
                                modelDownloadManager.downloadModel { downloaded, total, speed ->
                                    downloadProgress = if (total > 0) downloaded.toFloat() / total else 0f
                                    downloadSpeed = speed
                                }.onSuccess {
                                    isDownloading = false
                                    downloadStatus = modelDownloadManager.getDStatus()
                                }.onFailure {
                                    isDownloading = false
                                    isError = true
                                }
                            }
                        }
                        isPaused -> {
                            // 继续
                            modelDownloadManager.resumeDownload()
                            isPaused = false
                        }
                        isDownloading -> {
                            // 暂停
                            modelDownloadManager.pauseDownload()
                            isPaused = true
                        }
                        else -> {
                            // 开始下载
                            downloadStatus = com.english.accelerator.ai.downloader.DStatus.NOT_DOWNLOADED
                            scope.launch {
                                isDownloading = true
                                modelDownloadManager.downloadModel { downloaded, total, speed ->
                                    downloadProgress = if (total > 0) downloaded.toFloat() / total else 0f
                                    downloadSpeed = speed
                                }.onSuccess {
                                    isDownloading = false
                                    downloadStatus = modelDownloadManager.getDStatus()
                                }.onFailure {
                                    isDownloading = false
                                    isError = true
                                }
                            }
                        }
                    }
                },
                onSwitchRoute = {
                    modelDownloadManager.switchRoute()
                    currentRoute = modelDownloadManager.getCurrentRouteName()
                },
                onDelete = {
                    modelDownloadManager.deleteModel()
                    downloadStatus = modelDownloadManager.getDStatus()
                },
                onLoadModel = {
                    scope.launch {
                        gemmaManager.initialize()
                    }
                },
                onClearCache = {
                    // 长按5秒清空下载缓存
                    scope.launch {
                        modelDownloadManager.cancelDownload()
                        modelDownloadManager.deleteModel()
                        isDownloading = false
                        isPaused = false
                        isError = false
                        downloadProgress = 0f
                        downloadSpeed = 0L
                        downloadStatus = modelDownloadManager.getDStatus()
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
