package com.english.accelerator.ui.speaking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.ui.sidebar.Sidebar
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
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingScreen(
    onNavigateToSettings: () -> Unit = {}
) {
    val messages = remember { mutableStateListOf<Message>() }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    var showSidebar by remember { mutableStateOf(false) }
    var isContinuousMode by remember { mutableStateOf(false) }
    var showHistoryScreen by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Initialize with welcome message
    LaunchedEffect(Unit) {
        messages.add(
            Message(
                content = "Hello! Let's practice English conversation. What would you like to talk about today?",
                isFromUser = false
            )
        )
    }

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
                                // Add user message
                                messages.add(Message(content = inputText, isFromUser = true))
                                inputText = ""

                                // TODO: Send to AI and get response
                                isLoading = true
                            }
                        },
                        onCamera = { /* TODO: Open camera */ },
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
                items(messages) { message ->
                    MessageBubble(message = message)
                }

                // Loading indicator
                if (isLoading) {
                    item {
                        LoadingBubble()
                    }
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
    }
}

@Composable
fun MessageBubble(message: Message) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser) {
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
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
        ) {
            // Message bubble
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isFromUser) 4.dp else 16.dp
                ),
                color = if (message.isFromUser) Color.Transparent else Color.White,
                border = if (message.isFromUser) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = if (message.isFromUser) 0.dp else 2.dp,
                modifier = Modifier.then(
                    if (message.isFromUser) {
                        Modifier.background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                            ),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 4.dp
                            )
                        )
                    } else Modifier
                )
            ) {
                Text(
                    text = message.content,
                    fontSize = 16.sp,
                    color = if (message.isFromUser) Color.White else Color(0xFF1E293B),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // Timestamp
            Text(
                text = timeFormat.format(Date(message.timestamp)),
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }

        if (message.isFromUser) {
            Spacer(modifier = Modifier.width(48.dp))
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // 背景容器和输入框
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFFE2E8F0))
                .padding(
                    start = 52.dp,
                    end = 100.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
        ) {
            TextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                placeholder = {
                    Text(
                        text = "发消息或按住说话...",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                maxLines = 2
            )
        }

        // 悬浮按钮层
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            // 相机按钮（左侧悬浮）
            IconButton(
                onClick = onCamera,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "相机",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }

            // 占位空间
            Box(modifier = Modifier.weight(1f))

            // 上传按钮（右侧悬浮）
            IconButton(
                onClick = onAttach,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "上传",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }

            // 发送按钮（右侧悬浮）
            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .size(36.dp)
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
    // TODO: Load from database/storage
    val conversations = remember { mutableStateListOf<Conversation>() }

    LaunchedEffect(Unit) {
        // Mock data for now
        conversations.addAll(
            listOf(
                Conversation(
                    title = "English Practice",
                    preview = "Hello! Let's practice English...",
                    messageCount = 15
                ),
                Conversation(
                    title = "Daily Conversation",
                    preview = "How was your day?",
                    messageCount = 8
                )
            )
        )
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
                        onClick = { onConversationClick(conversation) }
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationCard(
    conversation: Conversation,
    onClick: () -> Unit
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
                    color = Color(0xFF1E293B)
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
    }
}

