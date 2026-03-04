package com.english.accelerator.ui.speaking.nodes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.ai.history.HistoryManager
import com.english.accelerator.ai.session.Session
import com.english.accelerator.ai.session.SessionManager
import com.english.accelerator.ui.speaking.Conversation

/**
 * 历史记录节点
 */
class History(
    private val onBackClick: () -> Unit,
    private val onConversationClick: (Conversation) -> Unit
) {
    val id = "history"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Render() {
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
                    Text("暂无对话历史", fontSize = 16.sp, color = Color(0xFF94A3B8))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(conversations) { conversation ->
                        ConvCard(conversation, onConversationClick)
                    }
                }
            }
        }
    }

    @Composable
    private fun ConvCard(
        conversation: Conversation,
        onClick: (Conversation) -> Unit
    ) {
        val sessionManager = remember { SessionManager.getInstance() }
        val historyManager = remember { HistoryManager.getInstance() }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(conversation) }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = conversation.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${conversation.messageCount} 条消息",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Text(
                        text = conversation.preview,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        maxLines = 2
                    )

                    Text(
                        text = formatTimestamp(conversation.timestamp),
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                IconButton(
                    onClick = {
                        sessionManager.deleteSession(conversation.id)
                        historyManager.deleteHistory(conversation.id)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "删除对话",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60_000 -> "刚刚"
            diff < 3600_000 -> "${diff / 60_000} 分钟前"
            diff < 86400_000 -> "${diff / 3600_000} 小时前"
            diff < 604800_000 -> "${diff / 86400_000} 天前"
            else -> {
                val date = java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date(timestamp))
                date
            }
        }
    }
}
