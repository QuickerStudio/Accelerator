/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import kotlinx.coroutines.delay
import java.io.File

/**
 * 截图成功通知组件
 * 显示10秒后自动消失
 */
@Composable
fun ScreenshotNotification(
    imageFile: File,
    onDismiss: () -> Unit,
    onOpenImage: () -> Unit,
    context: Context
) {
    LaunchedEffect(Unit) {
        delay(10000) // 10秒后自动消失
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(top = 80.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "截图成功！",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // AI讲解按钮
                Button(
                    onClick = {
                        // TODO: AI讲解功能
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("AI讲解", fontSize = 14.sp)
                }

                // 打开图片按钮
                Button(
                    onClick = {
                        onOpenImage()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("打开图片", fontSize = 14.sp)
                }

                // 分享图片按钮
                Button(
                    onClick = {
                        shareImage(context, imageFile)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("分享图片", fontSize = 14.sp)
                }
            }
        }
    }
}

private fun shareImage(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "分享图片"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
