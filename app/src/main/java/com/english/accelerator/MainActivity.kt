package com.english.accelerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.english.accelerator.ui.components.BottomInputArea
import com.english.accelerator.ui.components.CustomToast
import com.english.accelerator.ui.components.ScreenshotNotification
import com.english.accelerator.ui.navigation.BottomNavigationBar
import com.english.accelerator.ui.navigation.Screen
import com.english.accelerator.ui.settings.SettingsScreen
import com.english.accelerator.ui.speaking.SpeakingScreen
import com.english.accelerator.ui.speaking.VoiceInputTestScreen
import com.english.accelerator.ui.theme.AcceleratorTheme
import com.english.accelerator.ui.vocabulary.VocabularyScreen
import com.english.accelerator.ui.writing.WritingScreen
import com.english.accelerator.ai.model.GemmaInferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close

class MainActivity : ComponentActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化基础设施系统（必须最先初始化）
        com.english.accelerator.utils.AppLogger.init(this)
        com.english.accelerator.utils.DConfig.init(this)
        com.english.accelerator.ai.model.ModelConfig.init(this)

        // 记录应用启动
        com.english.accelerator.utils.AppLogger.info("MainActivity", "Application started")

        // 初始化 AI 系统
        com.english.accelerator.ai.session.SessionManager.init(this)
        com.english.accelerator.ai.history.HistoryManager.init(this)

        // 初始化 WordLearningManager
        com.english.accelerator.data.WordLearningManager.init(this)

        // 初始化 WordRepository
        com.english.accelerator.data.WordRepository.init(this)

        // 初始化 BookmarkManager
        com.english.accelerator.data.BookmarkManager.init(this)

        // 初始化 EssayCollectionManager
        com.english.accelerator.data.EssayCollectionManager.init(this)

        // 初始化 GemmaInferenceManager
        com.english.accelerator.ai.model.GemmaInferenceManager.init(this)

        // 初始化 WordLoader（中间件层）
        com.english.accelerator.utils.WordLoader.init(this)

        // 自动初始化模型（如果已下载）
        scope.launch {
            val gemmaManager = com.english.accelerator.ai.model.GemmaInferenceManager.getInstance()
            if (gemmaManager.isModelDownloaded()) {
                gemmaManager.initialize()
            }
        }

        enableEdgeToEdge()
        setContent {
            AcceleratorTheme {
                AcceleratorApp()
            }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        com.english.accelerator.ai.model.GemmaInferenceManager.getInstance().onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        com.english.accelerator.ai.model.GemmaInferenceManager.getInstance().cleanup()
    }
}

@Composable
fun AcceleratorApp() {
    var currentRoute by rememberSaveable { mutableStateOf(Screen.Vocabulary.route) }
    var showInputArea by rememberSaveable { mutableStateOf(false) }
    var hideBottomBar by rememberSaveable { mutableStateOf(false) }

    // Toast 状态
    var toastMessage by remember { mutableStateOf("") }
    var toastBackgroundColor by remember { mutableStateOf(Color.White) }
    var showToast by remember { mutableStateOf(false) }

    // 截图通知状态
    var screenshotFile by remember { mutableStateOf<File?>(null) }
    var showImageViewer by remember { mutableStateOf(false) }

    // 临时测试模式 - 设置为 true 启用测试界面
    val testMode = false

    if (testMode) {
        VoiceInputTestScreen()
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column {
                // 底部输入区域
                AnimatedVisibility(
                    visible = showInputArea,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    BottomInputArea(
                        modifier = Modifier.padding(bottom = 15.dp),
                        onShowToast = { message, color ->
                            toastMessage = message
                            toastBackgroundColor = color
                            showToast = true
                        },
                        onScreenshotCaptured = { file ->
                            screenshotFile = file
                        }
                    )
                }

                // 底部导航栏（带淡入淡出动画）
                AnimatedVisibility(
                    visible = !hideBottomBar,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            // 切换路由时关闭输入框
                            if (route != Screen.Vocabulary.route) {
                                showInputArea = false
                            }
                            currentRoute = route
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentRoute) {
                Screen.Vocabulary.route -> VocabularyScreen(
                    showInputArea = showInputArea,
                    onToggleInputArea = { showInputArea = !showInputArea },
                    onNavigateToSettings = { currentRoute = Screen.Settings.route }
                )
                Screen.Writing.route -> WritingScreen(
                    onNavigateToSettings = { currentRoute = Screen.Settings.route },
                    onKeyboardVisibilityChanged = { isVisible ->
                        hideBottomBar = isVisible
                    }
                )
                Screen.Speaking.route -> SpeakingScreen(
                    onNavigateToSettings = { currentRoute = Screen.Settings.route }
                )
                Screen.Settings.route -> SettingsScreen()
            }

            // Toast 提示（全局显示）
            if (showToast) {
                com.english.accelerator.ui.components.CustomToast(
                    message = toastMessage,
                    visible = showToast,
                    onDismiss = { showToast = false },
                    backgroundColor = toastBackgroundColor,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp)
                )
            }

            // 截图通知（仅在单词页面显示）
            if (currentRoute == Screen.Vocabulary.route && screenshotFile != null) {
                val context = androidx.compose.ui.platform.LocalContext.current
                ScreenshotNotification(
                    imageFile = screenshotFile!!,
                    onDismiss = { screenshotFile = null },
                    onOpenImage = {
                        showImageViewer = true
                    },
                    context = context
                )
            }

            // 图片查看器
            if (showImageViewer && screenshotFile != null) {
                ImageViewDialog(
                    imageFile = screenshotFile!!,
                    onDismiss = {
                        showImageViewer = false
                        screenshotFile = null
                    }
                )
            }
        }
    }
}
@Composable
private fun ImageViewDialog(
    imageFile: File,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        androidx.compose.material3.Surface(
            modifier = Modifier
                .fillMaxSize(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            color = Color(0xFF1E293B)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val bitmap = remember(imageFile) {
                    try {
                        android.graphics.BitmapFactory.decodeFile(imageFile.absolutePath)
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = imageFile.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }

                // 关闭按钮
                androidx.compose.material3.IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
