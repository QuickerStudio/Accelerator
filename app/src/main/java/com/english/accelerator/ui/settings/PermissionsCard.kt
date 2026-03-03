package com.english.accelerator.ui.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

/**
 * 权限管理卡片 - 自包含组件
 *
 * 功能：
 * - 存储权限管理
 * - 通知权限管理
 * - 麦克风权限管理
 */
@Composable
fun PermissionsCard() {
    val context = LocalContext.current

    // 权限状态
    var storagePermissionGranted by remember {
        mutableStateOf(checkStoragePermission(context))
    }
    var notificationPermissionGranted by remember {
        mutableStateOf(checkNotificationPermission(context))
    }
    var microphonePermissionGranted by remember {
        mutableStateOf(checkMicrophonePermission(context))
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 存储权限
        PermissionItemWithSwitch(
            icon = Icons.Default.Storage,
            title = "存储权限",
            subtitle = "用于保存学习数据和模型文件",
            checked = storagePermissionGranted,
            onCheckedChange = { enabled ->
                if (enabled) {
                    openAppSettings(context)
                } else {
                    storagePermissionGranted = false
                }
            }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 通知权限
        PermissionItemWithSwitch(
            icon = Icons.Default.Notifications,
            title = "通知权限",
            subtitle = "用于发送学习提醒",
            checked = notificationPermissionGranted,
            onCheckedChange = { enabled ->
                if (enabled) {
                    openAppSettings(context)
                } else {
                    notificationPermissionGranted = false
                }
            }
        )

        Divider(color = Color(0xFFE2E8F0))

        // 麦克风权限
        PermissionItemWithSwitch(
            icon = Icons.Default.Mic,
            title = "麦克风权限",
            subtitle = "用于语音练习功能",
            checked = microphonePermissionGranted,
            onCheckedChange = { enabled ->
                if (enabled) {
                    openAppSettings(context)
                } else {
                    microphonePermissionGranted = false
                }
            }
        )
    }
}

/**
 * 检查存储权限
 */
private fun checkStoragePermission(context: android.content.Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Android 11+ 使用 MANAGE_EXTERNAL_STORAGE
        android.os.Environment.isExternalStorageManager()
    } else {
        // Android 10 及以下使用传统权限
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * 检查通知权限
 */
private fun checkNotificationPermission(context: android.content.Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        // Android 12 及以下默认有通知权限
        true
    }
}

/**
 * 检查麦克风权限
 */
private fun checkMicrophonePermission(context: android.content.Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * 打开应用设置页面
 */
private fun openAppSettings(context: android.content.Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

@Composable
private fun PermissionItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8B5CF6),
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF8B5CF6),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFF94A3B8)
            )
        )
    }
}
