package com.english.accelerator.ui.vocabulary

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.english.accelerator.data.BookmarkManager
import com.english.accelerator.data.Word
import com.english.accelerator.ui.components.CustomToast
import com.english.accelerator.ui.components.VocabularyTopBar
import com.english.accelerator.ui.sidebar.Sidebar
import com.english.accelerator.ui.vocabulary.nodes.CardStack
import com.english.accelerator.ui.vocabulary.nodes.BookmarkNode
import com.english.accelerator.utils.AppLogger
import com.english.accelerator.utils.WordLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * VocabularyScreen - 节点管理器
 */
@Composable
fun VocabularyScreen(
    showInputArea: Boolean = false,
    onToggleInputArea: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val vm = remember { VocabularyVM(context) }

    val words by vm.words.collectAsState()
    val currentIndex by vm.currentIndex.collectAsState()

    var showBookmarkScreen by remember { mutableStateOf(false) }
    var showSidebar by remember { mutableStateOf(false) }
    var isEditorMode by remember { mutableStateOf(false) }
    var editingNoteId by remember { mutableStateOf<Int?>(null) }
    var editorTitle by remember { mutableStateOf("") }
    var editorContent by remember { mutableStateOf("") }

    var toastMessage by remember { mutableStateOf("") }
    var toastBackgroundColor by remember { mutableStateOf(Color.White) }
    var showToast by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val cardBottomPadding by animateDpAsState(
        targetValue = if (showInputArea) 180.dp else 100.dp,
        label = "cardBottomPadding"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (showBookmarkScreen) {
            BookmarkNode(onBackClick = { showBookmarkScreen = false }).Render()
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                VocabularyTopBar(
                    onMenuClick = { showSidebar = true },
                    onConversationClick = onToggleInputArea,
                    onBookmarkClick = { showBookmarkScreen = true },
                    isConversationMode = showInputArea
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(bottom = cardBottomPadding)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { focusManager.clearFocus() },
                    contentAlignment = Alignment.Center
                ) {
                    CardStack(
                        words = words,
                        currentIndex = currentIndex,
                        onSwipeLeft = {
                            vm.markUnmemorized()
                            toastMessage = "未记住"
                            toastBackgroundColor = Color(0xFFFEE2E2)
                            showToast = true
                        },
                        onSwipeRight = {
                            vm.markMemorized()
                            toastMessage = "已记住"
                            toastBackgroundColor = Color(0xFFDCFCE7)
                            showToast = true
                        },
                        onLongPress = { word ->
                            BookmarkManager.addBookmark(word)
                            toastMessage = "已收藏"
                            toastBackgroundColor = Color(0xFFDEEDFF)
                            showToast = true
                        }
                    ).Render()

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

/**
 * ViewModel - 管理状态和业务逻辑
 */
class VocabularyVM(private val context: Context) : ViewModel() {
    private val TAG = "VocabularyVM"

    private val _words = MutableStateFlow<List<Word>>(emptyList())
    val words: StateFlow<List<Word>> = _words.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            try {
                _words.value = WordLoader.getNextBatch(count = 50, includeReview = true)
                _currentIndex.value = 0
            } catch (e: Exception) {
                AppLogger.error(TAG, "Load words failed", e)
            }
        }
    }

    fun markMemorized() {
        val index = _currentIndex.value
        if (index < _words.value.size) {
            val word = _words.value[index]
            WordLoader.markAsMemorized(word.id)
            _currentIndex.value = index + 1

            if (_currentIndex.value >= _words.value.size && WordLoader.hasMoreWords()) {
                loadWords()
            }
        }
    }

    fun markUnmemorized() {
        val index = _currentIndex.value
        if (index < _words.value.size) {
            val word = _words.value[index]
            WordLoader.markAsUnmemorized(word.id)
            _currentIndex.value = index + 1

            if (_currentIndex.value >= _words.value.size && WordLoader.hasMoreWords()) {
                loadWords()
            }
        }
    }
}
