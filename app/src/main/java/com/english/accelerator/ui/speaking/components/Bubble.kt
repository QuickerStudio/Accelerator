package com.english.accelerator.ui.speaking.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.english.accelerator.ui.speaking.models.Message
import java.text.SimpleDateFormat
import java.util.*

/**
 * 消息气泡
 */
@Composable
fun Bubble(message: Message) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val isStreaming = !message.isFromUser && message.content.isNotEmpty() && message.inferenceStats == null

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Box(
                modifier = Modifier
                    .then(
                        if (message.isFromUser) {
                            Modifier
                                .clip(RoundedCornerShape(18.dp, 18.dp, 18.dp, 6.dp))
                                .background(Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))))
                        } else {
                            Modifier
                                .clip(RoundedCornerShape(18.dp, 18.dp, 6.dp, 18.dp))
                                .background(Color(0xFFF8FAFC))
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = message.content,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = if (message.isFromUser) Color.White else Color(0xFF1E293B),
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (isStreaming) {
                        Spacer(modifier = Modifier.width(6.dp))
                        TypingDot()
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = 6.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeFormat.format(Date(message.timestamp)),
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                if (!message.isFromUser && message.inferenceStats != null) {
                    val stats = message.inferenceStats
                    Text(
                        text = "• ${String.format("%.1f", stats.durationSeconds)}s • ${String.format("%.1f", stats.tokensPerSecond)} tok/s • ${stats.memoryUsedMB}MB",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(48.dp))
    }
}

/**
 * 打字指示器
 */
@Composable
fun TypingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(6.dp)
            .background(Color(0xFF8B5CF6).copy(alpha = alpha), CircleShape)
    )
}
