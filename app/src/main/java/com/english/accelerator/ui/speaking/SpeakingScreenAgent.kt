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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.data.ConversationTurn
import com.english.accelerator.ui.sidebar.Sidebar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Speaking Screen with Agent System Integration
 * Uses SpeakingViewModel for state management and AI interactions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingScreenWithAgent(
    onNavigateToSettings: () -> Unit = {},
    viewModel: SpeakingViewModel = SpeakingViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var showSidebar by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // Generate welcome message on first load
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            viewModel.generateWelcomeMessage()
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
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
                        IconButton(onClick = { /* Phone mode */ }) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "语音通话",
                                tint = Color(0xFF64748B)
                            )
                        }
                        IconButton(onClick = { viewModel.clearConversation() }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "更多选项")
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
                    // Bottom input area
                    BottomInputAreaAgent(
                        inputText = inputText,
                        onInputChange = { inputText = it },
                        onSend = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                                focusManager.clearFocus()
                            }
                        },
                        onCamera = { /* TODO: Open camera */ },
                        onAttach = { /* TODO: Attach file */ },
                        isLoading = isLoading
                    )

                    Spacer(modifier = Modifier.height(45.dp))

                    // Bottom navigation bar
                    BottomNavigationBarAgent(onNavigateToSettings = onNavigateToSettings)
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
                items(messages) { turn ->
                    MessageBubbleAgent(turn = turn)
                }

                // Loading indicator
                if (isLoading) {
                    item {
                        LoadingBubbleAgent()
                    }
                }

                // Error message
                error?.let { errorMsg ->
                    item {
                        ErrorBubble(message = errorMsg)
                    }
                }
            }
        }

        // Sidebar
        if (showSidebar) {
            Sidebar(
                isOpen = showSidebar,
                onClose = { showSidebar = false },
                onNavigateToSettings = onNavigateToSettings
            )
        }
    }
}

@Composable
fun MessageBubbleAgent(turn: ConversationTurn) {
    val isFromUser = turn.role == ConversationTurn.Role.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isFromUser) {
            // AI Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8B5CF6)),
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
            horizontalAlignment = if (isFromUser) Alignment.End else Alignment.Start
        ) {
            // Message bubble
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isFromUser) 16.dp else 4.dp,
                    bottomEnd = if (isFromUser) 4.dp else 16.dp
                ),
                color = if (isFromUser) Color.Transparent else Color.White,
                modifier = Modifier.then(
                    if (isFromUser) {
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
                ),
                shadowElevation = if (isFromUser) 0.dp else 2.dp,
                border = if (isFromUser) null else androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFE2E8F0)
                )
            ) {
                Text(
                    text = turn.content,
                    fontSize = 16.sp,
                    color = if (isFromUser) Color.White else Color(0xFF1E293B),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // Timestamp
            Text(
                text = formatTimestamp(turn.timestamp),
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }

        if (isFromUser) {
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun LoadingBubbleAgent() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // AI Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF8B5CF6)),
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
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF94A3B8))
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorBubble(message: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFFEE2E2),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFF991B1B)
            )
        }
    }
}

@Composable
fun BottomInputAreaAgent(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onCamera: () -> Unit,
    onAttach: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Camera button
            IconButton(
                onClick = onCamera,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "相机",
                    tint = Color(0xFF64748B)
                )
            }

            // Text input
            TextField(
                value = inputText,
                onValueChange = onInputChange,
                placeholder = { Text("发消息或按住说话...") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F5F9),
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    disabledContainerColor = Color(0xFFF1F5F9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp),
                maxLines = 4
            )

            // Upload button
            IconButton(
                onClick = onAttach,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "上传",
                    tint = Color(0xFF64748B)
                )
            }

            // Send button
            IconButton(
                onClick = onSend,
                modifier = Modifier.size(40.dp),
                enabled = inputText.isNotBlank() && !isLoading
            ) {
                Icon(
                    imageVector = if (isLoading) Icons.Default.Stop else Icons.Default.Send,
                    contentDescription = if (isLoading) "停止" else "发送",
                    tint = when {
                        isLoading -> Color(0xFFEF4444)
                        inputText.isNotBlank() -> Color(0xFF8B5CF6)
                        else -> Color(0xFF94A3B8)
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBarAgent(onNavigateToSettings: () -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(72.dp)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Book, contentDescription = "单词") },
            label = { Text("单词") },
            selected = false,
            onClick = { /* TODO */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Edit, contentDescription = "写作") },
            label = { Text("写作") },
            selected = false,
            onClick = { /* TODO */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, contentDescription = "对话") },
            label = { Text("对话") },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "设置") },
            label = { Text("设置") },
            selected = false,
            onClick = onNavigateToSettings
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
