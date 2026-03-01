# 界面风格与主题色设计 (Theme Styles)

## 设计思路

应用提供4种精选主题风格，让用户可以根据个人喜好和使用场景选择合适的界面外观。每种主题都经过精心设计，确保良好的视觉体验和可读性。用户可以在设置页面选择主题，应用会立即应用新的配色方案。

---

## 主题方案

### 1. 白色主题（Light）
**适用场景**: 白天使用，光线充足环境

**配色方案**:
- **背景色**: #F8FAFC (浅灰白)
- **表面色**: #FFFFFF (纯白)
- **卡片色**: #FFFFFF (纯白)
- **主题色**: #6366F1 (靛蓝)
- **主题色渐变**: #6366F1 → #8B5CF6
- **文本主色**: #1E293B (深灰)
- **文本次色**: #64748B (中灰)
- **文本三级**: #94A3B8 (浅灰)
- **分隔线**: #E2E8F0 (浅灰)
- **成功色**: #10B981 (绿色)
- **警告色**: #F59E0B (橙色)
- **错误色**: #EF4444 (红色)

**特点**:
- 明亮清晰，适合阅读
- 经典配色，专业稳重
- 对比度高，视觉舒适

---

### 2. 暗色主题（Dark）
**适用场景**: 夜间使用，低光环境，AMOLED屏幕省电

**配色方案**:
- **背景色**: #0F172A (深蓝黑)
- **表面色**: #1E293B (深灰蓝)
- **卡片色**: #334155 (灰蓝)
- **主题色**: #818CF8 (浅靛蓝)
- **主题色渐变**: #818CF8 → #A78BFA
- **文本主色**: #F1F5F9 (浅灰白)
- **文本次色**: #CBD5E1 (中灰)
- **文本三级**: #94A3B8 (浅灰)
- **分隔线**: #475569 (深灰)
- **成功色**: #34D399 (浅绿)
- **警告色**: #FBBF24 (浅橙)
- **错误色**: #F87171 (浅红)

**特点**:
- 护眼舒适，减少蓝光
- AMOLED屏幕省电
- 夜间使用不刺眼

---

### 3. 苹果绿主题（Apple Green）
**适用场景**: 清新自然，护眼舒适，适合长时间学习

**配色方案**:
- **背景色**: #F0FDF4 (浅绿白)
- **表面色**: #FFFFFF (纯白)
- **卡片色**: #DCFCE7 (浅绿)
- **主题色**: #10B981 (翡翠绿)
- **主题色渐变**: #10B981 → #14B8A6
- **文本主色**: #064E3B (深绿)
- **文本次色**: #047857 (中绿)
- **文本三级**: #059669 (浅绿)
- **分隔线**: #BBF7D0 (浅绿)
- **成功色**: #10B981 (绿色)
- **警告色**: #F59E0B (橙色)
- **错误色**: #EF4444 (红色)

**特点**:
- 清新自然，活力健康
- 绿色护眼，减少视觉疲劳
- 适合长时间学习使用

**暗色模式**（可选）:
- **背景色**: #022C22 (深绿黑)
- **表面色**: #064E3B (深绿)
- **卡片色**: #065F46 (中绿)
- **主题色**: #34D399 (浅绿)
- **主题色渐变**: #34D399 → #6EE7B7
- **文本主色**: #ECFDF5 (浅绿白)
- **文本次色**: #A7F3D0 (浅绿)

---

### 4. 亮紫主题（Bright Purple）
**适用场景**: 优雅时尚，个性鲜明，艺术气息

**配色方案**:
- **背景色**: #FAF5FF (浅紫白)
- **表面色**: #FFFFFF (纯白)
- **卡片色**: #F3E8FF (浅紫)
- **主题色**: #A855F7 (亮紫)
- **主题色渐变**: #A855F7 → #C084FC
- **文本主色**: #581C87 (深紫)
- **文本次色**: #7C3AED (中紫)
- **文本三级**: #9333EA (浅紫)
- **分隔线**: #E9D5FF (浅紫)
- **成功色**: #10B981 (绿色)
- **警告色**: #F59E0B (橙色)
- **错误色**: #EF4444 (红色)

