package com.english.accelerator.ui.speaking

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.english.accelerator.ui.components.VocabularyTopBar

@Composable
fun SpeakingScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部栏
        VocabularyTopBar(
            onMenuClick = {
                // TODO: 打开侧边栏
            },
            onConversationClick = {
                // TODO: 对话模式
            },
            onBookmarkClick = {
                // TODO: 收藏本
            }
        )

        // 内容区域
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("对话练习")
        }
    }
}
