package com.english.accelerator.ui.vocabulary.nodes

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.data.Word
import kotlin.math.min

/**
 * 单词卡片节点
 */
class WordCardNode(
    private val word: Word,
    private val onLongPress: () -> Unit
) {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Render() {
        val view = LocalView.current
        val wordLength = word.word.length
        val fontSize = calculateFontSize(wordLength)
        val letterSpacing = calculateLetterSpacing(wordLength)

        Card(
            modifier = Modifier
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
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFFFAFAFA), Color.White)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.3f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = word.word,
                            fontSize = fontSize.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB),
                            textAlign = TextAlign.Center,
                            letterSpacing = letterSpacing.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = word.phonetic,
                            fontSize = 24.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.7f)
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(20.dp))
                            .padding(28.dp)
                    ) {
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

    private fun calculateFontSize(length: Int): Float = when {
        length <= 8 -> 56f
        length <= 12 -> 56f * 0.85f
        length <= 16 -> 56f * 0.7f
        else -> min(56f * 0.6f, 32f).coerceAtLeast(32f)
    }

    private fun calculateLetterSpacing(length: Int): Float = when {
        length <= 8 -> 2f
        length <= 12 -> 1f
        length <= 16 -> 0.5f
        else -> 0f
    }
}