**特点**:
- 优雅神秘，艺术气息
- 个性鲜明，时尚前卫
- 视觉冲击力强

**暗色模式**（可选）:
- **背景色**: #2E1065 (深紫黑)
- **表面色**: #581C87 (深紫)
- **卡片色**: #6B21A8 (中紫)
- **主题色**: #C084FC (浅紫)
- **主题色渐变**: #C084FC → #E9D5FF
- **文本主色**: #FAF5FF (浅紫白)
- **文本次色**: #E9D5FF (浅紫)

---

## 主题选择器界面

### 布局结构
```
┌─────────────────────────────────┐
│  主题选择                        │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━  │
│                                 │
│  ┌─────────┐  ┌─────────┐     │
│  │         │  │         │     │
│  │  白色   │  │  暗色   │     │
│  │    ✓    │  │         │     │
│  └─────────┘  └─────────┘     │
│                                 │
│  ┌─────────┐  ┌─────────┐     │
│  │         │  │         │     │
│  │苹果绿   │  │  亮紫   │     │
│  │         │  │         │     │
│  └─────────┘  └─────────┘     │
│                                 │
│  预览                           │
│  ┌─────────────────────────┐   │
│  │ [预览卡片]               │   │
│  │ 这是主题预览              │   │
│  └─────────────────────────┘   │
│                                 │
│  [应用]  [取消]                 │
└─────────────────────────────────┘
```

### 设计细节

**底部弹窗容器**
- 类型: BottomSheet
- 圆角: 顶部24.dp
- 背景: 根据当前主题
- 内边距: 24.dp
- 最大高度: 屏幕高度的70%

**标题**
- 文本: "主题选择"
- 字体: HeadlineMedium (20.sp), 粗体
- 颜色: 主文本色
- 底部间距: 16.dp

**分隔线**
- 高度: 2.dp
- 颜色: 主题色
- 宽度: 60.dp
- 圆角: 1.dp
- 底部间距: 24.dp

**主题卡片**
- 布局: 2列网格
- 卡片尺寸: 宽度 = (容器宽度 - 间距) / 2, 高度 = 120.dp
- 圆角: CornerRadius.large (16.dp)
- 背景: 主题色渐变
- 阴影: elevation = 4.dp
- 边框: 3.dp, 选中时显示
- 边框颜色: 主题色
- 卡片间距: 16.dp
- 行间距: 16.dp

**主题卡片内容**
- 布局: 垂直居中
- 主题名称: HeadlineMedium (18.sp), 白色, 居中
- 选中标记: ✓, 32.dp, 白色, 居中, 名称下方8.dp
- 内边距: 16.dp

**主题卡片样式**:
1. **白色主题卡片**:
   - 背景渐变: #6366F1 → #8B5CF6
   - 文本: "白色", 白色

2. **暗色主题卡片**:
   - 背景渐变: #1E293B → #334155
   - 文本: "暗色", 白色

3. **苹果绿主题卡片**:
   - 背景渐变: #10B981 → #14B8A6
   - 文本: "苹果绿", 白色

4. **亮紫主题卡片**:
   - 背景渐变: #A855F7 → #C084FC
   - 文本: "亮紫", 白色

**预览区域**
- 标题: "预览", BodyLarge (16.sp), 粗体
- 顶部间距: 24.dp
- 底部间距: 16.dp
- 预览卡片:
  - 宽度: match_parent
  - 高度: 100.dp
  - 圆角: CornerRadius.large (16.dp)
  - 背景: 根据选中主题显示
  - 内边距: 20.dp
  - 文本: "这是主题预览", HeadlineMedium, 根据主题调整颜色
  - 实时更新: 选择主题后立即更新

