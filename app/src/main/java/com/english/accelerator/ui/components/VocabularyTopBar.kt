package com.english.accelerator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VocabularyTopBar(
    onMenuClick: () -> Unit,
    onConversationClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    isConversationMode: Boolean = false,
    isCollectionMode: Boolean = false,
    memorizedCount: Int = 0,
    unmemorizedCount: Int = 0,
    currentWordType: String = "",
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
        // 左侧：菜单按钮和语法信息
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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

            // 已记住/未记住统计
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "已记住：$memorizedCount",
                    fontSize = 12.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "未记住：$unmemorizedCount",
                    fontSize = 12.sp,
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 右侧按钮组（胶囊背景）
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF1F5F9))
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // AI 辅助按钮
            IconButton(
                onClick = onConversationClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (isConversationMode) Color(0xFF8B5CF6) else Color.Transparent
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI 辅助",
                    tint = if (isConversationMode) Color.White else Color(0xFF1E293B)
                )
            }

            // 作文收藏按钮
            IconButton(
                onClick = onBookmarkClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (isCollectionMode) Color(0xFF2563EB) else Color.Transparent
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = "作文收藏",
                    tint = if (isCollectionMode) Color.White else Color(0xFF1E293B)
                )
            }
        }
    }
}
