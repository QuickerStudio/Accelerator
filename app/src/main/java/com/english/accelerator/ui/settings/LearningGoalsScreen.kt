package com.english.accelerator.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.utils.WordLoader

/**
 * 学习目标设置页面
 *
 * 功能：
 * - 每日学习单词数设置（0=不限制，>0=固定池）
 * - 每日写作数设置
 * - 每日对话数设置
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningGoalsScreen(
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    // 状态管理 - 从 WordLoader 加载初始值
    var dailyWordGoal by remember { mutableStateOf(WordLoader.getPoolSize()) }
    var dailyWritingGoal by remember { mutableStateOf(0) }
    var dailyConversationGoal by remember { mutableStateOf(0) }

    // 保存单词目标到 WordLoader
    LaunchedEffect(dailyWordGoal) {
        WordLoader.setPoolSize(dailyWordGoal)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学习目标") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1E293B),
                    navigationIconContentColor = Color(0xFF8B5CF6)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 每日学习单词数
            GoalCard(
                title = "每日学习单词数",
                icon = Icons.Default.MenuBook,
                goalValue = dailyWordGoal,
                onGoalChange = { dailyWordGoal = it },
                minValue = 0,
                maxValue = 200,
                steps = 19,
                unit = "个",
                zeroLabel = "不限制（默认模式）"
            )

            // 每日写作数
            GoalCard(
                title = "每日写作数",
                icon = Icons.Default.Edit,
                goalValue = dailyWritingGoal,
                onGoalChange = { dailyWritingGoal = it },
                minValue = 0,
                maxValue = 10,
                steps = 9,
                unit = "篇",
                zeroLabel = "不限制"
            )

            // 每日对话数
            GoalCard(
                title = "每日对话数",
                icon = Icons.Default.Chat,
                goalValue = dailyConversationGoal,
                onGoalChange = { dailyConversationGoal = it },
                minValue = 0,
                maxValue = 20,
                steps = 19,
                unit = "次",
                zeroLabel = "不限制"
            )
        }
    }
}

@Composable
private fun GoalCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    goalValue: Int,
    onGoalChange: (Int) -> Unit,
    minValue: Int,
    maxValue: Int,
    steps: Int,
    unit: String,
    zeroLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 标题
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                )
            }

            // 目标值显示
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (goalValue == 0) zeroLabel else "$goalValue $unit",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B5CF6)
                )
                if (goalValue == 0 && title.contains("单词")) {
                    Text(
                        text = "（默认模式）",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // 滑块
            Slider(
                value = goalValue.toFloat(),
                onValueChange = { onGoalChange(it.toInt()) },
                valueRange = minValue.toFloat()..maxValue.toFloat(),
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF8B5CF6),
                    activeTrackColor = Color(0xFF8B5CF6),
                    inactiveTrackColor = Color(0xFFE2E8F0)
                )
            )

            // 范围标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (minValue == 0) "$minValue（不限制）" else "$minValue$unit",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "$maxValue$unit",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