**操作按钮**
- 布局: 水平排列，右对齐
- 顶部间距: 24.dp
- 按钮间距: 12.dp
- **取消按钮**:
  - 宽度: wrap_content
  - 高度: 48.dp
  - 圆角: CornerRadius.medium (12.dp)
  - 背景: 透明
  - 文本: "取消", LabelLarge, 次文本色
  - 内边距: 水平24.dp
- **应用按钮**:
  - 宽度: wrap_content
  - 高度: 48.dp
  - 圆角: CornerRadius.medium (12.dp)
  - 背景: 主题色
  - 文本: "应用", LabelLarge, 白色
  - 内边距: 水平24.dp

---

## 主题切换动画

### 颜色过渡动画
```kotlin
// 主题色过渡
val animatedColor by animateColorAsState(
    targetValue = targetThemeColor,
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
)
```

### 背景色过渡
```kotlin
// 背景色过渡
val animatedBackground by animateColorAsState(
    targetValue = themeBackground,
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
)
```

### 淡入淡出动画
```kotlin
// 内容淡入淡出
val alpha by animateFloatAsState(
    targetValue = if (themeChanged) 0f else 1f,
    animationSpec = tween(
        durationMillis = 150,
        easing = LinearEasing
    )
)

LaunchedEffect(themeChanged) {
    if (themeChanged) {
        delay(150)
        // 应用新主题
        applyNewTheme()
        // 淡入
        alpha = 1f
    }
}
```

---

## 主题持久化

### 主题数据模型
```kotlin
enum class AppTheme {
    LIGHT,        // 白色主题
    DARK,         // 暗色主题
    APPLE_GREEN,  // 苹果绿主题
    BRIGHT_PURPLE // 亮紫主题
}

data class ThemeSettings(
    val theme: AppTheme = AppTheme.LIGHT,
    val timestamp: Long = System.currentTimeMillis()
)
```

### 使用 DataStore 存储
```kotlin
// 保存主题设置
suspend fun saveTheme(theme: AppTheme) {
    dataStore.edit { preferences ->
        preferences[THEME_KEY] = theme.name
        preferences[THEME_TIMESTAMP_KEY] = System.currentTimeMillis()
    }
}

// 读取主题设置
val themeFlow: Flow<AppTheme> = dataStore.data.map { preferences ->
    val themeName = preferences[THEME_KEY] ?: AppTheme.LIGHT.name
    AppTheme.valueOf(themeName)
}
```

### 主题应用
```kotlin
@Composable
fun AppTheme(
    theme: AppTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppTheme.LIGHT -> lightColorScheme(
            primary = Color(0xFF6366F1),
            background = Color(0xFFF8FAFC),
            surface = Color(0xFFFFFFFF),
            // ... 其他颜色
        )
        AppTheme.DARK -> darkColorScheme(
            primary = Color(0xFF818CF8),
            background = Color(0xFF0F172A),
            surface = Color(0xFF1E293B),
            // ... 其他颜色
        )
        AppTheme.APPLE_GREEN -> lightColorScheme(
            primary = Color(0xFF10B981),
            background = Color(0xFFF0FDF4),
            surface = Color(0xFFFFFFFF),
            // ... 其他颜色
        )
        AppTheme.BRIGHT_PURPLE -> lightColorScheme(
            primary = Color(0xFFA855F7),
            background = Color(0xFFFAF5FF),
            surface = Color(0xFFFFFFFF),
            // ... 其他颜色
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
```

---

## 无障碍支持

### 颜色对比度
- 所有主题的文本对比度 ≥ 4.5:1
- 大号文本对比度 ≥ 3:1
- 交互元素对比度 ≥ 3:1

### 色盲友好
- 提供非颜色的视觉提示（图标、文字）
- 避免仅用颜色传达信息
- 测试红绿色盲、蓝黄色盲模式

### 高对比度模式
- 支持系统高对比度设置
- 增强边框和分隔线
- 提高文本对比度

---

## 性能优化

