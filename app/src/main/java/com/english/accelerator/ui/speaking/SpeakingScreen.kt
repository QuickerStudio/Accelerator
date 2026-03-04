package com.english.accelerator.ui.speaking

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.english.accelerator.ai.agent.AgentService
import com.english.accelerator.ai.agent.Prompts
import com.english.accelerator.ai.history.HistoryManager
import com.english.accelerator.ai.session.Session
import com.english.accelerator.ai.session.SessionManager
import com.english.accelerator.ui.components.CustomToast
import com.english.accelerator.ui.components.ScreenshotNotification
import com.english.accelerator.ui.sidebar.Sidebar
import com.english.accelerator.ui.speaking.nodes.*
import com.english.accelerator.utils.AppLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

/**
 * UI Message - 用于界面显示的消息模型
 */
data class Message(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val inferenceStats: InferenceStats? = null
)

data class InferenceStats(
    val startTime: Long,
    val endTime: Long,
    val tokensGenerated: Int,
    val memoryUsedMB: Long
) {
    val durationSeconds: Float get() = (endTime - startTime) / 1000f
    val tokensPerSecond: Float get() = if (durationSeconds > 0) tokensGenerated / durationSeconds else 0f
}

/**
 * Conversation - 用于历史记录显示
 */
data class Conversation(
    val id: String,
    val title: String,
    val timestamp: Long,
    val messageCount: Int,
    val preview: String
)

/**
 * SpeakingScreen - 节点管理器
 *
 * 职责：
 * - 管理所有 UI 节点的注册和生命周期
 * - 提供对外接口
 * - 协调节点之间的通信
 * - 管理状态和数据流
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingScreen(onNavigateToSettings: () -> Unit = {}) {
    val context = LocalContext.current
    val vm = remember { SpeakingVM(context) }

    val messages by vm.messages.collectAsState()
    val currentSession by vm.currentSession.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showSidebar by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }

    var screenshotFile by remember { mutableStateOf<File?>(null) }
    var showImageViewer by remember { mutableStateOf(false) }

    // 节点管理器
    val nodeManager = remember { NodeManager() }

    // 注册节点
    DisposableEffect(Unit) {
        nodeManager.onAttach()
        onDispose { nodeManager.onDetach() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = { Text("对话", fontSize = 20.sp, fontWeight = FontWeight.Medium) },
                        navigationIcon = {
                            IconButton(onClick = { showSidebar = true }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { vm.createSession() }) {
                                Icon(Icons.Default.Add, contentDescription = "新建", tint = Color(0xFF64748B))
                            }
                            IconButton(onClick = { showHistory = true }) {
                                Icon(Icons.Default.History, contentDescription = "历史")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White,
                            titleContentColor = Color(0xFF1E293B),
                            navigationIconContentColor = Color(0xFF64748B),
                            actionIconContentColor = Color(0xFF64748B)
                        )
                    )

                    val threadTitle = currentSession?.title ?: ""
                    val showTitle = threadTitle.isNotEmpty() && threadTitle != "对话"
                    AnimatedVisibility(
                        visible = showTitle,
                        enter = slideInVertically { -it } + fadeIn(tween(300)),
                        exit = slideOutVertically { -it } + fadeOut(tween(300))
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = threadTitle.take(10),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF8B5CF6),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Column {
                    InputBox(
                        text = inputText,
                        onTextChange = { inputText = it },
                        onSend = {
                            if (inputText.isNotBlank()) {
                                val msg = inputText
                                inputText = ""
                                vm.send(msg)
                            }
                        },
                        onScreenshotCaptured = { file ->
                            screenshotFile = file
                        }
                    ).Render()

                    Spacer(modifier = Modifier.height(45.dp))
                    NavBar(onNavigateToSettings).Render()
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                ChatWindow(
                    messages = messages
                ).Render()
            }
        }

        // 截图通知
        if (screenshotFile != null) {
            ScreenshotNotification(
                imageFile = screenshotFile!!,
                onDismiss = { screenshotFile = null },
                onOpenImage = {
                    showImageViewer = true
                },
                context = context
            )
        }

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

        if (showHistory) {
            History(
                onBackClick = { showHistory = false },
                onConversationClick = { conversation ->
                    vm.switchToSession(conversation.id)
                    showHistory = false
                }
            ).Render()
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

@Composable
private fun ImageViewDialog(
    imageFile: File,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E293B)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val bitmap = remember(imageFile) {
                    try {
                        BitmapFactory.decodeFile(imageFile.absolutePath)
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = imageFile.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

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

/**
 * 节点管理器 - 管理所有节点的生命周期
 */
class NodeManager {
    private val nodes = mutableMapOf<String, Any>()

