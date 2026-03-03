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
     */
    fun captureAndSave(context: Context, view: View, onSuccess: (File) -> Unit, onError: (String) -> Unit) {
        try {
            // 创建 Bitmap
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false

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
 */
@Composable
fun rememberScreenshotCapture(
    onSuccess: (File) -> Unit = {},
    onError: (String) -> Unit = {}
): () -> Unit {
    val view = LocalView.current
    val context = androidx.compose.ui.platform.LocalContext.current

    return remember {
        {
            ScreenshotHelper.captureAndSave(context, view.rootView, onSuccess, onError)
        }
    }
}