### 主题切换优化
- 使用 remember 缓存颜色值
- 避免不必要的重组
- 使用 derivedStateOf 计算派生颜色

### 颜色资源管理
- 预定义所有主题颜色
- 避免运行时计算颜色
- 使用 CompositionLocal 传递主题

---

## 实现优先级

### Phase 1 - 基础主题
1. 白色主题（默认）
2. 暗色主题
3. 主题选择器界面
4. 主题持久化

### Phase 2 - 扩展主题
1. 苹果绿主题
2. 亮紫主题
3. 主题切换动画
4. 预览功能

### Phase 3 - 优化
1. 性能优化
2. 无障碍支持
3. 主题切换体验优化

### 设计细节

**底部弹窗容器**
- 类型: BottomSheet
- 圆角: 顶部24.dp
- 背景: 根据当前模式
- 内边距: 24.dp
- 最大高度: 屏幕高度的80%

**标题**
- 文本: "主题色选择"
- 字体: HeadlineMedium (20.sp), 粗体
- 颜色: 主文本色
- 底部间距: 16.dp

**分隔线**
- 高度: 2.dp
- 颜色: 主题色
- 宽度: 60.dp
- 圆角: 1.dp
- 底部间距: 24.dp

**预设主题色区域**
- 标题: "预设主题色", BodyLarge (16.sp), 粗体
- 底部间距: 16.dp
- 布局: 4列网格
- 行间距: 16.dp
- 列间距: 16.dp

**主题色块**
- 尺寸: 64.dp × 64.dp
- 圆角: CornerRadius.medium (12.dp)
- 背景: 主题色渐变
- 边框: 2.dp, 选中时显示
- 边框颜色: 主题色
- 阴影: elevation = 4.dp
- 选中标记: ✓, 24.dp, 白色, 居中
- 标签: BodySmall (12.sp), 次文本色, 居中, 顶部间距4.dp

**界面模式区域**
- 标题: "界面模式", BodyLarge (16.sp), 粗体
- 顶部间距: 24.dp
- 底部间距: 16.dp
- 布局: 垂直列表

**模式选项**
- 高度: 48.dp
- 布局: 水平排列
- 单选按钮: 20.dp, 左侧
- 文本: BodyMedium (14.sp), 主文本色
- 间距: 12.dp
- 点击区域: 整行

**预览区域**
- 标题: "预览", BodyLarge (16.sp), 粗体
- 顶部间距: 24.dp
- 底部间距: 16.dp
- 预览卡片:
  - 宽度: match_parent
  - 高度: 120.dp
  - 圆角: CornerRadius.large (16.dp)
  - 背景: 主题色渐变
  - 内边距: 20.dp
  - 文本: "这是主题色预览", HeadlineMedium, 白色
  - 实时更新: 选择主题色后立即更新

**操作按钮**
- 布局: 水平排列，右对齐
- 顶部间距: 24.dp
- 按钮间距: 12.dp
- **取消按钮**:
  - 宽度: wrap_content
  - 高度: 48.dp
  - 圆角: CornerRadius.medium (12.dp)
  - 背景: 透明
  - 文本: "取消", LabelLarge, 次文本色
  - 内边距: 水平24.dp
- **应用按钮**:
  - 宽度: wrap_content
  - 高度: 48.dp
  - 圆角: CornerRadius.medium (12.dp)
  - 背景: 主题色
  - 文本: "应用", LabelLarge, 白色
  - 内边距: 水平24.dp

---

## 动态取色实现（Android 12+）

### Material You 动态配色
```kotlin
// 检查系统版本
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    // 使用动态配色
    val dynamicColorScheme = if (isDarkTheme) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }
    
    MaterialTheme(
        colorScheme = dynamicColorScheme,
        content = content
    )
} else {
    // 使用预设主题色
    MaterialTheme(
        colorScheme = customColorScheme,
        content = content
    )
}
```

