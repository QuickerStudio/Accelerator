package com.english.accelerator.ui.writing

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
fun WritingScreen(
    onNavigateToSettings: () -> Unit = {}
) {
    var showSidebar by remember { mutableStateOf(false) }

    // 编辑器状态持久化
    var isEditorMode by remember { mutableStateOf(false) }
    var editingNoteId by remember { mutableStateOf<Int?>(null) }
    var editorTitle by remember { mutableStateOf("") }
    var editorContent by remember { mutableStateOf("") }

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
                Text("写作练习")
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
