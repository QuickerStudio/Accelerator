# 笔记卡片拖拽分类功能实现报告

## 实现日期
2026-03-02

## 功能概述
实现了侧边栏笔记卡片的拖拽分类功能，用户可以通过长按笔记卡片并拖拽到分组卡片上来快速完成笔记分类，操作流程简化为 2 步：长按 → 拖拽 → 松手。

---

## 实现内容

### 1. 核心功能
- **长按触发拖拽**：长按笔记卡片后开始拖拽
- **拖拽预览层**：显示半透明的笔记卡片副本，跟随手指移动
- **悬停反馈**：拖拽到分组卡片上时，分组卡片变为绿色高亮
- **松手更新**：松手时自动更新笔记的分组关联
- **原卡片隐藏**：拖拽时原卡片变透明，内容隐藏

### 2. 技术实现

#### 2.1 拖拽状态管理（DragDropState.kt）
创建了 `DragDropState` 类来管理拖拽状态：

```kotlin
@Stable
class DragDropState {
    var isDragging by mutableStateOf(false)
    var draggingNote by mutableStateOf<Note?>(null)
    var dragOffset by mutableStateOf(Offset.Zero)
    var hoveringGroupId by mutableStateOf<Int?>(null)
    private val groupPositions = mutableStateMapOf<Int, Rect>()

    fun startDragging(note: Note, offset: Offset)
    fun updateDragPosition(offset: Offset)
    fun endDragging(): Int?
    fun cancelDragging()
    fun registerGroupPosition(groupId: Int, rect: Rect)
}
```

**关键方法**：
- `startDragging()`: 开始拖拽，记录笔记和初始位置
- `updateDragPosition()`: 更新拖拽位置，检测悬停的分组
- `endDragging()`: 结束拖拽，返回目标分组 ID
- `registerGroupPosition()`: 注册分组卡片的位置信息

#### 2.2 笔记卡片拖拽手势（NoteCard）
使用 `detectDragGesturesAfterLongPress` 实现拖拽手势：

```kotlin
.pointerInput(note.id) {
    detectDragGesturesAfterLongPress(
        onDragStart = { offset ->
            dragDropState.startDragging(note, cardOffset + Offset(50.dp.toPx(), 60.dp.toPx()))
        },
        onDrag = { change, dragAmount ->
            change.consume()
            dragDropState.updateDragPosition(cardOffset + change.position)
        },
        onDragEnd = {
            val targetGroupId = dragDropState.endDragging()
            if (targetGroupId != null) {
                NoteManager.updateNoteGroup(note.id, targetGroupId)
            }
        },
        onDragCancel = {
            dragDropState.cancelDragging()
        }
    )
}
```

**关键点**：
- 使用 `cardOffset + change.position` 计算手指的屏幕绝对坐标
- 拖拽时原卡片变透明：`containerColor = if (isDragging) Color.Transparent else Color(0xFFF1F5F9)`

#### 2.3 拖拽预览层（DragPreviewLayerInList）
创建了拖拽预览层组件，显示在笔记列表区域：

```kotlin
@Composable
private fun DragPreviewLayerInList(
    note: Note,
    offset: Offset,
    listBoxOffset: Offset
) {
    androidx.compose.ui.platform.LocalDensity.current.run {
        Card(
            modifier = Modifier
                .offset(
                    x = (offset.x - listBoxOffset.x).toDp() - cardWidth / 2,
                    y = (offset.y - listBoxOffset.y).toDp() - cardHeight / 2
                )
        ) {
            // 显示笔记内容
        }
    }
}
```

**关键点**：
- 使用 `.toDp()` 正确转换像素到 dp
- 减去 `listBoxOffset` 将屏幕坐标转换为 Box 内的相对坐标
- 减去卡片尺寸的一半，使卡片中心对齐手指位置

#### 2.4 分组卡片位置注册（NoteGroupCard）
为每个分组卡片注册位置信息：

