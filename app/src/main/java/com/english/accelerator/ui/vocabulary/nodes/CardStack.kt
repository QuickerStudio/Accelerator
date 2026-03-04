/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ui.vocabulary.nodes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.english.accelerator.data.Word
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 卡片堆栈节点
 */
class CardStack(
    private val words: List<Word>,
    private val currentIndex: Int,
    private val onSwipeLeft: () -> Unit,
    private val onSwipeRight: () -> Unit,
    private val onLongPress: (Word) -> Unit
) {
    @Composable
    fun Render() {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp.value
        val swipeThreshold = screenWidth * 0.3f

        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }
        val animatedOffsetX = remember { Animatable(0f) }
        val animatedOffsetY = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()

        Box {
            for (i in 2 downTo 1) {
                val index = currentIndex + i
                if (index < words.size) {
                    Box(
                        modifier = Modifier
                            .offset(x = (i * 6).dp, y = (i * 10).dp)
                            .scale(1f - i * 0.04f)
                            .graphicsLayer { alpha = 1f - i * 0.15f }
                    ) {
                        WordCardNode(words[index], {}).Render()
                    }
                }
            }

            if (currentIndex < words.size) {
                LaunchedEffect(animatedOffsetX.value, animatedOffsetY.value) {
                    offsetX = animatedOffsetX.value
                    offsetY = animatedOffsetY.value
                }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .graphicsLayer { rotationZ = offsetX / 50f }
                        .pointerInput(currentIndex) {
                            detectDragGestures(
                                onDragEnd = {
                                    scope.launch {
                                        if (abs(offsetX) > swipeThreshold) {
                                            val targetX = if (offsetX > 0) screenWidth * 2 else -screenWidth * 2
                                            animatedOffsetX.animateTo(targetX, tween(300))
                                            if (offsetX > 0) onSwipeRight() else onSwipeLeft()
                                            animatedOffsetX.snapTo(0f)
                                            animatedOffsetY.snapTo(0f)
                                        } else {
                                            animatedOffsetX.animateTo(0f, tween(300))
                                            animatedOffsetY.animateTo(0f, tween(300))
                                        }
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    scope.launch {
                                        animatedOffsetX.snapTo(animatedOffsetX.value + dragAmount.x)
                                        animatedOffsetY.snapTo(animatedOffsetY.value + dragAmount.y)
                                    }
                                }
                            )
                        }
                ) {
                    WordCardNode(words[currentIndex], { onLongPress(words[currentIndex]) }).Render()
                }
            }
        }
    }
}
