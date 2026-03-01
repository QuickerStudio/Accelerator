# 动画与交互规范

## 1. 页面转场动画

```kotlin
// 进入动画
val enterTransition = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + fadeIn(animationSpec = tween(300))

// 退出动画
val exitTransition = slideOutHorizontally(
    targetOffsetX = { -it / 3 },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + fadeOut(animationSpec = tween(300))
```

---

## 2. 卡片翻转动画

```kotlin
val rotation by animateFloatAsState(
    targetValue = if (isFlipped) 180f else 0f,
    animationSpec = tween(400, easing = FastOutSlowInEasing)
)
```

---

## 3. 按钮点击动画

```kotlin
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.95f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
)
```

---

## 4. 列表项出现动画

```kotlin
LazyColumn {
    itemsIndexed(items) { index, item ->
        val animatedModifier = Modifier.animateItemPlacement(
            animationSpec = tween(300)
        )
        // Item content
    }
}
```

---

## 响应式设计

### 屏幕尺寸适配

```kotlin
enum class WindowSize {
    Compact,   // < 600dp
    Medium,    // 600dp - 840dp
    Expanded   // > 840dp
}

// 根据屏幕尺寸调整布局
when (windowSize) {
    WindowSize.Compact -> {
        // 单列布局
        // 功能卡片 2列
    }
    WindowSize.Medium -> {
        // 双列布局
        // 功能卡片 3列
    }
    WindowSize.Expanded -> {
        // 三列布局
        // 功能卡片 4列
    }
}
```

### 横屏适配

- 单词卡片: 宽度限制为600.dp，居中显示
- 对话列表: 最大宽度800.dp，居中显示
- 写作编辑: 最大宽度900.dp，居中显示
