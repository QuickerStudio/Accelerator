package com.english.accelerator.ui.speaking

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.animation.core.*
import com.mikepenz.markdown.m3.Markdown
import com.english.accelerator.ui.sidebar.Sidebar
import com.english.accelerator.ui.components.CustomToast
import com.english.accelerator.ui.components.ScreenshotNotification
import com.english.accelerator.utils.rememberScreenshotCapture
import com.english.accelerator.ai.llm.ModelState
import com.english.accelerator.ai.session.SessionManager
import com.english.accelerator.ai.session.Session
import com.english.accelerator.ai.history.HistoryManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Speaking Screen - AI Conversation Practice
 *
 * White theme design with message bubbles
 * Bottom input area aligned with vocabulary screen
 */

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val inferenceStats: InferenceStats? = null
)

/**
 * Inference performance statistics
 */
data class InferenceStats(
    val startTime: Long,
    val endTime: Long,
    val tokensGenerated: Int,
    val memoryUsedMB: Long
) {
    val durationSeconds: Float
        get() = (endTime - startTime) / 1000f

    val tokensPerSecond: Float
        get() = if (durationSeconds > 0) tokensGenerated / durationSeconds else 0f
}

/**
 * 解析对话响应
 * 格式：
 * RESPONSE: [AI 的回复]
 * FEEDBACK: [语法和表达反馈]
 */
