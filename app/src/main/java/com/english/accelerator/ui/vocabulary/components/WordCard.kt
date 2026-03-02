package com.english.accelerator.ui.vocabulary.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.data.Word

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordCard(
    word: Word,
    onLongPress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    // 卡片总高度：560dp
    // 面积分配：
    // - 单词区域：30% (168dp) - 单词 + 音标
    // - 释义区域：70% (392dp) - 中文释义

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(560.dp)
            .padding(horizontal = 24.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onLongPress()
                }
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFAFAFA),
                            Color.White
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                // 单词区域 - 30% 面积
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 单词 - 超大字体，视觉焦点
                    Text(
                        text = word.word,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 音标 - 大字体，清晰可读
                    Text(
                        text = word.phonetic,
                        fontSize = 24.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 释义区域 - 70% 面积
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f)
                        .background(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(28.dp)
                ) {
                    // 释义 - 左对齐，清晰排列
                    Text(
                        text = word.translation.replace("\\n", "\n"),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Start,
                        lineHeight = 36.sp,
                        letterSpacing = 0.3.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
