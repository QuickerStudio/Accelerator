package com.english.accelerator.ui.vocabulary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.english.accelerator.data.sampleWords
import com.english.accelerator.ui.components.VocabularyTopBar
import com.english.accelerator.ui.vocabulary.components.WordCardStack

@Composable
fun VocabularyScreen() {
    var currentIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部栏
        VocabularyTopBar(
            onMenuClick = {
                // TODO: 打开侧边栏
            },
            onConversationClick = {
                // TODO: 进入对话模式
            },
            onBookmarkClick = {
                // TODO: 打开收藏本
            }
        )

        // 卡片区域
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 48.dp),
            contentAlignment = Alignment.Center
        ) {
            WordCardStack(
                words = sampleWords,
                currentIndex = currentIndex,
                onSwipeLeft = {
                    // 标记为"未记住"
                    if (currentIndex < sampleWords.size - 1) {
                        currentIndex++
                    }
                },
                onSwipeRight = {
                    // 标记为"已记住"
                    if (currentIndex < sampleWords.size - 1) {
                        currentIndex++
                    }
                }
            )
        }
    }
}