### 壁纸取色算法
- 从系统壁纸提取主色调
- 生成协调的配色方案
- 自动适配浅色/深色模式
- 确保文本可读性

---

## 主题切换动画

### 颜色过渡动画
```kotlin
// 主题色过渡
val animatedColor by animateColorAsState(
    targetValue = targetThemeColor,
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
)
```

### 模式切换动画
```kotlin
// 浅色/深色模式切换
val animatedBackground by animateColorAsState(
    targetValue = if (isDarkMode) DarkBackground else LightBackground,
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
)
```

### 圆形展开动画（可选）
```kotlin
// 从点击位置展开新主题
val circularReveal = remember {
    Animatable(0f)
}

LaunchedEffect(themeChanged) {
    circularReveal.animateTo(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        )
    )
}
```

---

## 主题持久化

### 保存主题设置
```kotlin
data class ThemeSettings(
    val themeColor: ThemeColor,
    val themeMode: ThemeMode,
    val useDynamicColor: Boolean = false
)

enum class ThemeColor {
    DEFAULT,      // 默认靛蓝紫
    OCEAN_BLUE,   // 海洋蓝
    EMERALD,      // 翡翠绿
    SUNSET,       // 日落橙
    SAKURA,       // 樱花粉
    LAVENDER,     // 薰衣草紫
    SPACE_GRAY,   // 深空灰
    RUBY,         // 宝石红
    FOREST,       // 森林绿
    ROYAL_BLUE,   // 皇家蓝
    DYNAMIC       // 动态取色
}

enum class ThemeMode {
    LIGHT,        // 浅色模式
    DARK,         // 深色模式
    AMOLED_BLACK, // 纯黑模式
    AUTO          // 自动模式
}
```

### 使用 DataStore 存储
```kotlin
// 保存主题设置
suspend fun saveThemeSettings(settings: ThemeSettings) {
    dataStore.edit { preferences ->
        preferences[THEME_COLOR_KEY] = settings.themeColor.name
        preferences[THEME_MODE_KEY] = settings.themeMode.name
        preferences[USE_DYNAMIC_COLOR_KEY] = settings.useDynamicColor
    }
}

// 读取主题设置
val themeSettingsFlow: Flow<ThemeSettings> = dataStore.data.map { preferences ->
    ThemeSettings(
        themeColor = ThemeColor.valueOf(
            preferences[THEME_COLOR_KEY] ?: ThemeColor.DEFAULT.name
        ),
        themeMode = ThemeMode.valueOf(
            preferences[THEME_MODE_KEY] ?: ThemeMode.AUTO.name
        ),
        useDynamicColor = preferences[USE_DYNAMIC_COLOR_KEY] ?: false
    )
}
```

---

## 无障碍支持

### 颜色对比度
- 确保所有主题色的文本对比度 ≥ 4.5:1
- 大号文本对比度 ≥ 3:1
- 交互元素对比度 ≥ 3:1

### 色盲友好
- 提供非颜色的视觉提示（图标、文字）
- 避免仅用颜色传达信息
- 测试红绿色盲、蓝黄色盲模式

### 高对比度模式
- 支持系统高对比度设置
- 增强边框和分隔线
- 提高文本对比度

---

## 性能优化

### 主题切换优化
- 使用 remember 缓存颜色值
- 避免不必要的重组
- 使用 derivedStateOf 计算派生颜色

### 动态取色优化
- 缓存壁纸提取的颜色
- 仅在壁纸变化时重新提取
- 异步处理颜色提取

---

## 实现优先级

### Phase 1 - 基础主题
1. 默认主题色
2. 浅色/深色模式切换
3. 主题色选择器界面

### Phase 2 - 扩展主题
1. 10种预设主题色
2. 纯黑模式（AMOLED）
3. 自动模式

### Phase 3 - 高级功能
1. 动态取色（Android 12+）
2. 主题切换动画
3. 高对比度模式

### Phase 4 - 优化
1. 性能优化
2. 无障碍支持
3. 用户自定义主题色
