package com.english.accelerator.ui.speaking

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.ai.history.HistoryManager
import com.english.accelerator.ai.session.Session
import com.english.accelerator.ai.session.SessionManager
import com.english.accelerator.ui.sidebar.Sidebar
import com.english.accelerator.ui.speaking.components.*
import com.english.accelerator.ui.speaking.models.Conversation
import com.english.accelerator.ui.speaking.screens.HistoryScreen
import kotlinx.coroutines.launch

/**
 * 对话主屏幕 - 简化版
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingScreen(onNavigateToSettings: () -> Unit = {}) {
    val context = LocalContext.current
    val vm = remember { VM(context) }
    val messages by vm.messages.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val currentSession by vm.currentSession.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var showSidebar by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

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
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                contentAlignment = Alignment.CenterStart
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
                    InputBar(
                        text = inputText,
                        onTextChange = { inputText = it },
                        onSend = {
                            if (inputText.isNotBlank()) {
                                val msg = inputText
                                inputText = ""
                                focusManager.clearFocus()
                                vm.send(msg)
                                scope.launch { listState.animateScrollToItem(messages.size) }
                            }
                        },
                        onCamera = { }
                    )
                    Spacer(modifier = Modifier.height(45.dp))
                    NavBar(onNavigateToSettings = onNavigateToSettings)
                }
            }
        ) { padding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { focusManager.clearFocus() },
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages.filter { it.content.isNotEmpty() }) { message ->
                    Bubble(message = message)
                }
            }
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
            HistoryScreen(
                onBackClick = { showHistory = false },
                onConversationClick = { showHistory = false }
            )
        }
    }
}
