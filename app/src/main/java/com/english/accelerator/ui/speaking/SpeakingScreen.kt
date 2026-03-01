package com.english.accelerator.ui.speaking

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.english.accelerator.ui.components.VocabularyTopBar
import com.english.accelerator.ui.sidebar.Sidebar

@Composable
fun SpeakingScreen(
    onNavigateToSettings: () -> Unit = {}
) {
    var showSidebar by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部栏
            VocabularyTopBar(
                onMenuClick = {
                    showSidebar = true
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

        // 侧边栏
        if (showSidebar) {
            Sidebar(
                isOpen = showSidebar,
                onClose = { showSidebar = false },
                onNavigateToSettings = onNavigateToSettings
            )
        }
    }
}
