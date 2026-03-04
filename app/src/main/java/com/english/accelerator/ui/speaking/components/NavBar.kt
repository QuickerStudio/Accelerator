package com.english.accelerator.ui.speaking.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 底部导航栏
 */
@Composable
fun NavBar(onNavigateToSettings: () -> Unit) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column {
            Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NavItem(
                    icon = Icons.Default.Book,
                    label = "单词",
                    selected = false,
                    onClick = { }
                )
                NavItem(
                    icon = Icons.Default.Edit,
                    label = "写作",
                    selected = false,
                    onClick = { }
                )
                NavItem(
                    icon = Icons.Default.Chat,
                    label = "对话",
                    selected = true,
                    onClick = { }
                )
                NavItem(
                    icon = Icons.Default.Settings,
                    label = "设置",
                    selected = false,
                    onClick = onNavigateToSettings
                )
            }
        }
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight()
            .padding(vertical = 4.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color(0xFF8B5CF6) else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (selected) Color(0xFF8B5CF6) else Color(0xFF94A3B8)
        )
    }
}
