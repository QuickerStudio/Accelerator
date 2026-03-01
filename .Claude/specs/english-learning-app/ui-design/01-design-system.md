# 设计系统基础

## 设计概述

基于参考截图的设计风格，本应用采用现代化的Material Design 3设计语言，具有以下核心特征：

### 设计原则
- **简洁明快**：清晰的视觉层次，减少视觉噪音
- **卡片式布局**：使用圆角卡片组织内容模块
- **渐变色彩**：使用柔和的渐变背景增强视觉吸引力
- **大字体层级**：清晰的标题和内容对比
- **充足留白**：保持界面呼吸感

---

## 色彩系统

### 主色调
```kotlin
// 主题色 - 蓝紫色系
val Primary = Color(0xFF6366F1)        // 主色 - 靛蓝
val PrimaryVariant = Color(0xFF4F46E5) // 主色变体
val Secondary = Color(0xFF8B5CF6)      // 辅助色 - 紫色
val SecondaryVariant = Color(0xFF7C3AED)

// 渐变色
val GradientStart = Color(0xFF6366F1)
val GradientEnd = Color(0xFF8B5CF6)
```

### 背景色
```kotlin
val Background = Color(0xFFF8FAFC)     // 浅灰背景
val Surface = Color(0xFFFFFFFF)        // 卡片表面
val SurfaceVariant = Color(0xFFF1F5F9) // 次级表面
```

### 文本色
```kotlin
val TextPrimary = Color(0xFF1E293B)    // 主文本 - 深灰
val TextSecondary = Color(0xFF64748B)  // 次要文本 - 中灰
val TextTertiary = Color(0xFF94A3B8)   // 三级文本 - 浅灰
val TextOnPrimary = Color(0xFFFFFFFF)  // 主色上的文本
```

### 功能色
```kotlin
val Success = Color(0xFF10B981)        // 成功 - 绿色
val Warning = Color(0xFFF59E0B)        // 警告 - 橙色
val Error = Color(0xFFEF4444)          // 错误 - 红色
val Info = Color(0xFF3B82F6)           // 信息 - 蓝色
```

---

## 字体系统

```kotlin
// 标题字体
val HeadlineLarge = TextStyle(
    fontSize = 32.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 40.sp,
    letterSpacing = 0.sp
)

val HeadlineMedium = TextStyle(
    fontSize = 28.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 36.sp,
    letterSpacing = 0.sp
)

val HeadlineSmall = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 32.sp,
    letterSpacing = 0.sp
)

// 正文字体
val BodyLarge = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)

val BodyMedium = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 20.sp,
    letterSpacing = 0.25.sp
)

val BodySmall = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp
)

// 标签字体
val LabelLarge = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
)

val LabelMedium = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
)
```

---

## 间距系统

```kotlin
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}
```

---

## 圆角系统

```kotlin
object CornerRadius {
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val xlarge = 24.dp
    val full = 9999.dp
}
```

---

## 设计资源

### 图标库
- Material Icons Extended
- 自定义SVG图标 (如需要)

### 字体
- 系统默认字体 (Roboto on Android)
- 可选: Noto Sans CJK (中文优化)

### 插图
- 空状态插图
- 错误状态插图
- 成功状态插图
