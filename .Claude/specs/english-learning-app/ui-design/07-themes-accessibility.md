# 主题与无障碍设计

## 暗色主题

### 暗色色彩系统

```kotlin
// 暗色主题色
val DarkPrimary = Color(0xFF818CF8)
val DarkSecondary = Color(0xFFA78BFA)
val DarkBackground = Color(0xFF0F172A)
val DarkSurface = Color(0xFF1E293B)
val DarkSurfaceVariant = Color(0xFF334155)

// 暗色文本
val DarkTextPrimary = Color(0xFFF1F5F9)
val DarkTextSecondary = Color(0xFFCBD5E1)
val DarkTextTertiary = Color(0xFF94A3B8)
```

### 暗色主题适配原则

- 降低背景亮度，提升前景对比度
- 使用更柔和的阴影效果
- 调整渐变色的饱和度
- 确保所有文本在暗色背景上清晰可读

---

## 无障碍设计

### 1. 内容描述

所有交互元素必须提供contentDescription：

```kotlin
Icon(
    imageVector = Icons.Default.VolumeUp,
    contentDescription = "播放单词发音",
    modifier = Modifier.clickable { /* ... */ }
)
```

### 2. 最小触摸目标

所有可点击元素最小尺寸为48.dp × 48.dp

### 3. 颜色对比度

- 正文文本对比度 ≥ 4.5:1
- 大号文本对比度 ≥ 3:1
- 交互元素对比度 ≥ 3:1

### 4. 语义化标签

使用semantics修饰符提供额外的无障碍信息：

```kotlin
Text(
    text = "5/20",
    modifier = Modifier.semantics {
        contentDescription = "已学习5个单词，共20个单词"
    }
)
```

---

## 性能优化

### 1. 图片加载

- 使用Coil库异步加载图片
- 提供占位符和错误图片
- 根据显示尺寸缩放图片

### 2. 列表优化

- 使用LazyColumn/LazyRow
- 实现key参数确保正确的重组
- 避免在item中创建新的lambda

### 3. 状态管理

- 使用remember缓存计算结果
- 使用derivedStateOf避免不必要的重组
- 合理使用Modifier.drawBehind减少重组

---

## 实现优先级

### Phase 1 - 核心UI框架
1. 主题系统 (Color.kt, Theme.kt, Type.kt)
2. 通用组件库 (AppButton, AppCard, AppTopBar)
3. 主页面布局

### Phase 2 - 单词学习模块
1. VocabularyScreen布局
2. WordCard组件及翻转动画
3. 进度指示器

### Phase 3 - 口语训练模块
1. SpeakingScreen布局
2. ChatBubble组件
3. 录音按钮及动画

### Phase 4 - 写作练习模块
1. WritingScreen布局
2. 文本编辑器
3. 语法错误标注
4. 错误详情弹窗

### Phase 5 - 优化与完善
1. 暗色主题
2. 响应式布局
3. 无障碍优化
4. 性能优化

---

## 参考资料

- Material Design 3: https://m3.material.io/
- Jetpack Compose Guidelines: https://developer.android.com/jetpack/compose
- Android Accessibility: https://developer.android.com/guide/topics/ui/accessibility
