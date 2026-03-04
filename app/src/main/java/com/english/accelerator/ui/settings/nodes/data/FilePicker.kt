/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 简单的文件浏览器对话框
 *
 * 用于查看应用缓存目录中的文件
 */
@Composable
fun FileExplorerDialog(
    rootPath: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentPath by remember { mutableStateOf(File(rootPath)) }
    var files by remember { mutableStateOf(listOf<File>()) }
    var showDeleteConfirm by remember { mutableStateOf<File?>(null) }
    var showCopyToast by remember { mutableStateOf(false) }
    var imageToView by remember { mutableStateOf<File?>(null) }

    // 加载当前目录的文件
    LaunchedEffect(currentPath) {
        files = try {
            currentPath.listFiles()?.sortedWith(
                compareBy<File> { !it.isDirectory }
                    .thenBy { it.name.lowercase() }
            ) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF8FAFC)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 标题栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF8B5CF6))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "文件浏览器",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭",
                            tint = Color.White
                        )
                    }
                }

                // 当前路径
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE2E8F0)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentPath.absolutePath,
                                fontSize = 12.sp,
                                color = Color(0xFF475569)
                            )
                        }
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("path", currentPath.absolutePath)
                                clipboard.setPrimaryClip(clip)
                                showCopyToast = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "复制路径",
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // 返回上级按钮
                if (currentPath.absolutePath != File(rootPath).absolutePath) {
                    TextButton(
                        onClick = {
                            currentPath.parentFile?.let { parent ->
                                if (parent.absolutePath.startsWith(rootPath)) {
                                    currentPath = parent
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回上级",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("返回上级目录")
                    }
                }

                // 文件列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    items(files) { file ->
                        FileItem(
                            file = file,
                            onClick = {
                                if (file.isDirectory) {
                                    currentPath = file
                                } else if (isImageFile(file)) {
                                    imageToView = file
                                }
                            },
                            onDelete = {
                                showDeleteConfirm = file
                            },
                            onShare = {
                                shareFile(context, file)
                            }
                        )
                    }

                    if (files.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "目录为空",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 复制成功提示
    if (showCopyToast) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            showCopyToast = false
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = Color(0xFFDCFCE7),
                contentColor = Color(0xFF1E293B)
            ) {
                Text("路径已复制到剪贴板")
            }
        }
    }

    // 图片查看对话框
    imageToView?.let { imageFile ->
        ImageViewDialog(
            imageFile = imageFile,
            onDismiss = { imageToView = null },
            onShare = {
                shareFile(context, imageFile)
            }
        )
    }

    // 删除确认对话框
    showDeleteConfirm?.let { fileToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除 ${fileToDelete.name} 吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            if (fileToDelete.isDirectory) {
                                fileToDelete.deleteRecursively()
                            } else {
                                fileToDelete.delete()
                            }
                            // 刷新文件列表
                            files = currentPath.listFiles()?.sortedWith(
                                compareBy<File> { !it.isDirectory }
                                    .thenBy { it.name.lowercase() }
                            ) ?: emptyList()
                        } catch (e: Exception) {
                            // 删除失败
                        }
                        showDeleteConfirm = null
                    }
                ) {
                    Text("删除", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun FileItem(
    file: File,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when {
                        file.isDirectory -> Icons.Default.Folder
                        isImageFile(file) -> Icons.Default.Image
                        else -> Icons.Default.InsertDriveFile
                    },
                    contentDescription = null,
                    tint = when {
                        file.isDirectory -> Color(0xFF8B5CF6)
                        isImageFile(file) -> Color(0xFF10B981)
                        else -> Color(0xFF64748B)
                    },
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = file.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row {
                        Text(
                            text = formatFileSize(file.length()),
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatDate(file.lastModified()),
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Row {
                if (!file.isDirectory) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "分享",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
        bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
        else -> String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0))
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun isImageFile(file: File): Boolean {
    val extension = file.extension.lowercase()
    return extension in listOf("png", "jpg", "jpeg", "gif", "bmp", "webp")
}

private fun shareFile(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = when {
                isImageFile(file) -> "image/*"
                else -> "*/*"
            }
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "分享文件"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
private fun ImageViewDialog(
    imageFile: File,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E293B)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 标题栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = imageFile.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Row {
                        IconButton(onClick = onShare) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "分享",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭",
                                tint = Color.White
                            )
                        }
                    }
                }

                // 图片显示区域
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    val bitmap = remember(imageFile) {
                        try {
                            BitmapFactory.decodeFile(imageFile.absolutePath)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = imageFile.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "无法加载图片",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
