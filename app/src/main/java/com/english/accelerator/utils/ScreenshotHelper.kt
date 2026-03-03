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
    fun captureAndSave(context: Context, view: View, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        try {
            // 创建 Bitmap
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false

            // 保存到图片库
            val fileName = "Accelerator_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.png"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ 使用 MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Accelerator")
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                    onSuccess("截图已保存到相册")
                } else {
                    onError("保存失败")
                }
            } else {
                // Android 9 及以下使用传统方式
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val acceleratorDir = File(picturesDir, "Accelerator")
                if (!acceleratorDir.exists()) {
                    acceleratorDir.mkdirs()
                }

                val file = File(acceleratorDir, fileName)
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }

                // 通知媒体库更新
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DATA, file.absolutePath)
                }
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                onSuccess("截图已保存到相册")
            }

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
    onSuccess: (String) -> Unit = {},
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
