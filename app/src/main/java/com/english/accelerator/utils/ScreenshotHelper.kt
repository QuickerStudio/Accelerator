/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * 截图工具类
 *
 * 功能：
 * - 截取当前屏幕内容
 * - 保存到图片库
 * - 支持 Android 10+ 的 Scoped Storage
 */
object ScreenshotHelper {

    /**
     * 截取视图并保存到图片库
     * @param cropRatio 裁剪比例，例如 6f/9f 表示 6:9 的比例，null 表示不裁剪
     * @param offsetDp 向上偏移量（单位：dp），默认为 0
     */
    fun captureAndSave(
        context: Context,
        view: View,
        cropRatio: Float? = null,
        offsetDp: Int = 0,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // 创建 Bitmap
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val originalBitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false

            // 如果需要裁剪，按照指定比例裁剪中心区域
            val bitmap = if (cropRatio != null) {
                val viewWidth = originalBitmap.width
                val viewHeight = originalBitmap.height

                // 计算裁剪后的尺寸（保持宽度，调整高度）
                val cropHeight = (viewWidth / cropRatio).toInt()

                // 如果计算出的高度超过视图高度，则保持高度，调整宽度
                val (finalWidth, finalHeight) = if (cropHeight > viewHeight) {
                    val cropWidth = (viewHeight * cropRatio).toInt()
                    cropWidth to viewHeight
                } else {
                    viewWidth to cropHeight
                }

                // 计算居中裁剪的起始位置，根据参数向上偏移
                val density = context.resources.displayMetrics.density
                val offsetPx = (offsetDp * density).toInt() // dp转换为像素
                val startX = (viewWidth - finalWidth) / 2
                val startY = ((viewHeight - finalHeight) / 2 - offsetPx).coerceAtLeast(0)

                // 裁剪 Bitmap
                val croppedBitmap = Bitmap.createBitmap(
                    originalBitmap,
                    startX.coerceAtLeast(0),
                    startY.coerceAtLeast(0),
                    finalWidth.coerceAtMost(viewWidth),
                    finalHeight.coerceAtMost(viewHeight)
                )
                originalBitmap.recycle()
                croppedBitmap
            } else {
                originalBitmap
            }

            // 保存到应用私有目录
            val fileName = "Accelerator_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.png"
            val pictureDir = File(context.filesDir, "piture")
            if (!pictureDir.exists()) {
                pictureDir.mkdirs()
            }

            val file = File(pictureDir, fileName)
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            onSuccess(file)
            bitmap.recycle()
        } catch (e: Exception) {
            onError("截图失败: ${e.message}")
        }
    }
}

/**
 * Composable 函数：获取当前视图用于截图
 * @param cropRatio 裁剪比例，例如 6f/9f 表示 6:9 的比例，null 表示不裁剪
 * @param offsetDp 向上偏移量（单位：dp），默认为 0
 */
@Composable
fun rememberScreenshotCapture(
    cropRatio: Float? = null,
    offsetDp: Int = 0,
    onSuccess: (File) -> Unit = {},
    onError: (String) -> Unit = {}
): () -> Unit {
    val view = LocalView.current
    val context = androidx.compose.ui.platform.LocalContext.current

    return remember {
        {
            ScreenshotHelper.captureAndSave(context, view.rootView, cropRatio, offsetDp, onSuccess, onError)
        }
    }
}
