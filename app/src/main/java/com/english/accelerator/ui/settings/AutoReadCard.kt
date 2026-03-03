package com.english.accelerator.ui.settings

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

/**
 * 自动朗读设置卡片 - 自包含组件
 *
 * 功能：
 * - 每日自动朗读英语文本/单词/语法开关
 * - 日期时间设置（每周哪些天、每日几点）
 * - 朗读速度设置
 * - 朗读音量设置
 * - 朗读语音选择
 */
@Composable
fun AutoReadCard() {
    var readTextEnabled by remember { mutableStateOf(true) }
    var readWordsEnabled by remember { mutableStateOf(true) }
    var readGrammarEnabled by remember { mutableStateOf(true) }
    var selectedDays by remember { mutableStateOf(setOf(1, 2, 3, 4, 5, 6, 7)) }
    var selectedTime by remember { mutableStateOf("20:00") }
    var showTimePickerDialog by remember { mutableStateOf(false) }

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

        // 日期时间设置
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "日期时间设置",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )

            // 每周哪些天
            Text(
                text = "每周哪些天:",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            // 使用两行布局显示星期按钮
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // 第一行：周一到周四
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    weekDays.take(4).forEachIndexed { index, day ->
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
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = day,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // 第二行：周五到周日
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    weekDays.drop(4).forEachIndexed { index, day ->
                        val dayNumber = index + 5
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
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = day,
                                fontSize = 13.sp
                            )
                        }
                    }
                    // 添加空白占位符以保持对齐
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 每日几点
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "每日几点:",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                OutlinedButton(
                    onClick = { showTimePickerDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF8B5CF6)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF8B5CF6)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = selectedTime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Divider(color = Color(0xFFE2E8F0))

        // 朗读速度
        SettingItemWithArrow(
            icon = Icons.Default.Speed,
            title = "朗读速度",
            subtitle = "设置朗读速度",
            onClick = { /* TODO: 进入朗读速度设置页面 */ }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 朗读音量
        SettingItemWithArrow(
            icon = Icons.Default.VolumeDown,
            title = "朗读音量",
            subtitle = "设置朗读音量",
            onClick = { /* TODO: 进入朗读音量设置页面 */ }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 朗读语音
        SettingItemWithArrow(
            icon = Icons.Default.RecordVoiceOver,
            title = "朗读语音",
            subtitle = "选择朗读语音",
            onClick = { /* TODO: 进入朗读语音选择页面 */ }
        )
    }

    // 时间选择对话框
    if (showTimePickerDialog) {
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            title = { Text("设置时间") },
            text = {
                Column {
                    Text("选择每日朗读时间")
                    Spacer(modifier = Modifier.height(16.dp))
                    // TODO: 添加时间选择器
                    Text(
                        text = "当前时间: $selectedTime",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text("确定", color = Color(0xFF8B5CF6))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text("取消", color = Color(0xFF8B5CF6))
                }
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
