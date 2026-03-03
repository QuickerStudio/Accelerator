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
    var startTime by remember { mutableStateOf("00:00") }
    var duration by remember { mutableStateOf("0") }
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "每日开始时间:",
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
                            text = startTime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 持续时间
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "持续时间:",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { newValue ->
                            // 只允许输入数字
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                duration = newValue
                            }
                        },
                        modifier = Modifier.width(100.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8B5CF6),
                            unfocusedBorderColor = Color(0xFF8B5CF6),
                            focusedTextColor = Color(0xFF8B5CF6),
                            unfocusedTextColor = Color(0xFF8B5CF6)
                        ),
                        singleLine = true
                    )
                    Text(
                        text = "分钟",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
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
            Column {
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
        }
    }

    // 开始时间选择对话框
    if (showTimePickerDialog) {
        var selectedHour by remember { mutableStateOf(0) }
        var selectedMinute by remember { mutableStateOf(0) }

        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            title = { Text("设置开始时间") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("选择每日朗读开始时间")
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 小时选择
                        OutlinedTextField(
                            value = selectedHour.toString().padStart(2, '0'),
                            onValueChange = { newValue ->
                                val hour = newValue.toIntOrNull()
                                if (hour != null && hour in 0..23) {
                                    selectedHour = hour
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            ),
                            singleLine = true,
                            label = { Text("时") }
                        )

                        Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                        // 分钟选择
                        OutlinedTextField(
                            value = selectedMinute.toString().padStart(2, '0'),
                            onValueChange = { newValue ->
                                val minute = newValue.toIntOrNull()
                                if (minute != null && minute in 0..59) {
                                    selectedMinute = minute
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            ),
                            singleLine = true,
                            label = { Text("分") }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        startTime = "${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}"
                        showTimePickerDialog = false
                    }
                ) {
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
