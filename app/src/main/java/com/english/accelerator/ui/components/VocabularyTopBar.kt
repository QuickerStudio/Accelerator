package com.english.accelerator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VocabularyTopBar(
    onMenuClick: () -> Unit,
    onConversationClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    isConversationMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 侧边栏按钮（圆形背景）
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "菜单",
                tint = Color(0xFF1E293B)
            )
        }

        // 右侧按钮组（胶囊背景）
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF1F5F9))
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 对话按钮
            IconButton(
                onClick = onConversationClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (isConversationMode) Color(0xFF64748B) else Color.Transparent
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "对话模式",
                    tint = if (isConversationMode) Color.White else Color(0xFF1E293B)
                )
            }

            // 收藏本按钮
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = "收藏本",
                    tint = Color(0xFF1E293B)
                )
            }
        }
    }
}
