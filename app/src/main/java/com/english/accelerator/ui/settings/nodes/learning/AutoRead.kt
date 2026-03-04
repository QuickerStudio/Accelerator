/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow

/**
 * 自动朗读设置卡片 - 自包含组件
 *
 * 功能：
 * - 每日自动朗读英语文本/单词/语法开关
 * - 日期时间设置（每周哪些天、每日几点）- 可折叠
 * - 朗读速度设置
 * - 朗读音量设置
 * - 朗读语音选择
 */
@Composable
fun AutoReadCard() {
    var readTextEnabled by remember { mutableStateOf(false) }
    var readWordsEnabled by remember { mutableStateOf(false) }
    var readGrammarEnabled by remember { mutableStateOf(false) }
    var isScheduleExpanded by remember { mutableStateOf(false) }
    var isReadSettingsExpanded by remember { mutableStateOf(false) }
    var selectedDays by remember { mutableStateOf(setOf(1, 2, 3, 4, 5, 6, 7)) }
    var startHour by remember { mutableStateOf(0) }
    var startMinute by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(30) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var readSpeed by remember { mutableStateOf(1f) } // 朗读速度 0.5x - 2.0x
    var readVolume by remember { mutableStateOf(0.8f) } // 朗读音量 0.0 - 1.0
    var selectedVoice by remember { mutableStateOf(0) } // 0: 男声, 1: 女声, 2: 童声

    val weekDays = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 每日自动朗读英语文本
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "每日自动朗读英语文本",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = readTextEnabled,
                onCheckedChange = { readTextEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF8B5CF6),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFF94A3B8)
                )
            )
        }

        Divider(color = Color(0xFFE2E8F0))

        // 每日自动朗读英语单词
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LibraryBooks,
                contentDescription = null,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "每日自动朗读英语单词",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = readWordsEnabled,
                onCheckedChange = { readWordsEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF8B5CF6),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFF94A3B8)
                )
            )
        }

        Divider(color = Color(0xFFE2E8F0))

        // 每日自动阅读英语语法
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "每日自动阅读英语语法",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = readGrammarEnabled,
                onCheckedChange = { readGrammarEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF8B5CF6),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFF94A3B8)
                )
            )
        }

        Divider(color = Color(0xFFE2E8F0))

        // 日期时间设置 - 可折叠标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isScheduleExpanded = !isScheduleExpanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "日期时间设置",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isScheduleExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }

        // 可折叠的日期时间设置内容
        AnimatedVisibility(
            visible = isScheduleExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 每周哪些天
                Text(
                    text = "每周哪些天:",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )

                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    weekDays.forEachIndexed { index, day ->
                        val dayNumber = index + 1
                        val isSelected = selectedDays.contains(dayNumber)
                        OutlinedButton(
                            onClick = {
                                selectedDays = if (isSelected) {
                                    selectedDays - dayNumber
                                } else {
                                    selectedDays + dayNumber
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) Color(0xFF8B5CF6) else Color.Transparent,
                                contentColor = if (isSelected) Color.White else Color(0xFF8B5CF6)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF8B5CF6)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = day,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 每日开始时间
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePickerDialog = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "每日开始时间",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = TimeFormatter.format(startHour, startMinute),
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

                Divider(color = Color(0xFFE2E8F0))

                // 持续时间 - 使用滑块
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "持续时间",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E293B)
                            )
                        }
                        Text(
                            text = "$duration 分钟",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF8B5CF6)
                        )
                    }
                    Slider(
                        value = duration.toFloat(),
                        onValueChange = { duration = it.toInt() },
                        valueRange = 5f..120f,
                        steps = 22,
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
                            text = "5分钟",
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

        Divider(color = Color(0xFFE2E8F0))

        // 朗读设置 - 可折叠标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isReadSettingsExpanded = !isReadSettingsExpanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "朗读设置",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isReadSettingsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }

        // 可折叠的朗读设置内容
        AnimatedVisibility(
            visible = isReadSettingsExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 朗读速度
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "朗读速度",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "${String.format("%.1f", readSpeed)}x",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF8B5CF6)
                        )
                    }
                    Slider(
                        value = readSpeed,
                        onValueChange = { readSpeed = it },
                        valueRange = 0.5f..2.0f,
                        steps = 14,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF8B5CF6),
                            activeTrackColor = Color(0xFF8B5CF6),
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        )
                    )
                }

                Divider(color = Color(0xFFE2E8F0))

                // 朗读音量
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "朗读音量",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "${(readVolume * 100).toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF8B5CF6)
                        )
                    }
                    Slider(
                        value = readVolume,
                        onValueChange = { readVolume = it },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF8B5CF6),
                            activeTrackColor = Color(0xFF8B5CF6),
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        )
                    )
                }

                Divider(color = Color(0xFFE2E8F0))

                // 朗读语音选择 - 分段胶囊按钮
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "朗读语音",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("男声", "女声", "童声").forEachIndexed { index, voice ->
                            OutlinedButton(
                                onClick = { selectedVoice = index },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedVoice == index) Color(0xFF8B5CF6) else Color.Transparent,
                                    contentColor = if (selectedVoice == index) Color.White else Color(0xFF8B5CF6)
                                ),
                                border = BorderStroke(1.dp, Color(0xFF8B5CF6)),
                                shape = when (index) {
                                    0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                    2 -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                    else -> RoundedCornerShape(0.dp)
                                },
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text(
                                    text = voice,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 时间选择对话框
    if (showTimePickerDialog) {
        TimePickerDialog(
            title = "选择每日开始时间",
            initialHour = startHour,
            initialMinute = startMinute,
            onDismiss = { showTimePickerDialog = false },
            onConfirm = { hour, minute ->
                startHour = hour
                startMinute = minute
                showTimePickerDialog = false
            }
        )
    }
}

@Composable
private fun SettingItemWithArrow(
    icon: ImageVector,
    title: String,
    subtitle: String,
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}