private fun parseConversationResponse(rawResponse: String): String {
    val lines = rawResponse.lines()
    val responseLine = lines.find { it.startsWith("RESPONSE:") }
    val feedbackLine = lines.find { it.startsWith("FEEDBACK:") }

    val response = responseLine?.removePrefix("RESPONSE:")?.trim() ?: rawResponse
    val feedback = feedbackLine?.removePrefix("FEEDBACK:")?.trim()

    return if (feedback != null && feedback.isNotBlank()) {
        "$response\n\n💡 反馈：$feedback"
    } else {
        response
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingScreen(
    onNavigateToSettings: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Use ViewModel for state management
    val viewModel = remember { ConversationViewModel(context) }
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var showSidebar by remember { mutableStateOf(false) }
    var isContinuousMode by remember { mutableStateOf(false) }
    var showHistoryScreen by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Toast 状态
    var toastMessage by remember { mutableStateOf("") }
    var toastBackgroundColor by remember { mutableStateOf(Color.White) }
    var showToast by remember { mutableStateOf(false) }

    // 截图状态
    var screenshotFile by remember { mutableStateOf<File?>(null) }
    var showImageViewer by remember { mutableStateOf(false) }

    // 截图功能
    val captureScreenshot = rememberScreenshotCapture(
        cropRatio = 3f / 5f, // 3:5 比例裁剪
        offsetDp = 10, // 向上偏移 10dp
        onSuccess = { file ->
            screenshotFile = file
        },
        onError = { error ->
            toastMessage = error
            toastBackgroundColor = Color(0xFFFEE2E2)
            showToast = true
        }
    )

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("对话", fontSize = 20.sp, fontWeight = FontWeight.Medium) },
                    navigationIcon = {
                        IconButton(onClick = { showSidebar = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // New conversation thread
                        IconButton(onClick = {
                            // Use ViewModel to create new session
                            viewModel.createNewSession()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "新建对话",
                                tint = Color(0xFF64748B)
                            )
                        }
                        // Toggle continuous conversation mode
                        IconButton(onClick = { isContinuousMode = !isContinuousMode }) {
                            Icon(
                                imageVector = if (isContinuousMode) Icons.Default.Phone else Icons.Default.PhonePaused,
                                contentDescription = "持续对话",
                                tint = if (isContinuousMode) Color(0xFF8B5CF6) else Color(0xFF64748B)
                            )
                        }
                        // Conversation history
                        IconButton(onClick = { showHistoryScreen = true }) {
                            Icon(Icons.Default.History, contentDescription = "对话历史")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color(0xFF1E293B),
                        navigationIconContentColor = Color(0xFF64748B),
                        actionIconContentColor = Color(0xFF64748B)
                    )
                )
            },
            bottomBar = {
                Column {
                    // Bottom input area (aligned with vocabulary screen)
                    BottomInputArea(
                        inputText = inputText,
                        onInputChange = { inputText = it },
                        onSend = {
                            if (inputText.isNotBlank()) {
                                val userMessage = inputText
                                inputText = ""
                                focusManager.clearFocus()

                                // Use ViewModel to send message
                                viewModel.sendMessage(userMessage)

                                // Scroll to bottom
                                scope.launch {
                                    listState.animateScrollToItem(messages.size)
                                }
                            }
                        },
                        onCamera = { captureScreenshot() },
                        onAttach = { /* TODO: Attach file */ }
                    )

                    Spacer(modifier = Modifier.height(45.dp))

                    // Bottom navigation bar
                    BottomNavigationBar(onNavigateToSettings = onNavigateToSettings)
                }
            }
        ) { paddingValues ->
            // Message list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                    },
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages.filter { it.content.isNotEmpty() }) { message ->
                    MessageBubble(message = message)
                }
            }
        }

        // Sidebar
        if (showSidebar) {
            Sidebar(
                isOpen = showSidebar,
                onClose = { showSidebar = false },
                onNavigateToSettings = onNavigateToSettings,
                isEditorMode = false,
                onEditorModeChange = { },
                editingNoteId = null,
                onEditingNoteIdChange = { },
                editorTitle = "",
                onEditorTitleChange = { },
                editorContent = "",
                onEditorContentChange = { }
            )
        }

        // Conversation history screen
        if (showHistoryScreen) {
            ConversationHistoryScreen(
                onBackClick = { showHistoryScreen = false },
                onConversationClick = { conversation ->
                    // TODO: Load conversation
                    showHistoryScreen = false
                }
            )
        }

        // Toast 提示
        if (showToast) {
            CustomToast(
                message = toastMessage,
                visible = showToast,
                onDismiss = { showToast = false },
                backgroundColor = toastBackgroundColor,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            )
        }

        // 截图通知
        screenshotFile?.let { file ->
            ScreenshotNotification(
                imageFile = file,
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
                },
                onShare = {
                    shareImage(context, screenshotFile!!)
                }
            )
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val isStreaming = !message.isFromUser && message.content.isNotEmpty() && message.inferenceStats == null

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser) {
            // AI avatar with gradient background
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
        ) {
            // Message bubble - no border, clean Markdown rendering
            Box(
                modifier = Modifier
                    .then(
                        if (message.isFromUser) {
                            Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 18.dp,
                                        topEnd = 18.dp,
                                        bottomStart = 18.dp,
                                        bottomEnd = 6.dp
                                    )
                                )
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                    )
                                )
                        } else {
                            Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 18.dp,
                                        topEnd = 18.dp,
                                        bottomStart = 6.dp,
                                        bottomEnd = 18.dp
                                    )
                                )
                                .background(Color(0xFFF8FAFC))
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Message content with Markdown support
                    if (message.isFromUser) {
                        // User messages: simple text
                        Text(
                            text = message.content,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    } else {
                        // AI messages: Markdown rendering
                        Markdown(
                            content = message.content,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }

                    // Typing indicator for streaming messages
                    if (isStreaming) {
                        Spacer(modifier = Modifier.width(6.dp))
                        TypingIndicator()
                    }
                }
            }

            // Timestamp and stats
            Row(
                modifier = Modifier.padding(top = 6.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeFormat.format(Date(message.timestamp)),
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                // Show inference stats for completed AI messages
                if (!message.isFromUser && message.inferenceStats != null) {
                    val stats = message.inferenceStats
                    Text(
                        text = "• ${String.format("%.1f", stats.durationSeconds)}s • ${String.format("%.1f", stats.tokensPerSecond)} tok/s • ${stats.memoryUsedMB}MB",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        if (message.isFromUser) {
            Spacer(modifier = Modifier.width(48.dp))
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(6.dp)
            .background(
                color = Color(0xFF8B5CF6).copy(alpha = alpha),
                shape = CircleShape
            )
    )
}

@Composable
fun LoadingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // AI avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF8B5CF6), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "AI",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF94A3B8), CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomInputArea(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onCamera: () -> Unit,
    onAttach: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRecording by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 文本输入框区域 (70%)
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(
                        start = 52.dp,
                        end = 16.dp,  // 减小右侧padding
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF1E293B)
                    ),
                    maxLines = 2,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (inputText.isEmpty()) {
                                Text(
                                    text = "发消息",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // 相机按钮（左侧悬浮在文本框内）
                IconButton(
                    onClick = onCamera,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterStart)
                        .offset(x = (-44).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "相机",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 语音按钮区域 (30%)
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        if (isRecording) Color(0xFFBFDBFE) else Color(0xFFE2E8F0)
                    )
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            Log.d("VoiceInput", "Voice button pressed")
                            val down = awaitFirstDown()
                            isRecording = true
                            focusManager.clearFocus()

                            // 等待松开
                            val up = waitForUpOrCancellation()
                            if (up != null) {
                                Log.d("VoiceInput", "Voice button released, sending")
                                isRecording = false
                                onSend()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "按住说话",
                    tint = if (isRecording) Color(0xFF8B5CF6) else Color(0xFF64748B),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 发送按钮（悬浮在文本框和语音按钮之间的间隙中）
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-120).dp)
                .clip(CircleShape)
                .background(
                    if (inputText.isNotEmpty()) Color(0xFF3B82F6) else Color(0xFFCBD5E1)
                ),
            enabled = inputText.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "发送",
                tint = if (inputText.isNotEmpty()) Color.White else Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(onNavigateToSettings: () -> Unit) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column {
            Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NavigationItem(
                    icon = Icons.Default.Book,
                    label = "单词",
                    selected = false,
                    onClick = { /* TODO: Navigate to vocabulary */ }
                )
                NavigationItem(
                    icon = Icons.Default.Edit,
                    label = "写作",
                    selected = false,
                    onClick = { /* TODO: Navigate to writing */ }
                )
                NavigationItem(
                    icon = Icons.Default.Chat,
                    label = "对话",
                    selected = true,
                    onClick = { }
                )
                NavigationItem(
                    icon = Icons.Default.Settings,
                    label = "设置",
                    selected = false,
                    onClick = onNavigateToSettings
                )
            }
        }
    }
}

