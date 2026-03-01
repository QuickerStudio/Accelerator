package com.english.accelerator.ui.vocabulary.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

@Composable
fun WordCardStack(
    words: List<Word>,
    currentIndex: Int,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onLongPress: (Word) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp.value
    val swipeThreshold = screenWidth * 0.3f

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX = remember { Animatable(0f) }
    val animatedOffsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        // 显示后面的 2 张卡片作为堆叠效果
        for (i in 2 downTo 1) {
            val index = currentIndex + i
            if (index < words.size) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (i * 6).dp,
                            y = (i * 10).dp
                        )
                        .scale(1f - i * 0.04f)
                        .graphicsLayer {
                            alpha = 1f - i * 0.15f
                        }
                ) {
                    WordCard(word = words[index])
                }
            }
        }

        // 当前卡片（可滑动）
        if (currentIndex < words.size) {
            LaunchedEffect(animatedOffsetX.value, animatedOffsetY.value) {
                offsetX = animatedOffsetX.value
                offsetY = animatedOffsetY.value
            }

            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .graphicsLayer {
                        rotationZ = offsetX / 50f
                    }
                    .pointerInput(currentIndex) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    if (abs(offsetX) > swipeThreshold) {
                                        // 滑出动画
                                        val targetX = if (offsetX > 0) screenWidth * 2 else -screenWidth * 2
                                        animatedOffsetX.animateTo(
                                            targetValue = targetX,
                                            animationSpec = tween(300)
                                        )

                                        if (offsetX > 0) {
                                            onSwipeRight()
                                        } else {
                                            onSwipeLeft()
                                        }

                                        // 重置位置
                                        animatedOffsetX.snapTo(0f)
                                        animatedOffsetY.snapTo(0f)
                                    } else {
                                        // 回弹动画
                                        animatedOffsetX.animateTo(0f, animationSpec = tween(300))
                                        animatedOffsetY.animateTo(0f, animationSpec = tween(300))
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
                WordCard(
                    word = words[currentIndex],
                    onLongPress = { onLongPress(words[currentIndex]) }
                )
            }
        }
    }
}
