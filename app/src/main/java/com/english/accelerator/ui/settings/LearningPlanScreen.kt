package com.english.accelerator.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth

/**
 * 学习计划设置页面
 *
 * 功能：
 * - 学习提醒总开关
 * - 提醒时间设置
 * - 日历选择提醒日期
 * - 每日学习时间段设置
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningPlanScreen(
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    // 状态管理
    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderHour by remember { mutableStateOf(20) }
    var reminderMinute by remember { mutableStateOf(0) }
    var selectedDays by remember { mutableStateOf(setOf<Int>()) } // 选中的日期
    var dailyStartHour by remember { mutableStateOf(9) }
    var dailyStartMinute by remember { mutableStateOf(0) }
    var dailyEndHour by remember { mutableStateOf(22) }
    var dailyEndMinute by remember { mutableStateOf(0) }

    // 对话框状态
    var showReminderTimePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学习计划") },
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
            // 学习提醒设置
            SettingsCard(title = "学习提醒设置") {
                Column {
                    // 系统通知开关
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "学习提醒信息",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "发送系统消息通知",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF8B5CF6),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFF94A3B8)
                            )
                        )
                    }

                    if (reminderEnabled) {
                        Divider(color = Color(0xFFE2E8F0))

                        // 提醒时间
                        TimeSettingItem(
                            icon = Icons.Default.AccessTime,
                            title = "提醒时间",
                            time = TimeFormatter.format(reminderHour, reminderMinute),
                            onClick = { showReminderTimePicker = true }
                        )

                        Divider(color = Color(0xFFE2E8F0))

                        // 提醒日期选择（日历）
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = Color(0xFF8B5CF6),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "选择提醒日期",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                                Text(
                                    text = if (selectedDays.isEmpty()) {
                                        "点击日期选择提醒日"
                                    } else {
                                        "已选择 ${selectedDays.size} 天"
                                    },
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            // 日历网格
                            MonthCalendarGrid(
                                selectedDays = selectedDays,
                                onDayClick = { day ->
                                    selectedDays = if (selectedDays.contains(day)) {
                                        selectedDays - day
                                    } else {
                                        selectedDays + day
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // 每日学习时间段
            SettingsCard(title = "每日学习时间段") {
                Column {
                    // 开始时间
                    TimeSettingItem(
                        icon = Icons.Default.WbSunny,
                        title = "每日开始时间",
                        time = TimeFormatter.format(dailyStartHour, dailyStartMinute),
                        onClick = { showStartTimePicker = true }
                    )

                    Divider(color = Color(0xFFE2E8F0))

                    // 结束时间
                    TimeSettingItem(
                        icon = Icons.Default.NightsStay,
                        title = "每日结束时间",
                        time = TimeFormatter.format(dailyEndHour, dailyEndMinute),
                        onClick = { showEndTimePicker = true }
                    )
                }
            }

            // 提示信息
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
                    Text(
                        text = "系统会在选定的日期和时间提醒你学习，帮助你养成良好的学习习惯。",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    // 时间选择器对话框
    if (showReminderTimePicker) {
        TimePickerDialog(
            title = "选择提醒时间",
            initialHour = reminderHour,
            initialMinute = reminderMinute,
            onDismiss = { showReminderTimePicker = false },
            onConfirm = { hour, minute ->
                // 验证提醒时间是否在学习时间段内
                val reminderMinutes = hour * 60 + minute
                val startMinutes = dailyStartHour * 60 + dailyStartMinute
                val endMinutes = dailyEndHour * 60 + dailyEndMinute

                if (reminderMinutes >= startMinutes && reminderMinutes <= endMinutes) {
                    reminderHour = hour
                    reminderMinute = minute
                    showReminderTimePicker = false
                } else {
                    // 提示用户时间超出范围
                    // TODO: 显示错误提示
                    showReminderTimePicker = false
                }
            }
        )
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            title = "选择开始时间",
            initialHour = dailyStartHour,
            initialMinute = dailyStartMinute,
            onDismiss = { showStartTimePicker = false },
            onConfirm = { hour, minute ->
                dailyStartHour = hour
                dailyStartMinute = minute
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            title = "选择结束时间",
            initialHour = dailyEndHour,
            initialMinute = dailyEndMinute,
            onDismiss = { showEndTimePicker = false },
            onConfirm = { hour, minute ->
                dailyEndHour = hour
                dailyEndMinute = minute
                showEndTimePicker = false
            }
        )
    }
}

/**
 * 月度日历网格组件（固定31天）
 */
@Composable
private fun MonthCalendarGrid(
    selectedDays: Set<Int>,
    onDayClick: (Int) -> Unit
) {
    val today = remember { LocalDate.now().dayOfMonth }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 星期标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // 日期网格和重置按钮
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 日期网格（固定31天）
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 添加日期（1-31）
                items(31) { index ->
                    val day = index + 1
                    val isSelected = selectedDays.contains(day)
                    val isToday = day == today

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(if (isToday) 3.dp else 0.dp), // 为今天的外圈留出空间
                        contentAlignment = Alignment.Center
                    ) {
                        // 今天的外圈
                        if (isToday) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = 2.dp,
                                        color = Color(0xFF8B5CF6),
                                        shape = CircleShape
                                    )
                            )
                        }

                        // 日期圆形背景
                        Box(
                            modifier = Modifier
                                .fillMaxSize(if (isToday) 0.75f else 1f) // 今天的圆形缩小
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> Color(0xFF8B5CF6)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { onDayClick(day) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                fontSize = 14.sp,
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isSelected -> Color.White
                                    isToday -> Color(0xFF8B5CF6)
                                    else -> Color(0xFF1E293B)
                                }
                            )
                        }
                    }
                }
            }

            // 悬浮重置按钮
            FloatingActionButton(
                onClick = {
                    // 清空所有选中的日期
                    selectedDays.forEach { day ->
                        onDayClick(day)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(y = 100.dp)
                    .width(200.dp)
                    .height(40.dp),
                containerColor = Color(0xFFF1F5F9),
                contentColor = Color(0xFF64748B),
                shape = RoundedCornerShape(30.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "重置",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "重置所有选择日期",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

/**
 * 设置卡片容器
 */
@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            content()
        }
    }
}

/**
 * 时间设置项
 */
@Composable
private fun TimeSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    time: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
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
            color = Color(0xFF1E293B),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = time,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF8B5CF6)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}
