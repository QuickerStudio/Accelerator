package com.english.accelerator.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

/**
 * 学习计划设置页面
 *
 * 功能：
 * - 每日开始时间设置（使用专业时间选择器）
 * - 每日结束时间设置（使用专业时间选择器）
 * - 学习提醒时间设置
 * - 学习目标设置
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningPlanScreen(
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    // 状态管理
    var dailyStartHour by remember { mutableStateOf(9) }
    var dailyStartMinute by remember { mutableStateOf(0) }
    var dailyEndHour by remember { mutableStateOf(22) }
    var dailyEndMinute by remember { mutableStateOf(0) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderHour by remember { mutableStateOf(20) }
    var reminderMinute by remember { mutableStateOf(0) }
    var dailyGoalMinutes by remember { mutableStateOf(30) }

    // 对话框状态
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showReminderTimePicker by remember { mutableStateOf(false) }

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

            // 学习提醒
            SettingsCard(title = "学习提醒") {
                Column {
                    // 提醒开关
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
                        Text(
                            text = "启用学习提醒",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.weight(1f)
                        )
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
                    }
                }
            }

            // 每日学习目标
            SettingsCard(title = "每日学习目标") {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "每日学习时长",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B)
                        )
                    }

                    // 时长滑块
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$dailyGoalMinutes 分钟",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5CF6)
                        )

                        Slider(
                            value = dailyGoalMinutes.toFloat(),
                            onValueChange = { dailyGoalMinutes = it.toInt() },
                            valueRange = 10f..120f,
                            steps = 21,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF8B5CF6),
                                activeTrackColor = Color(0xFF8B5CF6),
                                inactiveTrackColor = Color(0xFFE2E8F0)
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "10分钟",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "120分钟",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // 说明文字
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
                        text = "系统会在设定的时间段内提醒你学习，帮助你养成良好的学习习惯。",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    // 时间选择器对话框
    if (showStartTimePicker) {
        TimePickerDialog(
            title = "选择每日开始时间",
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
            title = "选择每日结束时间",
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

    if (showReminderTimePicker) {
        TimePickerDialog(
            title = "选择提醒时间",
            initialHour = reminderHour,
            initialMinute = reminderMinute,
            onDismiss = { showReminderTimePicker = false },
            onConfirm = { hour, minute ->
                reminderHour = hour
                reminderMinute = minute
                showReminderTimePicker = false
            }
        )
    }
}

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
