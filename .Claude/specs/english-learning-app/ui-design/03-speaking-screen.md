# AI对话训练页面设计 (SpeakingScreen)

## 设计思路

**白色主题设计**：顶部为侧边栏按钮、对话标题、电话按钮、更多选项按钮。中间是对话消息列表区域，显示AI和用户的对话气泡，支持滚动查看历史消息。底部的输入区域与单词页面保持一致，包含打开相机、文本输入框、文本上传按钮、发送/停止按钮，用于发送文本消息或语音消息与AI进行口语练习。最底部是导航栏组件，包含4个按钮：单词、写作、对话、设置。

---

## 布局结构
```
┌─────────────────────────────────┐
│  顶部栏 (白色)                    │
│  ☰  对话  ☎  ⋮                 │
├─────────────────────────────────┤
│                                 │
│  对话列表区域 (可滚动, 浅灰背景)   │
│  ┌─────────────────────────┐   │
│  │ AI: Hello! Let's...     │   │
│  └─────────────────────────┘   │
│         ┌─────────────────┐    │
│         │ User: Hi there! │    │
│         └─────────────────┘    │
│  ┌─────────────────────────┐   │
│  │ AI: Great! Tell me...   │   │
│  └─────────────────────────┘   │
│                                 │
├─────────────────────────────────┤
│  底部输入区域 (白色)              │
│  📷  [输入框: 发消息或按住说话]  │
│      ➕  ⏺                      │
├─────────────────────────────────┤
│  底部导航栏 (白色)                │
│  单词  写作  对话  设置          │
└─────────────────────────────────┘
```

---

## 设计细节

### 顶部栏
- 高度: 64.dp
- 背景: 白色 (#FFFFFF)
- 布局: 水平排列，4个按钮
- 按钮从左到右:
  1. 侧边栏按钮 (☰): 打开侧边菜单
  2. 标题 "对话": HeadlineMedium, 深色文字 (#1E293B)
  3. 电话按钮 (☎): 语音通话模式
  4. 更多选项按钮 (⋮): 显示更多功能菜单
- 按钮尺寸: 48.dp × 48.dp
- 图标: 24.dp, 深灰色 (#64748B)
- 底部分隔线: 1.dp, 浅灰 (#E2E8F0)

### 对话列表区域
- 背景: 浅灰色 (#F8FAFC)
- 内边距: 16.dp
- 可滚动: 垂直滚动
- 自动滚动到最新消息
- 消息间距: 12.dp

### 对话气泡 - AI消息
- 最大宽度: 屏幕宽度的75%
- 对齐: 左对齐
- 圆角: 左上16.dp, 右上16.dp, 右下16.dp, 左下4.dp
- 背景: 白色 (#FFFFFF)
- 边框: 1.dp, 浅灰 (#E2E8F0)
- 阴影: elevation = 2.dp
- 内边距: 水平16.dp, 垂直12.dp
- 文本: BodyLarge (16.sp), 深色 (#1E293B)
- 头像: 32.dp圆形, 主题色背景 (#8B5CF6), 白色AI图标
- 边距: 左侧8.dp (头像后), 右侧48.dp
- 时间戳: BodySmall, 灰色 (#94A3B8), 气泡下方

### 对话气泡 - 用户消息
- 最大宽度: 屏幕宽度的75%
- 对齐: 右对齐
- 圆角: 左上16.dp, 右上16.dp, 右下4.dp, 左下16.dp
- 背景: 主题色渐变 (#6366F1 → #8B5CF6)
- 文本: BodyLarge (16.sp), 白色
- 内边距: 水平16.dp, 垂直12.dp
- 边距: 左侧48.dp, 右侧8.dp
- 时间戳: BodySmall, 灰色 (#94A3B8), 气泡下方

### 加载状态气泡
- 样式: 与AI消息气泡相同
- 内容: "AI正在思考..."
- 动画: 三个点跳动动画
- 显示时机: AI回复生成中

### 底部输入区域（与单词页面保持一致）
- 高度: 56.dp
- 背景: 白色 (#FFFFFF)
- 顶部分隔线: 1.dp, 浅灰 (#E2E8F0)
- 内边距: 水平12.dp
- 布局: 水平排列
- 组件从左到右:
  1. **相机按钮** (📷)
     - 尺寸: 40.dp 圆形
     - 图标颜色: 深灰 (#64748B)
     - 功能: 打开相机拍照发送图片
  2. **文本输入框**
     - 占据剩余空间
     - 圆角: 20.dp
     - 背景: 浅灰 (#F1F5F9)
     - 提示文字: "发消息或按住说话..."
     - 文本: BodyMedium, 深色 (#1E293B)
     - 内边距: 水平16.dp, 垂直12.dp
     - 支持多行输入
     - 最大行数: 4行
  3. **上传按钮** (➕)
     - 尺寸: 40.dp 圆形
     - 图标颜色: 深灰 (#64748B)
     - 功能: 上传文件/图片
  4. **发送/停止按钮** (⏺)
     - 尺寸: 40.dp 圆形
     - 默认: 发送按钮（灰色 #94A3B8）
     - 输入时: 发送按钮（主题色 #8B5CF6）
     - 语音输入时: 停止按钮（红色 #EF4444）

### 底部导航栏
- 高度: 72.dp
- 背景: 白色 (#FFFFFF)
- 顶部分隔线: 1.dp, 浅灰 (#E2E8F0)
- 4个导航按钮，均匀分布
- 按钮样式:
  - 图标: 24.dp
  - 文字: LabelMedium
  - 未选中: 灰色 (#94A3B8)
  - 选中: 主题色 (#8B5CF6)
  - 内边距: 垂直12.dp
- 导航项:
  1. 单词 (Vocabulary)
  2. 写作 (Writing)
  3. 对话 (Speaking) - 当前选中
  4. 设置 (Settings)

---

## 交互流程

### 对话流程
1. 用户进入口语训练页面
2. AI发送欢迎消息，提出话题
3. 用户可以:
   - 在输入框输入文本消息
   - 按住输入框进行语音输入
   - 点击相机按钮发送图片
   - 点击上传按钮发送文件
4. 发送消息后，显示加载状态
5. AI回复消息，继续对话
6. 对话历史自动保存

### 语音输入
- 长按输入框开始录音
- 录音时显示波形动画
- 松开发送语音消息
- 自动转换为文字显示在气泡中
- 支持语音识别纠错

### 文本输入
- 点击输入框弹出键盘
- 输入文本后发送按钮变为主题色
- 点击发送按钮发送消息
- 支持多行文本输入
- 支持表情符号

---

## 动画规范

### 消息进入动画
```kotlin
// 新消息淡入 + 向上滑动
val offsetY by animateFloatAsState(
    targetValue = 0f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
)

val alpha by animateFloatAsState(
    targetValue = 1f,
    animationSpec = tween(300)
)
```

### 加载动画
```kotlin
// 三个点跳动动画
val dotOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = -8f,
    animationSpec = infiniteRepeatable(
        animation = tween(600, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )
)
```

### 录音按钮脉冲动画
```kotlin
// 录音时按钮缩放动画
val scale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
        animation = tween(1000, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )
)
```

### 发送按钮动画
```kotlin
// 点击发送时的缩放动画
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.9f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy
    )
)
```