    fun register(id: String, node: Any) {
        nodes[id] = node
    }

    fun unregister(id: String) {
        nodes.remove(id)
    }

    fun getNode(id: String): Any? = nodes[id]

    fun onAttach() {
        AppLogger.info("NodeManager", "All nodes attached")
    }

    fun onDetach() {
        nodes.clear()
        AppLogger.info("NodeManager", "All nodes detached")
    }
}

/**
 * ViewModel - 管理状态和业务逻辑
 */
class SpeakingVM(private val context: Context) : ViewModel() {
    private val TAG = "SpeakingVM"
    private val agentService = AgentService(context)
    private val sessionManager = SessionManager.getInstance()
    private val historyManager = HistoryManager.getInstance()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _currentSession = MutableStateFlow<Session?>(null)
    val currentSession: StateFlow<Session?> = _currentSession.asStateFlow()

    private var inferenceJob: Job? = null

    init {
        initSession()
    }

    private fun initSession() {
        viewModelScope.launch {
            try {
                val current = sessionManager.currentSession.value
                if (current != null && current.type == Session.Type.CONVERSATION) {
                    _currentSession.value = current
                    loadHistory(current.id)
                } else {
                    createSession()
                }
            } catch (e: Exception) {
                AppLogger.error(TAG, "Init failed", e)
                createSession()
            }
        }
    }

    fun createSession() {
        viewModelScope.launch {
            try {
                val session = sessionManager.createSession("对话", Session.Type.CONVERSATION)
                _currentSession.value = session
                _messages.value = emptyList()
            } catch (e: Exception) {
                AppLogger.error(TAG, "Create session failed", e)
            }
        }
    }

    fun switchToSession(sessionId: String) {
        viewModelScope.launch {
            try {
                inferenceJob?.cancel()
                val session = sessionManager.getSession(sessionId)
                if (session != null) {
                    _currentSession.value = session
                    loadHistory(sessionId)
                }
            } catch (e: Exception) {
                AppLogger.error(TAG, "Switch session failed", e)
            }
        }
    }

    private fun loadHistory(sessionId: String) {
        viewModelScope.launch {
            try {
                val history = historyManager.getHistory(sessionId)
                _messages.value = history?.messages?.map {
                    Message(
                        id = UUID.randomUUID().toString(),
                        content = it.content,
                        isFromUser = it.role == "user",
                        timestamp = it.timestamp
                    )
                } ?: emptyList()
            } catch (e: Exception) {
                AppLogger.error(TAG, "Load history failed", e)
            }
        }
    }

    fun send(input: String) {
        if (input.isBlank()) return
        val sessionId = _currentSession.value?.id ?: return
        inferenceJob?.cancel()

        viewModelScope.launch {
            try {
                val userMsg = Message(
                    id = UUID.randomUUID().toString(),
                    content = input,
                    isFromUser = true
                )
                _messages.value = _messages.value + userMsg

                val startTime = System.currentTimeMillis()
                val runtime = Runtime.getRuntime()
                val memBefore = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

                val streamingId = UUID.randomUUID().toString()
                val streamingMsg = Message(id = streamingId, content = "", isFromUser = false)
                _messages.value = _messages.value + streamingMsg

                var lastUpdate = 0L
                val interval = 50L

                inferenceJob = launch {
                    val result = agentService.generateResponse(
                        sessionId = sessionId,
                        userInput = input,
                        systemPrompt = Prompts.SPEAKING_PARTNER
                    ) { partial, done ->
                        val now = System.currentTimeMillis()
                        if (partial.isNotEmpty() && (done || now - lastUpdate >= interval)) {
                            lastUpdate = now
                            _messages.value = _messages.value.map {
                                if (it.id == streamingId) it.copy(content = partial) else it
                            }
                        }
                    }

                    val endTime = System.currentTimeMillis()
                    val memAfter = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

                    result.fold(
                        onSuccess = { response ->
                            val stats = InferenceStats(
                                startTime = startTime,
                                endTime = endTime,
                                tokensGenerated = response.length / 4,
                                memoryUsedMB = memAfter - memBefore
                            )
                            _messages.value = _messages.value.map {
                                if (it.id == streamingId) it.copy(content = response, inferenceStats = stats) else it
                            }
                        },
                        onFailure = { error ->
                            _messages.value = _messages.value.map {
                                if (it.id == streamingId) it.copy(content = "错误：${error.message}") else it
                            }
                        }
                    )
                }

                inferenceJob?.join()
            } catch (e: Exception) {
                AppLogger.error(TAG, "Send failed", e)
            } finally {
                inferenceJob = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        inferenceJob?.cancel()
    }
}
