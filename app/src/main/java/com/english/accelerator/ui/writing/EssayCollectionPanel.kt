package com.english.accelerator.ui.writing

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.data.Essay
import com.english.accelerator.data.EssayCollectionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EssayCollectionPanel(
    onEssayClick: (Essay) -> Unit,
    refreshTrigger: Int = 0,  // 添加刷新触发器
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val essays = remember { mutableStateListOf<Essay>() }

    // 监听刷新触发器
    LaunchedEffect(refreshTrigger) {
        essays.clear()
        essays.addAll(EssayCollectionManager.getCollectedEssays())
    }

    Column(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        // 标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "作文收藏",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Divider(color = Color(0xFFE2E8F0))

        Spacer(modifier = Modifier.height(16.dp))

        // 作文标签列表
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (essays.isEmpty()) {
                Text(
                    text = "还没有收藏的作文",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            } else {
                essays.forEach { essay ->
                    EssayTag(
                        essay = essay,
                        onClick = { onEssayClick(essay) },
                        onDelete = {
                            EssayCollectionManager.removeEssay(essay)
                            essays.remove(essay)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EssayTag(
    essay: Essay,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    // 长按3秒后删除
                    coroutineScope.launch {
                        delay(3000)
                        onDelete()
                    }
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFC)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 作文标题
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = essay.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,  // 改为粗体
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 评分
                    Text(
                        text = "评分 ${essay.grammarScore}",
                        fontSize = 11.sp,
                        color = Color(0xFF8B5CF6)
                    )
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    // 字数
                    Text(
                        text = "${essay.wordCount} 词",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
