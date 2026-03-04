/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.google.accompanist.flowlayout.FlowRow
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * 学习统计显示页面
 *
 * 统计维度：
 * - 单词学习：已记住单词数、学习单词数、复习次数
 * - 写作练习：完成作文数、总字数
 * - 对话练习：对话次数、对话轮次
 * - 学习日历：标记哪些天有学习记录
 * - 应用使用时长：仅作参考
 *
 * 每月最后一天 00:00 自动重置统计
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningStatsScreen(
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    // 当前月份信息
    val currentMonth = remember { YearMonth.now() }
    val daysInMonth = currentMonth.lengthOfMonth()
    val currentDay = LocalDate.now().dayOfMonth
    val daysUntilReset = daysInMonth - currentDay

    // 模拟数据 - 实际应该从数据库读取
    // TODO: 集成 WordLearningManager 获取真实数据
    val memorizedWords = remember { mutableStateOf(156) }
    val studiedWords = remember { mutableStateOf(320) }
    val reviewCount = remember { mutableStateOf(45) }

    val essayCount = remember { mutableStateOf(8) }
    val totalWords = remember { mutableStateOf(2450) }

    val conversationCount = remember { mutableStateOf(12) }
    val conversationRounds = remember { mutableStateOf(156) }

    val appUsageMinutes = remember { mutableStateOf(1050) }
    val streakDays = remember { mutableStateOf(7) }
    val averageEssayScore = remember { mutableStateOf(85) }
    val averageSpeakingScore = remember { mutableStateOf(78) }

    // 学习日历数据 - 哪些天有学习记录
    val studyDays = remember { mutableStateOf(setOf(1, 2, 3, 5, 6, 7, 9, 10, 12, 14, 15, 16, 18, 20, 21, 22, 23, 25, 27, 28, 29, 30)) }

    // 日历显示状态
    var showCalendar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学习统计") },
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
            // 本月概览卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B5CF6)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy年MM月")),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "$daysUntilReset 天后重置统计",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        IconButton(
                            onClick = { showCalendar = !showCalendar }
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "切换日历显示",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .size(32.dp)
                                )
                            }
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.3f))

                    // 连续学习天数
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "本月学习天数",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${studyDays.value.size}",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "天",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "连续学习",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB800),
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "${streakDays.value}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "天",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 学习日历（可折叠）
            if (showCalendar) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "学习日历",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )

                    // 日历网格
                    FlowRow(
                        mainAxisSpacing = 6.dp,
                        crossAxisSpacing = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (day in 1..daysInMonth) {
                            val hasStudied = studyDays.value.contains(day)
                            val isToday = day == currentDay

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when {
                                    hasStudied -> Color(0xFF8B5CF6)
                                    isToday -> Color(0xFFF1F5F9)
                                    else -> Color.Transparent
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .then(
                                        if (!hasStudied && !isToday) {
                                            Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                                        } else Modifier
                                    )
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "$day",
                                        fontSize = 12.sp,
                                        fontWeight = if (hasStudied || isToday) FontWeight.SemiBold else FontWeight.Normal,
                                        color = when {
                                            hasStudied -> Color.White
                                            isToday -> Color(0xFF8B5CF6)
                                            else -> Color(0xFF94A3B8)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 图例
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LegendItem(color = Color(0xFF8B5CF6), label = "已学习")
                        LegendItem(color = Color(0xFFF1F5F9), label = "今天", textColor = Color(0xFF8B5CF6))
                    }
                }
            }
            }

            // 分类统计标题
            Text(
                text = "分类统计",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )

            // 单词学习统计
            VocabularyStatsCard(
                memorizedWords = memorizedWords.value,
                studiedWords = studiedWords.value,
                reviewCount = reviewCount.value
            )

            // 写作练习统计
            WritingStatsCard(
                essayCount = essayCount.value,
                totalWords = totalWords.value,
                averageScore = averageEssayScore.value
            )

            // 对话练习统计
            SpeakingStatsCard(
                conversationCount = conversationCount.value,
                conversationRounds = conversationRounds.value,
                averageScore = averageSpeakingScore.value
            )

            // 应用使用时长（参考）
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF1F5F9)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "应用使用时长（参考）",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "从应用打开到关闭的总时长",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Text(
                        text = "${appUsageMinutes.value} 分钟",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // 说明卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF1F5F9)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "统计说明",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "• 统计数据每月自动重置\n• 重置时间：每月最后一天 00:00\n• 连续天数：每日至少学习一次即可保持\n• 应用使用时长仅供参考，不代表实际学习时长",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    textColor: Color = Color(0xFF64748B)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = color,
            modifier = Modifier.size(16.dp)
        ) {}
        Text(
            text = label,
            fontSize = 12.sp,
            color = textColor
        )
    }
}

@Composable
private fun VocabularyStatsCard(
    memorizedWords: Int,
    studiedWords: Int,
    reviewCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 标题行
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF8B5CF6).copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }
                Text(
                    text = "单词学习",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
            }

            // 统计数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "已记住",
                    value = "$memorizedWords",
                    unit = "个",
                    color = Color(0xFF8B5CF6)
                )
                StatItem(
                    label = "学习中",
                    value = "${studiedWords - memorizedWords}",
                    unit = "个",
                    color = Color(0xFF8B5CF6)
                )
                StatItem(
                    label = "复习次数",
                    value = "$reviewCount",
                    unit = "次",
                    color = Color(0xFF8B5CF6)
                )
            }

            Divider(color = Color(0xFFE2E8F0))

            // 记忆率
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "记忆率",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "${(memorizedWords * 100 / studiedWords.coerceAtLeast(1))}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8B5CF6)
                    )
                }
                LinearProgressIndicator(
                    progress = { memorizedWords.toFloat() / studiedWords.coerceAtLeast(1) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF8B5CF6),
                    trackColor = Color(0xFFE2E8F0),
                )
            }
        }
    }
}

@Composable
private fun WritingStatsCard(
    essayCount: Int,
    totalWords: Int,
    averageScore: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 标题行
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF3B82F6).copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }
                Text(
                    text = "写作练习",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
            }

            // 统计数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "完成作文",
                    value = "$essayCount",
                    unit = "篇",
                    color = Color(0xFF3B82F6)
                )
                StatItem(
                    label = "总字数",
                    value = "$totalWords",
                    unit = "词",
                    color = Color(0xFF3B82F6)
                )
                StatItem(
                    label = "平均字数",
                    value = "${totalWords / essayCount.coerceAtLeast(1)}",
                    unit = "词",
                    color = Color(0xFF3B82F6)
                )
            }

            Divider(color = Color(0xFFE2E8F0))

            // AI 平均评分
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "AI 平均评分",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Text(
                    text = "$averageScore 分",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B82F6)
                )
            }
        }
    }
}

@Composable
private fun SpeakingStatsCard(
    conversationCount: Int,
    conversationRounds: Int,
    averageScore: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 标题行
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF10B981).copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }
                Text(
                    text = "对话练习",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
            }

            // 统计数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(
                    label = "对话次数",
                    value = "$conversationCount",
                    unit = "次",
                    color = Color(0xFF10B981)
                )
                StatItem(
                    label = "对话轮次",
                    value = "$conversationRounds",
                    unit = "轮",
                    color = Color(0xFF10B981)
                )
            }

            Divider(color = Color(0xFFE2E8F0))

            // AI 平均评分
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "AI 平均评分",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Text(
                    text = "$averageScore 分",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    unit: String,
    color: Color = Color(0xFF1E293B)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = unit,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}
