package com.english.accelerator.ui.sidebar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize

@Composable
fun Sidebar(
    isOpen: Boolean,
    onClose: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    isEditorMode: Boolean = false,
    onEditorModeChange: (Boolean) -> Unit = {},
    editingNoteId: Int? = null,
    onEditingNoteIdChange: (Int?) -> Unit = {},
    editorTitle: String = "",
    onEditorTitleChange: (String) -> Unit = {},
    editorContent: String = "",
    onEditorContentChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 选中的分组 ID（null 表示显示全部笔记）
    var selectedGroupId by remember { mutableStateOf<Int?>(null) }

    // 拖拽状态管理
    val dragDropState = rememberDragDropState()

    // 长按笔记后显示分组选择器（保留作为备用方案）
    var showGroupSelector by remember { mutableStateOf(false) }
    var selectedNoteForMove by remember { mutableStateOf<com.english.accelerator.data.Note?>(null) }

    // 侧边栏偏移动画
    val offsetX by animateDpAsState(
        targetValue = if (isOpen) 0.dp else (-300).dp,
        animationSpec = tween(durationMillis = 300),
        label = "sidebarOffset"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // 遮罩层
        if (isOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(onClick = onClose)
            )
        }

        // 侧边栏内容
        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .width(300.dp)
                .fillMaxHeight()
                .shadow(16.dp)
                .background(Color.White)
                .statusBarsPadding()
        ) {
            if (isEditorMode) {
                // 编辑器视图
                NoteEditor(
                    noteId = editingNoteId,
                    initialTitle = editorTitle,
                    initialContent = editorContent,
                    onSave = { title, content ->
                        if (title.isNotEmpty() || content.isNotEmpty()) {
                            if (editingNoteId != null) {
                                com.english.accelerator.data.NoteManager.updateNote(editingNoteId!!, title, content)
                            } else {
                                com.english.accelerator.data.NoteManager.addNote(title, content)
                            }
                        }
                        // 返回正常视图
                        onEditorModeChange(false)
                        onEditingNoteIdChange(null)
                        onEditorTitleChange("")
                        onEditorContentChange("")
                    },
                    onBack = {
                        // 返回正常视图
                        onEditorModeChange(false)
                    }
                )
            } else {
                // 正常视图
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // 顶部品牌区域
                    SidebarHeader(
                        onSearchClick = { /* TODO: 搜索功能 */ },
                        onSettingsClick = {
                            onNavigateToSettings()
                            onClose()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 全部笔记区域
                    AllNotesSection(
                        selectedGroupId = selectedGroupId,
                        dragDropState = dragDropState,
                        onNewNoteClick = {
                            // 切换到编辑器视图
                            onEditorModeChange(true)
                            onEditingNoteIdChange(null)
                            onEditorTitleChange("")
                            onEditorContentChange("")
                        },
                        onNoteClick = { note ->
                            // 打开笔记编辑器
                            onEditorModeChange(true)
                            onEditingNoteIdChange(note.id)
                            onEditorTitleChange(note.title)
                            onEditorContentChange(note.content)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 笔记分组区域
                    NoteGroupsSection(
                        selectedGroupId = selectedGroupId,
                        dragDropState = dragDropState,
                        onGroupClick = { groupId ->
                            // Toggle 分组筛选
                            selectedGroupId = if (selectedGroupId == groupId) null else groupId
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 单词学习日志
                    LearningLogsSection()
                }
            }
        }

        // 分组选择器弹窗
        if (showGroupSelector && selectedNoteForMove != null) {
            GroupSelectorDialog(
                note = selectedNoteForMove!!,
                onDismiss = { showGroupSelector = false },
                onGroupSelected = { groupId ->
                    com.english.accelerator.data.NoteManager.updateNoteGroup(
                        selectedNoteForMove!!.id,
                        groupId
                    )
                    showGroupSelector = false
                    selectedNoteForMove = null
                }
            )
        }
    }
}

@Composable
private fun SidebarHeader(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var isSearchMode by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val brandText = "Accelerator"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 品牌名称（逐字消失动画）
            Row(modifier = Modifier.weight(1f)) {
                brandText.forEachIndexed { index, char ->
                    val delay = if (isSearchMode) {
                        (brandText.length - 1 - index) * 50
                    } else {
                        index * 50
                    }

                    val alpha by animateFloatAsState(
                        targetValue = if (isSearchMode) 0f else 1f,
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = delay
                        ),
                        label = "charAlpha_$index"
                    )

                    Text(
                        text = char.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B).copy(alpha = alpha)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // 搜索按钮
                IconButton(
                    onClick = {
                        if (searchText.isNotEmpty()) {
                            // 有内容时：执行搜索并清空
                            // TODO: 实现搜索功能
                            searchText = ""
                        } else {
                            // 无内容时：切换搜索模式
                            isSearchMode = !isSearchMode
                        }
                        onSearchClick()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (searchText.isNotEmpty()) Color(0xFFDCFCE7) else Color.Transparent,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color(0xFF64748B)
                    )
                }

                // 设置按钮
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "设置",
                        tint = Color(0xFF64748B)
                    )
                }
            }
        }

        // 搜索框（从右向左滑入）
        val searchBoxOffset by animateDpAsState(
            targetValue = if (isSearchMode) 0.dp else 300.dp,
            animationSpec = tween(durationMillis = 600),
            label = "searchBoxOffset"
        )

        if (isSearchMode || searchBoxOffset < 300.dp) {
            TextField(
                value = searchText,
                onValueChange = { newValue -> searchText = newValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.Center)
                    .offset(x = searchBoxOffset)
                    .padding(end = 96.dp),
                placeholder = {
                    Text(
                        text = "搜索笔记...",
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F5F9),
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B)
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AllNotesSection(
    selectedGroupId: Int? = null,
    dragDropState: DragDropState,
    onNewNoteClick: () -> Unit = {},
    onNoteClick: (com.english.accelerator.data.Note) -> Unit = {}
) {
    var isReversed by remember { mutableStateOf(false) }

    // 根据选中的分组筛选笔记
    val allNotes = if (selectedGroupId != null) {
        com.english.accelerator.data.NoteManager.getNotesByGroup(selectedGroupId)
    } else {
        com.english.accelerator.data.NoteManager.getAllNotes()
    }

    val notes = if (isReversed) allNotes.reversed() else allNotes

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "全部笔记",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                // 排序切换按钮
                IconButton(
                    onClick = { isReversed = !isReversed },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = if (isReversed) "倒序" else "正序",
                        tint = Color(0xFF64748B),
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(if (isReversed) 180f else 0f)
                    )
                }
            }

            // 新建笔记按钮
            Button(
                onClick = onNewNoteClick,
                modifier = Modifier
                    .size(32.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "新建笔记",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 笔记列表（水平滚动）+ 拖拽预览层
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 显示实际笔记
                notes.forEach { note ->
                    NoteCard(
                        note = note,
                        title = note.title.ifEmpty { "无标题" },
                        preview = note.content,
                        dragDropState = dragDropState,
                        onClick = { onNoteClick(note) }
                    )
                }
            }

            // 拖拽预览层（只在笔记列表区域显示）
            if (dragDropState.isDragging && dragDropState.draggingNote != null) {
                DragPreviewLayer(
                    note = dragDropState.draggingNote!!,
                    offset = dragDropState.dragOffset
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteCard(
    note: com.english.accelerator.data.Note,
    title: String,
    preview: String,
    dragDropState: DragDropState,
    onClick: () -> Unit = {}
) {
    var cardOffset by remember { mutableStateOf(Offset.Zero) }

    // 判断当前卡片是否正在被拖拽
    val isDragging = dragDropState.draggingNote?.id == note.id

    Card(
        modifier = Modifier
            .width(100.dp)
            .height(120.dp)
            .onGloballyPositioned { coordinates ->
                cardOffset = coordinates.positionInRoot()
            }
            .pointerInput(note.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        // 开始拖拽，传入卡片中心位置
                        dragDropState.startDragging(note, cardOffset + Offset(50.dp.toPx(), 60.dp.toPx()))
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // 更新拖拽位置
                        dragDropState.updateDragPosition(
                            dragDropState.dragOffset + dragAmount
                        )
                    },
                    onDragEnd = {
                        // 结束拖拽，更新笔记分组
                        val targetGroupId = dragDropState.endDragging()
                        if (targetGroupId != null) {
                            com.english.accelerator.data.NoteManager.updateNoteGroup(
                                note.id,
                                targetGroupId
                            )
                        }
                    },
                    onDragCancel = {
                        // 取消拖拽
                        dragDropState.cancelDragging()
                    }
                )
            }
            .clickable(enabled = !isDragging, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) Color.Transparent else Color(0xFFF1F5F9)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            if (!isDragging) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = preview,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
private fun NoteGroupsSection(
    selectedGroupId: Int? = null,
    dragDropState: DragDropState,
    onGroupClick: (Int) -> Unit = {}
) {
    var isReversed by remember { mutableStateOf(false) }
    val allGroups = com.english.accelerator.data.NoteGroupManager.getAllGroups()
    val groups = if (isReversed) allGroups.reversed() else allGroups

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "笔记分组",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                // 排序切换按钮
                IconButton(
                    onClick = { isReversed = !isReversed },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = if (isReversed) "倒序" else "正序",
                        tint = Color(0xFF64748B),
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(if (isReversed) 180f else 0f)
                    )
                }
            }

            // 添加分组按钮
            IconButton(
                onClick = { /* TODO: 添加分组 */ },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加分组",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 分组网格（水平滚动）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 显示实际分组
            groups.forEach { group ->
                NoteGroupCard(
                    groupId = group.id,
                    name = group.name,
                    isSelected = selectedGroupId == group.id,
                    isHovering = dragDropState.hoveringGroupId == group.id,
                    dragDropState = dragDropState,
                    onClick = { onGroupClick(group.id) }
                )
            }
        }
    }
}

@Composable
private fun NoteGroupCard(
    groupId: Int,
    name: String,
    isSelected: Boolean = false,
    isHovering: Boolean = false,
    dragDropState: DragDropState,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .size(64.dp)
            .onGloballyPositioned { coordinates ->
                // 注册分组卡片的位置
                val position = coordinates.positionInRoot()
                val size = coordinates.size.toSize()
                dragDropState.registerGroupPosition(
                    groupId,
                    androidx.compose.ui.geometry.Rect(
                        position.x,
                        position.y,
                        position.x + size.width,
                        position.y + size.height
                    )
                )
            }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isHovering -> Color(0xFF10B981) // 悬停时显示绿色
                isSelected -> Color(0xFF3B82F6)
                else -> Color(0xFFF1F5F9)
            }
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📁",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                fontSize = 10.sp,
                color = when {
                    isHovering -> Color.White
                    isSelected -> Color.White
                    else -> Color(0xFF64748B)
                }
            )
        }
    }
}

