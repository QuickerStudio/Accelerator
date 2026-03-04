package com.english.accelerator.ui.writing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.data.EssayCollectionManager
import com.english.accelerator.ui.components.VocabularyTopBar
import com.english.accelerator.ui.sidebar.Sidebar
import kotlinx.coroutines.launch

// AI 评论数据类
data class AiComment(
    val lineNumber: Int,
    val comment: String,
    val type: String // "grammar", "style", "positive"
)

@Composable
fun WritingScreen(
    onNavigateToSettings: () -> Unit = {},
    onKeyboardVisibilityChanged: (Boolean) -> Unit = {}
) {
    var showSidebar by remember { mutableStateOf(false) }
    var showEssayCollection by remember { mutableStateOf(false) }

    // 编辑器状态
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    // 编辑器状态持久化（侧边栏）
    var isEditorMode by remember { mutableStateOf(false) }
    var editingNoteId by remember { mutableStateOf<Int?>(null) }
    var editorTitle by remember { mutableStateOf("") }
    var editorContent by remember { mutableStateOf("") }

    // AI 辅助状态
    var showAiPanel by remember { mutableStateOf(false) }
    var aiComments by remember { mutableStateOf<List<AiComment>>(emptyList()) }

    val scope = rememberCoroutineScope()

    // 语法评分和词语类型
    var grammarScore by remember { mutableStateOf(0) }
    var currentWordType by remember { mutableStateOf("") }

    // 刷新触发器
    var refreshTrigger by remember { mutableStateOf(0) }

    // 键盘控制
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isKeyboardVisible by remember { mutableStateOf(false) }

    // 统计信息
    val wordCount = content.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
    val charCount = content.length

    // 禁用系统键盘并通知导航栏状态
    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible) {
            keyboardController?.hide()
        }
        onKeyboardVisibilityChanged(isKeyboardVisible)
    }

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
                    // Toggle AI 面板，关闭作文收藏
                    showAiPanel = !showAiPanel
                    if (showAiPanel) {
                        showEssayCollection = false
                    }
                },
                onBookmarkClick = {
                    // Toggle 作文收藏，关闭 AI 面板
                    showEssayCollection = !showEssayCollection
                    if (showEssayCollection) {
                        showAiPanel = false
                    }
                },
                isConversationMode = showAiPanel,
                isCollectionMode = showEssayCollection,
                statusMessage = ""
            )

            // 编辑器区域
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFAFAFA))
            ) {
                // 主编辑区
                Column(
                    modifier = Modifier
                        .weight(if (showAiPanel || showEssayCollection) 0.6f else 1f)
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    // 标题输入框
                    TitleTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "标题",
                        onClearAll = {
                            // 清空标题和内容
                            title = ""
                            content = ""
                        },
                        onSaveToCollection = {
                            // 保存到作文收藏
                            EssayCollectionManager.addEssay(
                                title = title,
                                content = content,
                                grammarScore = grammarScore
                            )
                            // 保存后清空标题和内容
                            title = ""
                            content = ""
                            grammarScore = 0
                            currentWordType = ""
                            // 触发刷新
                            refreshTrigger++
                        },
                        onFocusChanged = { hasFocus ->
                            isKeyboardVisible = hasFocus
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 内容编辑器
                    ContentEditor(
                        value = content,
                        onValueChange = { newValue ->
                            // 只允许英文字母、数字、空格和英文标点符号
                            val filtered = newValue.filter { char ->
                                char.isLetterOrDigit() ||
                                char.isWhitespace() ||
                                char in ".,!?;:'\"-()[]{}/@#$%&*+=<>~`|\\/"
                            }
                            content = filtered
                        },
                        placeholder = "Every word you write is a step forward. Start your English journey here!",
                        modifier = Modifier.weight(1f),
                        onFocusChanged = { hasFocus ->
                            isKeyboardVisible = hasFocus
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 底部工具栏
                    EditorToolbar(
                        wordCount = wordCount,
                        charCount = charCount,
                        onSave = {
                            // TODO: 保存草稿
                        },
                        onClear = {
                            title = ""
                            content = ""
                        },
                        onAiAssist = {
                            // TODO: 通过 AgentService 进行语法检查
                        }
                    )
                }

                // AI 辅助面板
                if (showAiPanel) {
                    AiAssistPanel(
                        onCheckGrammar = {
                            // TODO: 通过 AgentService 进行语法检查
                        },
                        onGetSuggestions = {
                            // TODO: 通过 AgentService 获取写作建议
                        },
                        onDownloadModel = {
                            // 下载功能已移除，跳转到设置页面
                            onNavigateToSettings()
                        },
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
                    )
                }

                // 作文收藏面板
                if (showEssayCollection) {
                    EssayCollectionPanel(
                        onEssayClick = { essay ->
                            title = essay.title
                            content = essay.content
                            grammarScore = essay.grammarScore
                        },
                        refreshTrigger = refreshTrigger,
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
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

        // 简易英文键盘
        if (isKeyboardVisible) {
            SimpleEnglishKeyboard(
                onKeyPress = { key ->
                    content += key
                },
                onBackspace = {
                    if (content.isNotEmpty()) {
                        content = content.dropLast(1)
                    }
                },
                onSpace = {
                    content += " "
                },
                onClose = {
                    focusManager.clearFocus()
                    isKeyboardVisible = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun TitleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onClearAll: () -> Unit,
    onSaveToCollection: () -> Unit,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 标题输入框
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            ),
            cursorBrush = SolidColor(Color(0xFF2563EB)),
            singleLine = true,  // 单行，不换行
            modifier = Modifier
                .weight(1f)
                .height(56.dp)  // 固定高度，与按钮一致
                .onFocusChanged { focusState ->
                    onFocusChanged(focusState.isFocused)
                },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                    innerTextField()
                }
            }
        )

        // 清空按钮
        IconButton(
            onClick = onClearAll,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = Color(0xFFFEE2E2),  // 浅红色
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "清空全部",
                tint = Color(0xFFDC2626),  // 深红色图标
                modifier = Modifier.size(24.dp)
            )
        }

        // 保存按钮
        IconButton(
            onClick = onSaveToCollection,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = Color(0xFFD1FAE5),  // 浅绿色
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = "保存到收藏库",
                tint = Color(0xFF059669),  // 深绿色图标
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ContentEditor(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = 18.sp,
            lineHeight = 28.sp,
            color = Color(0xFF1E293B)
        ),
        cursorBrush = SolidColor(Color(0xFF2563EB)),
        readOnly = true,  // 只读，使用自定义键盘输入
        modifier = modifier.onFocusChanged { focusState ->
            onFocusChanged(focusState.isFocused)
        },
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
                innerTextField()
            }
        }
    )
}


@Composable
private fun EditorToolbar(
    wordCount: Int,
    charCount: Int,
    onSave: () -> Unit,
    onClear: () -> Unit,
    onAiAssist: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 统计信息
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$wordCount 词",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = "$charCount 字符",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
        }

        // 操作按钮
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // AI 辅助按钮
            IconButton(
                onClick = onAiAssist,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI 辅助",
                    tint = Color(0xFF8B5CF6)
                )
            }

            // 清空按钮
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "清空",
                    tint = Color(0xFF64748B)
                )
            }

            // 保存按钮
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "保存",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "保存",
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun AiAssistPanel(
    onCheckGrammar: () -> Unit,
    onGetSuggestions: () -> Unit,
    onDownloadModel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

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
                text = "AI 辅助",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFFE2E8F0))
        Spacer(modifier = Modifier.height(16.dp))

        // 控制按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCheckGrammar,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6)
                )
            ) {
                Text("语法检查", fontSize = 12.sp)
            }
            Button(
                onClick = onGetSuggestions,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                )
            ) {
                Text("写作建议", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: 通过 AgentService 显示推理结果
        Text(
            text = "AI 功能将通过 AgentService 实现",
            fontSize = 14.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(vertical = 32.dp)
        )
    }
}

@Composable
private fun ModelDownloadPrompt(onDownload: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CloudDownload,
            contentDescription = null,
            tint = Color(0xFF8B5CF6),
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "需要下载 AI 模型",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Text(
            text = "模型大小约 1-2GB\n首次使用需要下载",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Button(
            onClick = onDownload,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B5CF6)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("下载模型")
        }
    }
}

@Composable
private fun DownloadProgress(progress: Float) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "正在下载模型...",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = Color(0xFF8B5CF6),
            trackColor = Color(0xFFE2E8F0)
        )
        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )
    }
}
