package com.english.accelerator.ui.speaking.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.ai.history.HistoryManager
import com.english.accelerator.ai.session.Session
import com.english.accelerator.ai.session.SessionManager
import com.english.accelerator.ui.speaking.components.ConvCard
import com.english.accelerator.ui.speaking.models.Conversation

/**
 * 对话历史屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBackClick: () -> Unit,
    onConversationClick: (Conversation) -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance() }
    val historyManager = remember { HistoryManager.getInstance() }
    val sessions by sessionManager.sessionList.collectAsState()

    val conversations = remember(sessions) {
        sessions
            .filter { it.type == Session.Type.CONVERSATION }
            .map { session ->
                val history = historyManager.getHistory(session.id)
                val messageCount = history?.messages?.size ?: 0
                val preview = history?.messages?.lastOrNull()?.content?.take(50) ?: "暂无消息"

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
                    ConvCard(
                        conversation = conversation,
                        onClick = { onConversationClick(conversation) },
                        onDelete = { sessionManager.deleteSession(conversation.id) }
                    )
                }
            }
        }
    }
}
