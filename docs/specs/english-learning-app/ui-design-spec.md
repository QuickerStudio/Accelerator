# 英语学习应用 UI 设计规范

## 文档概述

本文档整合了英语学习应用的完整 UI 设计规范，包括设计系统、页面设计、组件库、动画规范、主题系统等内容。

**版本**: 1.0  
**最后更新**: 2026-03-01  
**设计语言**: Material Design 3

---

## 目录

1. [设计系统基础](#1-设计系统基础)
2. [页面设计](#2-页面设计)
   - 2.1 [单词学习页面](#21-单词学习页面)
   - 2.2 [AI口语训练页面](#22-ai口语训练页面)
   - 2.3 [写作练习页面](#23-写作练习页面)
   - 2.4 [设置页面](#24-设置页面)
   - 2.5 [侧边栏](#25-侧边栏)
3. [通用组件库](#3-通用组件库)
4. [动画与交互规范](#4-动画与交互规范)
5. [主题系统](#5-主题系统)
6. [设置页面导航](#6-设置页面导航)
7. [无障碍设计](#7-无障碍设计)
8. [实现优先级](#8-实现优先级)

---

## 1. 设计系统基础

### 1.1 设计原则

- **简洁明快**: 清晰的视觉层次，减少视觉噪音
- **卡片式布局**: 使用圆角卡片组织内容模块
- **渐变色彩**: 使用柔和的渐变背景增强视觉吸引力
- **大字体层级**: 清晰的标题和内容对比
- **充足留白**: 保持界面呼吸感

### 1.2 色彩系统

#### 主色调
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

#### 背景色
```kotlin
val Background = Color(0xFFF8FAFC)     // 浅灰背景
val Surface = Color(0xFFFFFFFF)        // 卡片表面
val SurfaceVariant = Color(0xFFF1F5F9) // 次级表面
```

#### 文本色
```kotlin
val TextPrimary = Color(0xFF1E293B)    // 主文本 - 深灰
val TextSecondary = Color(0xFF64748B)  // 次要文本 - 中灰
val TextTertiary = Color(0xFF94A3B8)   // 三级文本 - 浅灰
val TextOnPrimary = Color(0xFFFFFFFF)  // 主色上的文本
```

#### 功能色
```kotlin
val Success = Color(0xFF10B981)        // 成功 - 绿色
val Warning = Color(0xFFF59E0B)        // 警告 - 橙色
val Error = Color(0xFFEF4444)          // 错误 - 红色
val Info = Color(0xFF3B82F6)           // 信息 - 蓝色
```

### 1.3 字体系统

```kotlin
// 标题字体
val HeadlineLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp)
val HeadlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp)
val HeadlineSmall = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, lineHeight = 32.sp)

// 正文字体
val BodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp)
val BodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp)
val BodySmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, lineHeight = 16.sp)

// 标签字体
val LabelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
val LabelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp)
```

### 1.4 间距系统

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

### 1.5 圆角系统

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

## 2. 页面设计

### 2.1 单词学习页面 (VocabularyScreen)

#### 设计思路
顶部为侧边栏按钮、单词标题、对话按钮、单词本按钮。中间是可以左右滑动的单词卡片，左滑动是未记住，右滑动是已经记住，双击单词卡片语音播报读单词发音，长按单词卡片是收藏到单词本中。底部的文本框分别是打开相机、文本输入框、文本上传按钮、发送/停止按钮。最底部是导航栏组件。

#### 布局结构
- 顶部栏 (64.dp): 侧边栏、标题、对话、单词本按钮
- 单词卡片区域: 可左右滑动，宽度 = 屏幕宽度 - 48.dp，高度 = 400.dp
- 底部输入区域 (56.dp): 相机、输入框、上传、发送按钮
- 底部导航栏 (72.dp): 单词、写作、对话、设置

#### 单词卡片交互
- **左滑**: 标记"未记住"，滑动距离 > 30%触发
- **右滑**: 标记"已记住"，滑动距离 > 30%触发
- **双击**: 语音播报单词发音
- **长按**: 收藏到单词本 (500ms)

### 2.2 AI口语训练页面 (SpeakingScreen)

#### 设计思路
顶部为侧边栏按钮、对话标题、电话按钮、更多选项按钮。中间是对话消息列表区域，显示AI和用户的对话气泡，支持滚动查看历史消息。底部的输入区域包含打开相机、文本输入框、文本上传按钮、发送/停止按钮。

#### 对话气泡设计
- **AI消息**: 左对齐，深色卡片背景，圆角(16,16,16,4)，最大宽度75%
- **用户消息**: 右对齐，主题色渐变背景，圆角(16,16,4,16)，最大宽度75%
- **加载状态**: 三个点跳动动画

#### 交互功能
- 文本输入: 支持多行，最大4行
- 语音输入: 长按录音，松开发送
- 图片发送: 相机拍照或上传文件
- 自动滚动: 新消息自动滚动到底部

### 2.3 写作练习页面 (WritingScreen)

#### 设计思路
顶部为侧边栏按钮、写作标题、保存按钮、语法评分、错误数。中间是全屏文本编辑区域，用户可以输入英文文章，系统会自动标注语法错误。点击错误标注可以查看详细说明和修改建议。

#### 语法评分算法
- 评分范围: 0-100分
- 计算因素:
  - 语法错误数量 (40%)
  - 拼写错误数量 (20%)
  - 句子结构复杂度 (20%)
  - 词汇丰富度 (20%)
- 显示颜色:
  - 90-100分: 绿色
  - 70-89分: 黄色
  - 0-69分: 红色

#### 错误标注
- 样式: 波浪下划线，红色
- 点击: 显示错误详情弹窗 (BottomSheet)
- 弹窗内容: 错误类型、说明、建议修改列表

### 2.4 设置页面 (SettingsScreen)

#### 设计思路
设置页面（我的页面）是用户的个人中心，顶部显示用户头像和用户名，下方是水平滑动的选项卡，展示用户在不同模块的数据统计。中间是英语学业水平卡片，显示各项学习数据和AI生成的学习建议。底部是功能设置列表。

#### 用户信息区域
- 高度: 80.dp
- 头像: 56.dp圆形，2.dp主题色边框
- 用户名: HeadlineMedium，白色
- 可点击编辑

#### 选项卡区域
- 高度: 120.dp
- 水平滚动: 收藏、单词、写作、口语、个人简历
- 卡片尺寸: 100.dp × 100.dp
- 显示: 标题 + 数值

#### 英语学业水平卡片
- 显示数据:
  - 学习单词数
  - 写作水平
  - 口语水平
  - 语法水平
- AI学习建议: 根据评分自动生成

#### 功能设置列表
- 任务: 查看和管理学习任务
- 主题色: 选择应用主题颜色
- 朗读音色: 选择TTS朗读音色
- 模型设置: 配置AI模型参数
- 设置: 应用通用设置

### 2.5 侧边栏 (Sidebar)

#### 设计思路
侧边栏是一个从左侧滑出的抽屉式面板，通过手指向右滑动手势触发显示。主要用于笔记管理和学习日志查看。

#### 布局结构
- 宽度: 320.dp (约85%屏幕宽度)
- 高度: 全屏
- 背景: 深色 (#1E1E1E)
- 阴影: elevation = 16.dp

#### 内容区域
1. **顶部品牌区域** (64.dp)
   - Accelerator 品牌名称
   - 搜索按钮
   - 设置按钮

2. **新建笔记按钮** (56.dp)
   - 主题色渐变背景
   - ➕ 图标 + "新建笔记"文本

3. **全部笔记区域**
   - 水平滚动列表，显示5个笔记卡片
   - 卡片尺寸: 120.dp × 80.dp

4. **笔记分组区域**
   - 2行网格布局，可左右滑动
   - 分组图标: 64.dp × 64.dp
   - ➕ 添加分组按钮

5. **单词学习日志**
   - 按时间分组: 置顶、今天、本周、更早
   - 可垂直滚动查看所有日志

---

## 3. 通用组件库

### 3.1 通用按钮 (AppButton)

```kotlin
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    icon: ImageVector? = null
)

enum class ButtonVariant {
    Primary,    // 主按钮 - 渐变背景
    Secondary,  // 次要按钮 - 灰色背景
    Outline,    // 轮廓按钮 - 透明背景+边框
    Text        // 文本按钮 - 透明背景
}
```

### 3.2 卡片组件 (AppCard)

```kotlin
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    gradient: Pair<Color, Color>? = null,
    elevation: Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit
)
```

### 3.3 顶部标题栏 (AppTopBar)

```kotlin
@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
)
```

### 3.4 底部导航栏 (BottomNavigationBar)

```kotlin
@Composable
fun BottomNavigationBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
)

enum class NavigationTab {
    Vocabulary,  // 单词
    Writing,     // 写作
    Speaking,    // 对话
    Settings     // 设置
}
```

### 3.5 对话气泡 (ChatBubble)

```kotlin
@Composable
fun ChatBubble(
    message: String,
    isUser: Boolean,
    timestamp: String,
    modifier: Modifier = Modifier
)
```

---

## 4. 动画与交互规范

### 4.1 页面转场动画

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

### 4.2 卡片翻转动画

```kotlin
val rotation by animateFloatAsState(
    targetValue = if (isFlipped) 180f else 0f,
    animationSpec = tween(400, easing = FastOutSlowInEasing)
)
```

### 4.3 按钮点击动画

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

## 5. 主题系统

### 5.1 主题方案

应用提供4种精选主题风格，让用户可以根据个人喜好和使用场景选择合适的界面外观。

#### 1. 白色主题 (Light)
- **适用场景**: 白天使用，光线充足环境
- **背景色**: #F8FAFC (浅灰白)
- **主题色**: #6366F1 → #8B5CF6 (靛蓝渐变)
- **文本主色**: #1E293B (深灰)

#### 2. 暗色主题 (Dark)
- **适用场景**: 夜间使用，低光环境，AMOLED屏幕省电
- **背景色**: #0F172A (深蓝黑)
- **主题色**: #818CF8 → #A78BFA (浅靛蓝渐变)
- **文本主色**: #F1F5F9 (浅灰白)

#### 3. 苹果绿主题 (Apple Green)
- **适用场景**: 清新自然，护眼舒适，适合长时间学习
- **背景色**: #F0FDF4 (浅绿白)
- **主题色**: #10B981 → #14B8A6 (翡翠绿渐变)
- **文本主色**: #064E3B (深绿)

#### 4. 亮紫主题 (Bright Purple)
- **适用场景**: 优雅时尚，个性鲜明，艺术气息
- **背景色**: #FAF5FF (浅紫白)
- **主题色**: #A855F7 → #C084FC (亮紫渐变)
- **文本主色**: #581C87 (深紫)

### 5.2 主题选择器界面

#### 布局设计
- 类型: BottomSheet
- 圆角: 顶部24.dp
- 主题卡片: 2列网格，120.dp × 120.dp
- 预览区域: 实时显示选中主题效果
- 操作按钮: 应用、取消

#### 主题切换动画
```kotlin
// 颜色过渡动画
val animatedColor by animateColorAsState(
    targetValue = targetThemeColor,
    animationSpec = tween(300, easing = FastOutSlowInEasing)
)
```

### 5.3 主题持久化

```kotlin
enum class AppTheme {
    LIGHT,        // 白色主题
    DARK,         // 暗色主题
    APPLE_GREEN,  // 苹果绿主题
    BRIGHT_PURPLE // 亮紫主题
}

// 使用 DataStore 存储
suspend fun saveTheme(theme: AppTheme) {
    dataStore.edit { preferences ->
        preferences[THEME_KEY] = theme.name
    }
}
```

---

## 6. 设置页面导航

### 6.1 朗读音色设置 (Voice Settings)

#### 功能特性
- 当前音色显示（渐变卡片）
- 推荐音色列表（美式/英式男声女声）
- 音色试听功能
- 语速调节（0.5x - 2.0x）
- 音量调节（0% - 100%）

#### 可用音色
1. 美式女声 (Emma): 清晰自然，适合学习
2. 美式男声 (James): 沉稳专业，发音标准
3. 英式女声 (Sophie): 优雅动听，英伦口音
4. 英式男声 (Oliver): 磁性低沉，标准英音

### 6.2 模型设置 (Model Settings)

#### 功能特性
- 当前模型显示（Qwen2.5-3B，本地离线模型）
- 系统提示词编辑（5个角色选项卡）
- 模型参数调节（温度、最大长度、Top P）
- 恢复默认设置功能

#### Agent 角色系统

应用包含5个专业的 Agent 角色，每个角色有独立的系统提示词和默认参数：

1. **📚 单词学习** (VocabularyTutor)
   - 默认温度: 0.7, Max Tokens: 512
   - 任务: 解释单词、提供例句和记忆技巧

2. **🔍 语法检查** (GrammarChecker)
   - 默认温度: 0.3, Max Tokens: 1024
   - 任务: 识别语法错误、给出修改建议

3. **✍️ 作文批改** (EssayReviewer)
   - 默认温度: 0.5, Max Tokens: 2048
   - 任务: 评价作文、指出优缺点、给出改进建议

4. **💬 口语陪练** (SpeakingPartner)
   - 默认温度: 0.8, Max Tokens: 512
   - 任务: 自然对话、纠正错误、给出发音建议

5. **📊 学习规划** (LearningPlanner)
   - 默认温度: 0.5, Max Tokens: 1024
   - 任务: 分析学习状态、制定学习计划

#### Agent 自动切换机制

Agent 角色会根据用户所在页面自动切换：
- 单词页 → 单词学习助手
- 口语页 → 口语陪练
- 写作页 → 作文批改
- 设置页 → 学习规划

用户可以在模型设置页面查看和编辑每个角色的系统提示词。

#### 模型参数说明

**温度 (Temperature)**: 0.0 - 2.0
- 0.0-0.5: 精确、一致（适合语法检查）
- 0.5-1.0: 平衡（适合作文批改）
- 1.0-2.0: 创造性、多样（适合对话）

**最大长度 (Max Tokens)**: 256 - 4096
- 256-512: 短回复（单词解释、对话）
- 512-1024: 中等长度（语法检查）
- 1024-2048: 长文本（作文批改、学习规划）

**Top P (核采样)**: 0.0 - 1.0
- 0.5-0.7: 保守、稳定
- 0.8-0.9: 平衡（推荐）
- 0.9-1.0: 多样、创新

### 6.3 通用设置 (General Settings)

#### 功能分组

**学习设置**
- 学习提醒: 开关
- 每日学习目标: 设置单词数、时长、写作篇数、对话次数
- 学习计划: 设置提醒时间和日期
- 学习统计显示: 设置显示方式

**自动朗读设置**
- 每日自动朗读英语文本: 开关
- 每日自动朗读英语单词: 开关
- 每日自动阅读英语语法: 开关
- 日期时间设置: 选择每周哪些天、每日几点

**权限管理**
- 通知权限: 用于学习提醒
- 麦克风权限: 用于口语练习
- 相机权限: 用于拍照识别
- 存储权限: 用于保存数据

**其他**
- 版本号: v0.0.1
- 公司信息: QuickerStudio
- 用户协议
- 检查更新

**数据管理（可折叠）**
- 清除缓存
- 导入数据: 支持JSON/CSV/TXT格式
- 导出数据: 导出学习数据到文件

---

## 7. 无障碍设计

### 7.1 内容描述

所有交互元素必须提供 contentDescription：

```kotlin
Icon(
    imageVector = Icons.Default.VolumeUp,
    contentDescription = "播放单词发音",
    modifier = Modifier.clickable { /* ... */ }
)
```

### 7.2 最小触摸目标

所有可点击元素最小尺寸为 48.dp × 48.dp

### 7.3 颜色对比度

- 正文文本对比度 ≥ 4.5:1
- 大号文本对比度 ≥ 3:1
- 交互元素对比度 ≥ 3:1

### 7.4 语义化标签

使用 semantics 修饰符提供额外的无障碍信息：

```kotlin
Text(
    text = "5/20",
    modifier = Modifier.semantics {
        contentDescription = "已学习5个单词，共20个单词"
    }
)
```

### 7.5 色盲友好

- 提供非颜色的视觉提示（图标、文字）
- 避免仅用颜色传达信息
- 测试红绿色盲、蓝黄色盲模式

---

## 8. 实现优先级

### Phase 1 - 核心UI框架
1. 主题系统 (Color.kt, Theme.kt, Type.kt)
2. 通用组件库 (AppButton, AppCard, AppTopBar)
3. 主页面布局
4. 底部导航栏

### Phase 2 - 单词学习模块
1. VocabularyScreen 布局
2. WordCard 组件及翻转动画
3. 进度指示器
4. 滑动交互（左滑/右滑）
5. 双击发音、长按收藏功能

### Phase 3 - 口语训练模块
1. SpeakingScreen 布局
2. ChatBubble 组件
3. 录音按钮及动画
4. 消息列表滚动
5. 语音输入功能

### Phase 4 - 写作练习模块
1. WritingScreen 布局
2. 文本编辑器
3. 语法错误标注
4. 错误详情弹窗
5. 语法评分算法

### Phase 5 - 设置与个人中心
1. SettingsScreen 布局
2. 用户信息区域
3. 选项卡数据展示
4. 英语学业水平卡片
5. AI学习建议生成
6. 功能设置列表

### Phase 6 - 侧边栏
1. Sidebar 布局
2. 滑动手势交互
3. 笔记列表和分组
4. 学习日志显示
5. 搜索功能

### Phase 7 - 设置页面导航
1. 朗读音色设置
2. 模型设置（Agent 系统）
3. 通用设置
4. 权限管理
5. 数据导入导出

### Phase 8 - 主题系统
1. 4种预设主题
2. 主题选择器界面
3. 主题切换动画
4. 主题持久化

### Phase 9 - 优化与完善
1. 暗色主题适配
2. 响应式布局（平板/横屏）
3. 无障碍优化
4. 性能优化
5. 动画细节优化

---

## 9. 性能优化建议

### 9.1 图片加载
- 使用 Coil 库异步加载图片
- 提供占位符和错误图片
- 根据显示尺寸缩放图片

### 9.2 列表优化
- 使用 LazyColumn/LazyRow
- 实现 key 参数确保正确的重组
- 避免在 item 中创建新的 lambda

### 9.3 状态管理
- 使用 remember 缓存计算结果
- 使用 derivedStateOf 避免不必要的重组
- 合理使用 Modifier.drawBehind 减少重组

### 9.4 主题切换优化
- 使用 remember 缓存颜色值
- 避免不必要的重组
- 使用 CompositionLocal 传递主题

---

## 10. 参考资料

- Material Design 3: https://m3.material.io/
- Jetpack Compose Guidelines: https://developer.android.com/jetpack/compose
- Android Accessibility: https://developer.android.com/guide/topics/ui/accessibility
- Compose Animation: https://developer.android.com/jetpack/compose/animation

---

## 附录

### A. 设计资源

**图标库**
- Material Icons Extended
- 自定义 SVG 图标（如需要）

**字体**
- 系统默认字体 (Roboto on Android)
- 可选: Noto Sans CJK (中文优化)

**插图**
- 空状态插图
- 错误状态插图
- 成功状态插图

### B. 设计文件组织

```
ui-design/
├── README.md                    # 导航文档
├── 01-design-system.md          # 设计系统基础
├── 02-vocabulary-screen.md      # 单词学习页面
├── 03-speaking-screen.md        # AI口语训练页面
├── 04-writing-screen.md         # 写作练习页面
├── 05-components.md             # 通用组件库
├── 06-animations.md             # 动画与交互规范
├── 07-themes-accessibility.md   # 主题与无障碍设计
├── 08-settings-screen.md        # 设置页面
├── 09-sidebar.md                # 侧边栏
├── 10-theme-styles.md           # 界面风格与主题色
├── 11-settings-pages.md         # 设置页面导航
└── settings-pages/              # 设置子页面
    ├── README.md
    ├── voice-settings.md        # 朗读音色设置
    ├── model-settings.md        # 模型设置
    └── general-settings.md      # 通用设置
```

---

**文档版本**: 1.0  
**最后更新**: 2026-03-01  
**维护者**: 开发团队
