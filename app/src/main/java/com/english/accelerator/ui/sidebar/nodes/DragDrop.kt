package com.english.accelerator.ui.sidebar

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize

/**
 * 拖拽状态管理
 */
class DragDropState {
    // 当前正在拖拽的笔记
    var draggingNote by mutableStateOf<com.english.accelerator.data.Note?>(null)
        private set

    // 拖拽位置
    var dragOffset by mutableStateOf(Offset.Zero)
        private set

    // 是否正在拖拽
    val isDragging: Boolean
        get() = draggingNote != null

    // 分组卡片的位置信息
    private val groupPositions = mutableStateMapOf<Int, Rect>()

    // 当前悬停的分组 ID
    var hoveringGroupId by mutableStateOf<Int?>(null)
        private set

    /**
     * 开始拖拽
     */
    fun startDragging(note: com.english.accelerator.data.Note, startOffset: Offset) {
        draggingNote = note
        dragOffset = startOffset
    }

    /**
     * 更新拖拽位置
     */
    fun updateDragPosition(offset: Offset) {
        dragOffset = offset
        // 检测是否悬停在某个分组上
        hoveringGroupId = detectHoveringGroup(offset)
    }

    /**
     * 结束拖拽
     */
    fun endDragging(): Int? {
        val targetGroupId = hoveringGroupId
        draggingNote = null
        dragOffset = Offset.Zero
        hoveringGroupId = null
        return targetGroupId
    }

    /**
     * 取消拖拽
     */
    fun cancelDragging() {
        draggingNote = null
        dragOffset = Offset.Zero
        hoveringGroupId = null
    }

    /**
     * 注册分组卡片的位置
     */
    fun registerGroupPosition(groupId: Int, rect: Rect) {
        groupPositions[groupId] = rect
    }

    /**
     * 检测拖拽位置是否在某个分组上
     */
    private fun detectHoveringGroup(offset: Offset): Int? {
        return groupPositions.entries.firstOrNull { (_, rect) ->
            rect.contains(offset)
        }?.key
    }
}

/**
 * 创建拖拽状态
 */
@Composable
fun rememberDragDropState(): DragDropState {
    return remember { DragDropState() }
}
