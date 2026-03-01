package com.english.accelerator.ui.vocabulary

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.english.accelerator.data.BookmarkManager
import com.english.accelerator.data.sampleWords
import com.english.accelerator.ui.components.CustomToast
import com.english.accelerator.ui.components.VocabularyTopBar
import com.english.accelerator.ui.vocabulary.components.WordCardStack

@Composable
fun VocabularyScreen(
    showInputArea: Boolean = false,
    onToggleInputArea: () -> Unit = {}
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var showBookmarkScreen by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastBackgroundColor by remember { mutableStateOf(Color.White) }
    var showToast by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // 动画：卡片底部内边距
    val cardBottomPadding by animateDpAsState(
        targetValue = if (showInputArea) 180.dp else 100.dp,
        label = "cardBottomPadding"
    )

    if (showBookmarkScreen) {
        BookmarkScreen(
            onBackClick = { showBookmarkScreen = false }
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部栏
            VocabularyTopBar(
                onMenuClick = {
                    // TODO: 打开侧边栏
                },
                onConversationClick = onToggleInputArea,
                onBookmarkClick = {
                    showBookmarkScreen = true
                },
                isConversationMode = showInputArea
            )

            // 卡片区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(bottom = cardBottomPadding)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                    },
                contentAlignment = Alignment.Center
            ) {
                WordCardStack(
                    words = sampleWords,
                    currentIndex = currentIndex,
                    onSwipeLeft = {
                        // 标记为"未记住"
                        if (currentIndex < sampleWords.size - 1) {
                            currentIndex++
                            toastMessage = "未记住"
                            toastBackgroundColor = Color(0xFFFEE2E2) // 浅红色
                            showToast = true
                        }
                    },
                    onSwipeRight = {
                        // 标记为"已记住"
                        if (currentIndex < sampleWords.size - 1) {
                            currentIndex++
                            toastMessage = "已记住"
                            toastBackgroundColor = Color(0xFFDCFCE7) // 浅绿色
                            showToast = true
                        }
                    },
                    onLongPress = { word ->
                        BookmarkManager.addBookmark(word)
                        toastMessage = "已收藏"
                        toastBackgroundColor = Color(0xFFDEEDFF) // 浅蓝色
                        showToast = true
                    }
                )

                // Toast 提示（位于卡片上方）
                CustomToast(
                    message = toastMessage,
                    visible = showToast,
                    onDismiss = { showToast = false },
                    backgroundColor = toastBackgroundColor,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp)
                )
            }
        }
    }
}
