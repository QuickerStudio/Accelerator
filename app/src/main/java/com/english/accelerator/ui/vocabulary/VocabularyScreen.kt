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
import com.english.accelerator.utils.WordLoader
import com.english.accelerator.data.BookmarkManager
import com.english.accelerator.ui.components.CustomToast
import com.english.accelerator.ui.components.VocabularyTopBar
import com.english.accelerator.ui.sidebar.Sidebar
import com.english.accelerator.ui.vocabulary.components.WordCardStack

@Composable
fun VocabularyScreen(
    showInputArea: Boolean = false,
    onToggleInputArea: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current

    // 使用 WordLoader 获取单词
    var currentBatchWords by remember {
        mutableStateOf(WordLoader.getNextBatch(count = 50, includeReview = true))
    }
    var currentIndexInBatch by remember { mutableIntStateOf(0) }

    var showBookmarkScreen by remember { mutableStateOf(false) }
    var showSidebar by remember { mutableStateOf(false) }

    // 编辑器状态持久化
    var isEditorMode by remember { mutableStateOf(false) }
    var editingNoteId by remember { mutableStateOf<Int?>(null) }
    var editorTitle by remember { mutableStateOf("") }
    var editorContent by remember { mutableStateOf("") }

    var toastMessage by remember { mutableStateOf("") }
    var toastBackgroundColor by remember { mutableStateOf(Color.White) }
    var showToast by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // 动画：卡片底部内边距
    val cardBottomPadding by animateDpAsState(
        targetValue = if (showInputArea) 180.dp else 100.dp,
        label = "cardBottomPadding"
    )

    Box(modifier = Modifier.fillMaxSize()) {
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
                        showSidebar = true
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
                    words = currentBatchWords,
                    currentIndex = currentIndexInBatch,
                    onSwipeLeft = {
                        // 标记为"未记住"
                        if (currentIndexInBatch < currentBatchWords.size) {
                            val currentWord = currentBatchWords[currentIndexInBatch]
                            WordLoader.markAsUnmemorized(currentWord.id)
                            currentIndexInBatch++

                            toastMessage = "未记住"
                            toastBackgroundColor = Color(0xFFFEE2E2) // 浅红色
                            showToast = true

                            // 换批检查：当前批学完了，加载下一批
                            if (currentIndexInBatch >= currentBatchWords.size) {
                                if (WordLoader.hasMoreWords()) {
                                    currentBatchWords = WordLoader.getNextBatch(count = 50, includeReview = true)
                                    currentIndexInBatch = 0
                                }
                            }
                        }
                    },
                    onSwipeRight = {
                        // 标记为"已记住"
                        if (currentIndexInBatch < currentBatchWords.size) {
                            val currentWord = currentBatchWords[currentIndexInBatch]
                            WordLoader.markAsMemorized(currentWord.id)
                            currentIndexInBatch++

                            toastMessage = "已记住"
                            toastBackgroundColor = Color(0xFFDCFCE7) // 浅绿色
                            showToast = true

                            // 换批检查：当前批学完了，加载下一批
                            if (currentIndexInBatch >= currentBatchWords.size) {
                                if (WordLoader.hasMoreWords()) {
                                    currentBatchWords = WordLoader.getNextBatch(count = 50, includeReview = true)
                                    currentIndexInBatch = 0
                                }
                            }
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

        // 侧边栏
        if (showSidebar) {
            Sidebar(
                isOpen = showSidebar,
                onClose = { showSidebar = false },
                onNavigateToSettings = onNavigateToSettings,
                isEditorMode = isEditorMode,
                onEditorModeChange = { isEditorMode = it },
                editingNoteId = editingNoteId,
                onEditingNoteIdChange = { editingNoteId = it },
                editorTitle = editorTitle,
                onEditorTitleChange = { editorTitle = it },
                editorContent = editorContent,
                onEditorContentChange = { editorContent = it }
            )
        }
    }
}
