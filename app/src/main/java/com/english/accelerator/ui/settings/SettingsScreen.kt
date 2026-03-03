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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val gemmaManager = remember { GemmaInferenceManager.getInstance() }
    val modelState by gemmaManager.modelState.collectAsState()
    val modelDownloadManager = remember { com.english.accelerator.ai.downloader.DManager(context) }
    val scope = rememberCoroutineScope()

    // 核心状态：只使用 downloadStatus 作为唯一真相来源
    var downloadStatus by remember { mutableStateOf(modelDownloadManager.getDStatus()) }
    var downloadProgress by remember {
        val state = modelDownloadManager.getFullState()
        mutableStateOf(
            if (state.fileSize > 0 && state.expectedSize > 0) {
                state.fileSize.toFloat() / state.expectedSize.toFloat()
            } else {
                0f
            }
        )
    }
    var downloadSpeed by remember { mutableStateOf(0L) }
    var currentRoute by remember { mutableStateOf(modelDownloadManager.getCurrentRouteName()) }
    var showFileExplorer by remember { mutableStateOf(false) }

    // 从 downloadStatus 派生所有 UI 状态
    val isDownloaded = downloadStatus == com.english.accelerator.ai.downloader.DStatus.COMPLETE
    val isDownloading = downloadStatus == com.english.accelerator.ai.downloader.DStatus.DOWNLOADING
    val isPaused = downloadStatus == com.english.accelerator.ai.downloader.DStatus.PARTIAL
    val hasCache = isPaused || isDownloading

    // 定时刷新进度（仅在下载中或暂停时）
    LaunchedEffect(downloadStatus) {
        while (downloadStatus == com.english.accelerator.ai.downloader.DStatus.DOWNLOADING ||
               downloadStatus == com.english.accelerator.ai.downloader.DStatus.PARTIAL) {
            delay(1000)
            val state = modelDownloadManager.getFullState()
            if (state.expectedSize > 0) {
                downloadProgress = state.fileSize.toFloat() / state.expectedSize.toFloat()
            }
            downloadStatus = modelDownloadManager.getDStatus()
        }
    }

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
                isDownloaded = isDownloaded,
                isDownloading = isDownloading,
                isPaused = isPaused,
                isError = false,
                downloadProgress = downloadProgress,
                downloadSpeed = downloadSpeed,
                currentRoute = currentRoute,
                hasCache = hasCache,
                onDownloadClick = {
                    when (downloadStatus) {
                        com.english.accelerator.ai.downloader.DStatus.PARTIAL -> {
                            // 继续下载
                            scope.launch {
                                downloadStatus = com.english.accelerator.ai.downloader.DStatus.DOWNLOADING
                                modelDownloadManager.downloadModel { downloaded, total, speed ->
                                    downloadSpeed = speed
                                }.onSuccess {
                                    downloadStatus = modelDownloadManager.getDStatus()
                                }.onFailure {
                                    downloadStatus = modelDownloadManager.getDStatus()
                                }
                            }
                        }
                        com.english.accelerator.ai.downloader.DStatus.DOWNLOADING -> {
                            // 暂停
                            modelDownloadManager.pauseDownload()
                            downloadStatus = com.english.accelerator.ai.downloader.DStatus.PARTIAL
                        }
                        else -> {
                            // 开始下载
                            scope.launch {
                                downloadStatus = com.english.accelerator.ai.downloader.DStatus.DOWNLOADING
                                modelDownloadManager.downloadModel { downloaded, total, speed ->
                                    downloadSpeed = speed
                                }.onSuccess {
                                    downloadStatus = modelDownloadManager.getDStatus()
                                }.onFailure {
                                    downloadStatus = modelDownloadManager.getDStatus()
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
                    scope.launch {
                        modelDownloadManager.cancelDownload()
                        modelDownloadManager.deleteModel()
                        downloadSpeed = 0L
                        downloadStatus = modelDownloadManager.getDStatus()
                    }
                },
                onOpenDirectory = {
                    showFileExplorer = true
                }
            )
        }

        // 文件浏览器对话框
        if (showFileExplorer) {
            FileExplorerDialog(
                rootPath = context.filesDir.absolutePath,
                onDismiss = { showFileExplorer = false }
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
