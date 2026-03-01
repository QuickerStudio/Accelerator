# 写作练习页面设计 (WritingScreen)

## 设计思路

顶部为侧边栏按钮、写作标题、保存按钮、更多选项按钮。中间是文本编辑区域，用户可以输入英文文章，系统会自动标注语法错误。点击错误标注可以查看详细说明和修改建议。底部的输入区域包含打开相机、文本输入框、文本上传按钮、发送/停止按钮，用于向AI询问写作建议或语法问题。最底部是导航栏组件，包含4个按钮：单词、写作、对话、设置。

---

## 布局结构
```
┌─────────────────────────────────┐
│  顶部栏                          │
│  ☰  写作  💾  语法评分:85 错误:3│
├─────────────────────────────────┤
│                                 │
│  文本编辑区域 (全屏可滚动)        │
│  ┌─────────────────────────┐   │
│  │ My Daily Routine        │   │
│  │                         │   │
│  │ I wake up at 7 AM       │   │
│  │ every day. Then I...    │   │
│  │                         │   │
│  │ [语法错误波浪线标注]      │   │
│  │                         │   │
│  │                         │   │
│  │                         │   │
│  │                         │   │
│  │                         │   │
│  └─────────────────────────┘   │
│                                 │
├─────────────────────────────────┤
│  底部输入区域                     │
│  📷  [输入框: 发消息或按住说话]  │
│      ➕  ⏺                      │
├─────────────────────────────────┤
│  底部导航栏                      │
│  单词  写作  对话  设置          │
└─────────────────────────────────┘
```

---

## 设计细节

