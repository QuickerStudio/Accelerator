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
    val scope = rememberCoroutineScope()

    // 文件浏览器对话框状态
    var showFileExplorer by remember { mutableStateOf(false) }

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
                onLoadModel = {
                    scope.launch {
                        gemmaManager.initialize()
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

        // 关于部分
        SettingsSection(title = "关于") {
            AboutCard()
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