@Composable
private fun LearningLogsSection() {
    var pinnedExpanded by remember { mutableStateOf(true) }
    var todayExpanded by remember { mutableStateOf(true) }
    var thisWeekExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "单词",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 学习日志列表
        LogCategorySection(
            icon = "📌",
            title = "置顶",
            logs = listOf("重要单词复习"),
            isExpanded = pinnedExpanded,
            onToggle = { pinnedExpanded = !pinnedExpanded },
            hasBackground = true
        )

        LogCategorySection(
            icon = "📅",
            title = "今天",
            logs = listOf("学习了 20 个新单词", "复习了 15 个单词"),
            isExpanded = todayExpanded,
            onToggle = { todayExpanded = !todayExpanded },
            hasBackground = false
        )

        LogCategorySection(
            icon = "📅",
            title = "本周",
            logs = listOf("完成 3 次学习", "掌握 50 个单词"),
            isExpanded = thisWeekExpanded,
            onToggle = { thisWeekExpanded = !thisWeekExpanded },
            hasBackground = false
        )

        LogCategorySection(
            icon = "📅",
            title = "更早",
            logs = listOf("上周学习记录", "上月学习记录"),
            isExpanded = true,
            onToggle = {},
            hasBackground = true,
            showToggle = false
        )
    }
}

