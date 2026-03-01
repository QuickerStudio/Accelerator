package com.english.accelerator.ui.sidebar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Sidebar(
    isOpen: Boolean,
    onClose: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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
                AllNotesSection()

                Spacer(modifier = Modifier.height(24.dp))

                // 笔记分组区域
                NoteGroupsSection()

                Spacer(modifier = Modifier.height(24.dp))

                // 单词学习日志
                LearningLogsSection()
            }
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

@Composable
private fun AllNotesSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "全部笔记",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            // 新建笔记按钮
            Button(
                onClick = { /* TODO: 新建笔记 */ },
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

        // 笔记列表（水平滚动）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 10个示例笔记卡片
            repeat(10) { index ->
                NoteCard(
                    title = "笔记 ${index + 1}",
                    preview = "这是笔记 ${index + 1} 的预览内容..."
                )
            }
        }
    }
}

@Composable
private fun NoteCard(title: String, preview: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F5F9)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
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

@Composable
private fun NoteGroupsSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "笔记分组",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

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
            // 10个示例分组
            repeat(10) { index ->
                NoteGroupCard(name = "分组 ${index + 1}")
            }
        }
    }
}

@Composable
private fun NoteGroupCard(name: String) {
    Card(
        modifier = Modifier.size(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F5F9)
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
                color = Color(0xFF64748B)
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