@Composable
fun NavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight()
            .padding(vertical = 4.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color(0xFF8B5CF6) else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (selected) Color(0xFF8B5CF6) else Color(0xFF94A3B8)
        )
    }
}

// Conversation data class for history
data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val preview: String,
    val timestamp: Long = System.currentTimeMillis(),
    val messageCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationHistoryScreen(
    onBackClick: () -> Unit,
    onConversationClick: (Conversation) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { SessionManager.getInstance() }
    val historyManager = remember { HistoryManager.getInstance() }

    val sessions by sessionManager.sessionList.collectAsState()

    // Convert sessions to conversations with real data
    val conversations = remember(sessions) {
        sessions
            .filter { it.type == Session.Type.CONVERSATION }
            .map { session ->
                val history = historyManager.getHistory(session.id)
                val messageCount = history?.messages?.size ?: 0
                val preview = history?.messages?.lastOrNull()?.content?.take(50) ?: "No messages yet"

                Conversation(
                    id = session.id,
                    title = session.title,
                    preview = preview,
                    timestamp = session.createdAt,
                    messageCount = messageCount
                )
            }
            .sortedByDescending { it.timestamp }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("对话历史", fontSize = 20.sp, fontWeight = FontWeight.Medium) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color(0xFF1E293B),
                navigationIconContentColor = Color(0xFF64748B)
            )
        )

        // Conversation list
        if (conversations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无对话历史",
                    fontSize = 16.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(conversations) { conversation ->
                    ConversationCard(
                        conversation = conversation,
                        onClick = { onConversationClick(conversation) },
                        onDelete = {
                            sessionManager.deleteSession(conversation.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationCard(
    conversation: Conversation,
    onClick: () -> Unit,
    onDelete: () -> Unit = {}
) {
    val timeFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        shadowElevation = 2.dp
    ) {
        Box {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${conversation.messageCount} 条消息",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = conversation.preview,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = timeFormat.format(Date(conversation.timestamp)),
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // 删除按钮
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .offset(x = 4.dp, y = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "删除",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}



private fun shareImage(context: android.content.Context, file: File) {
    try {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(android.content.Intent.createChooser(intent, "分享图片"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
private fun ImageViewDialog(
    imageFile: File,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
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
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