@Composable
private fun LogCategorySection(
    icon: String,
    title: String,
    logs: List<String>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    hasBackground: Boolean,
    showToggle: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 分组标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(if (hasBackground) Color(0xFFF8FAFC) else Color.Transparent)
                .clickable(enabled = showToggle) { onToggle() }
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = icon,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            if (showToggle) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = if (isExpanded) "收起" else "展开",
                    tint = Color(0xFF64748B),
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (isExpanded) 90f else 0f)
                )
            }
        }

        // 日志项（可折叠）
        if (isExpanded) {
            logs.forEach { log ->
                LogItem(content = log)
            }
        }
    }
}

@Composable
private fun LogItem(content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = content,
            fontSize = 14.sp,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "2 小时前",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )
    }
    Divider(
        color = Color(0xFFE2E8F0),
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

// 拖拽预览层
@Composable
private fun DragPreviewLayer(
    note: com.english.accelerator.data.Note,
    offset: Offset
) {
    // 计算卡片左上角位置（减去卡片尺寸的一半，使卡片中心对齐手指位置）
    val cardWidth = 100.dp
    val cardHeight = 120.dp

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
                .offset(
                    x = offset.x.dp - cardWidth / 2,
                    y = offset.y.dp - cardHeight / 2
                )
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF1F5F9).copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    text = note.title.ifEmpty { "无标题" },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 3
                )
            }
        }
    }
}

// 分组选择器弹窗
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupSelectorDialog(
    note: com.english.accelerator.data.Note,
    onDismiss: () -> Unit,
    onGroupSelected: (Int?) -> Unit
) {
    val groups = com.english.accelerator.data.NoteGroupManager.getAllGroups()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "移动笔记到分组",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "选择要移动到的分组：",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 移除分组选项
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGroupSelected(null) },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (note.groupId == null) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📋",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "全部笔记（无分组）",
                            fontSize = 14.sp,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 分组列表
                groups.forEach { group ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGroupSelected(group.id) },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (note.groupId == group.id) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📁",
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = group.name,
                                fontSize = 14.sp,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

