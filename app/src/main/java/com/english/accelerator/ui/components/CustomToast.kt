package com.english.accelerator.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CustomToast(
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    backgroundColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(1500)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = Color(0xFF1E293B),
                fontSize = 14.sp
            )
        }
    }
}