```kotlin
.onGloballyPositioned { coordinates ->
    val position = coordinates.positionInRoot()
    val size = coordinates.size.toSize()
    dragDropState.registerGroupPosition(
        groupId,
        Rect(
            position.x,
            position.y,
            position.x + size.width,
            position.y + size.height
        )
    )
}
```

**悬停反馈**：
```kotlin
containerColor = when {
    isHovering -> Color(0xFF10B981) // 悬停时显示绿色
    isSelected -> Color(0xFF3B82F6)
    else -> Color(0xFFF1F5F9)
}
```

---

## 技术难点与解决方案

### 难点 1：坐标系转换
**问题**：拖拽预览层在侧边栏内部，但 `positionInRoot()` 返回的是屏幕绝对坐标，导致位置计算错误。

**解决方案**：
1. 获取笔记列表 Box 的屏幕位置 `listBoxOffset`
2. 将屏幕坐标转换为 Box 内的相对坐标：`offset.x - listBoxOffset.x`
3. 使用 `.toDp()` 正确转换像素到 dp

### 难点 2：拖拽预览层位置不准确
**问题**：初期使用 `(offset.x - listBoxOffset.x).dp` 导致位置错误，因为直接把像素值当作 dp 值。

**解决方案**：
使用 `LocalDensity.current.run { (offset.x - listBoxOffset.x).toDp() }` 正确转换。

### 难点 3：拖拽预览层被遮挡
**问题**：在 Compose 中，后绘制的组件会覆盖先绘制的组件。

**解决方案**：
将拖拽预览层放在笔记列表 `Row` 的后面，确保它在最上层。

---

## 界面层级结构

```
Sidebar (侧边栏)
└── Box (侧边栏容器)
    └── Column (滚动内容)
        └── AllNotesSection (全部笔记区域)
            └── Box (笔记列表容器) ← 获取 listBoxOffset
                ├── Row (笔记卡片列表)
                │   └── NoteCard (笔记卡片) ← 拖拽手势
                └── DragPreviewLayerInList (拖拽预览层) ← 在最上层
```

---

## 用户体验优化

1. **视觉反馈**
   - 长按时原卡片变透明
   - 拖拽时显示半透明预览卡片
   - 悬停在分组上时分组变绿色

2. **交互流程**
   - 长按笔记卡片（触发拖拽）
   - 拖拽到分组卡片上（分组变绿色）
   - 松手完成分类（自动更新）

3. **保留备用方案**
   - 保留了长按弹窗选择分组的功能作为备用

---

## 代码文件变更

### 新增文件
- `app/src/main/java/com/english/accelerator/ui/sidebar/DragDropState.kt`

### 修改文件
- `app/src/main/java/com/english/accelerator/ui/sidebar/Sidebar.kt`
  - 添加拖拽状态管理
  - 修改 `NoteCard` 组件，添加拖拽手势
  - 修改 `NoteGroupCard` 组件，添加位置注册和悬停反馈
  - 添加 `DragPreviewLayerInList` 组件

---

## 测试结果

✅ 长按笔记卡片触发拖拽
✅ 拖拽预览层正确显示在笔记列表区域
✅ 拖拽预览层跟随手指移动
✅ 拖拽到分组上时分组变绿色
✅ 松手后笔记分组正确更新
✅ 原卡片在拖拽时变透明

---

## 后续优化建议

1. **性能优化**
   - 考虑使用 `LaunchedEffect` 优化位置注册的频率
   - 减少不必要的重组

2. **用户体验**
   - 添加拖拽开始和结束的震动反馈
   - 添加拖拽动画效果

3. **功能扩展**
   - 支持拖拽到"全部笔记"区域移除分组
   - 支持拖拽排序笔记

---

## 总结

成功实现了笔记卡片的拖拽分类功能，解决了坐标系转换、位置计算、层级遮挡等技术难点。用户体验流畅，操作简单直观，只需 2 步即可完成笔记分类。