### 顶部栏
- 高度: 64.dp
- 背景: 深色 (#1E1E1E)
- 布局: 水平排列
- 左侧按钮组:
  1. 侧边栏按钮 (☰): 打开侧边菜单
  2. 标题 "写作": HeadlineMedium, 白色
  3. 保存按钮 (💾): 保存当前文章
- 右侧统计信息:
  - 语法评分: "语法评分:85", BodyMedium, 白色
  - 错误数: "错误:3", BodyMedium, 红色 (#EF4444)
  - 分隔符: 空格
  - 动态更新: 实时显示评分和错误数
- 按钮尺寸: 48.dp × 48.dp
- 图标: 24.dp, 白色

### 文本编辑区域
- 背景: 深色 (#121212)
- 内边距: 16.dp
- 占据全部可用空间（顶部栏和底部输入区域之间）
- 可滚动: 垂直滚动
- 编辑框样式:
  - 宽度: match_parent - 32.dp
  - 高度: 填充父容器
  - 圆角: CornerRadius.large (16.dp)
  - 背景: 深色卡片 (#2A2A2A)
  - 边框: 1.dp, 深灰 (#3A3A3A)
  - 内边距: 20.dp
  - 文本: BodyLarge (16.sp), 白色
  - 行高: 28.sp
  - 提示文本: "开始写作...", 灰色 (#94A3B8)
  - 光标颜色: 主题色

### 语法错误标注
- 样式: 波浪下划线
- 颜色: Error (红色 #EF4444)
- 线宽: 2.dp
- 波浪频率: 4.dp
- 点击交互: 显示错误详情弹窗
- 悬停效果: 下划线加粗

### 错误详情弹窗
- 位置: 底部弹出 (BottomSheet)
- 圆角: 顶部圆角 24.dp
- 背景: 深色卡片 (#2A2A2A)
- 内边距: 24.dp
- 内容布局:
  - **错误类型标签**:
    - 尺寸: wrap_content × 28.dp
    - 圆角: CornerRadius.small (8.dp)
    - 背景: Error背景20%透明度
    - 文本: LabelMedium, Error
    - 示例: "语法错误", "拼写错误", "时态错误"
  - **错误文本**: HeadlineMedium, 白色, 删除线
  - **错误说明**: BodyLarge, 灰色
  - **建议列表**:
    - 标题: "建议修改", LabelLarge, 白色
    - 每项高度: 48.dp
    - 圆角: CornerRadius.medium (12.dp)
    - 背景: 深灰 (#3A3A3A)
    - 点击时: 主题色背景
    - 文本: BodyMedium, 白色
    - 图标: 勾选, 20.dp, Success

### 语法评分算法
- 评分范围: 0-100分
- 计算因素:
  - 语法错误数量（权重40%）
  - 拼写错误数量（权重20%）
  - 句子结构复杂度（权重20%）
  - 词汇丰富度（权重20%）
- 显示颜色:
  - 90-100分: 绿色 (#10B981)
  - 70-89分: 黄色 (#F59E0B)
  - 0-69分: 红色 (#EF4444)
- 实时更新: 每次编辑后重新计算

### 底部输入区域
- 高度: 56.dp
- 背景: 深色 (#1E1E1E)
- 内边距: 水平12.dp
- 布局: 水平排列
- 组件从左到右:
  1. **相机按钮** (📷)
     - 尺寸: 40.dp 圆形
     - 功能: 打开相机拍照识别文字
  2. **文本输入框**
     - 占据剩余空间
     - 圆角: 20.dp
     - 背景: 深灰 (#2A2A2A)
     - 提示文字: "发消息或按住说话..."
     - 文本: BodyMedium, 白色
     - 内边距: 水平16.dp, 垂直12.dp
     - 支持多行输入
     - 最大行数: 4行
  3. **上传按钮** (➕)
     - 尺寸: 40.dp 圆形
     - 功能: 上传文件/图片
  4. **发送/停止按钮** (⏺)
     - 尺寸: 40.dp 圆形
     - 默认: 发送按钮（灰色）
     - 输入时: 发送按钮（主题色）
     - 语音输入时: 停止按钮（红色）

### 底部导航栏
- 高度: 72.dp
- 背景: 深色 (#1E1E1E)
- 4个导航按钮，均匀分布
- 按钮样式:
  - 图标: 24.dp
  - 文字: LabelMedium
  - 未选中: 灰色 (#94A3B8)
  - 选中: 白色
  - 内边距: 垂直12.dp
- 导航项:
  1. 单词 (Vocabulary)
  2. 写作 (Writing) - 当前选中
  3. 对话 (Speaking)
  4. 设置 (Settings)

---

## 交互流程

### 写作流程
1. 用户进入写作练习页面
2. 在编辑区域输入英文文章（全屏编辑）
3. 系统实时检测语法错误并标注
4. 顶部右侧实时显示语法评分和错误数
5. 用户可以:
   - 点击错误标注查看详情
   - 选择建议修改
   - 在底部输入框询问AI写作建议
   - 点击保存按钮保存文章
6. 语法评分和错误数实时更新

### 错误修正流程
1. 用户点击波浪线标注的错误
2. 弹出错误详情弹窗
3. 显示错误类型、说明和建议
4. 用户点击建议项
5. 自动替换错误文本
6. 弹窗关闭，继续编辑

### AI辅助功能
- 用户可以在底部输入框询问:
  - "这句话怎么表达更好？"
  - "帮我润色这段文字"
  - "这个词用得对吗？"
  - "给我一些写作建议"
- AI在对话界面回复建议
- 支持语音输入询问

### 保存功能
- 点击保存按钮保存文章
- 自动保存草稿（每30秒）
- 显示保存成功提示
- 可以查看历史文章列表

---

## 动画规范

### 错误标注动画
```kotlin
// 错误标注淡入动画
val alpha by animateFloatAsState(
    targetValue = 1f,
    animationSpec = tween(300)
)

// 波浪线动画
val waveOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 8f,
    animationSpec = infiniteRepeatable(
        animation = tween(1000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
)
```

### 弹窗动画
```kotlin
// 底部弹窗滑入动画
val offsetY by animateFloatAsState(
    targetValue = 0f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
)
```

### 建议选中动画
```kotlin
// 建议项点击动画
val scale by animateFloatAsState(
    targetValue = if (isSelected) 0.95f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy
    )
)

val backgroundColor by animateColorAsState(
    targetValue = if (isSelected) Primary else SurfaceVariant,
    animationSpec = tween(200)
)
```

### 保存按钮动画
```kotlin
// 保存成功动画
val rotation by animateFloatAsState(
    targetValue = if (isSaved) 360f else 0f,
    animationSpec = tween(500, easing = FastOutSlowInEasing)
)
```
