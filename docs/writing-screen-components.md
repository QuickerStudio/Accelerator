# 写作导航界面组件标准

## 界面结构

```
WritingScreen (写作界面)
├── VocabularyTopBar (顶部栏)
│   ├── MenuButton (菜单按钮)
│   ├── GrammarInfo (语法信息区)
│   │   ├── GrammarScore (语法评分)
│   │   └── CurrentWordType (当前词语类型)
│   ├── AiAssistButton (AI 辅助按钮)
│   └── CollectionButton (作文收藏库按钮)
├── EditorArea (编辑器区域)
│   ├── MainEditArea (主编辑区)
│   │   ├── TitleTextField (标题输入框)
│   │   │   └── SwipeActions (滑动操作)
│   │   │       ├── ClearAction (清空操作)
│   │   │       └── SaveToCollectionAction (保存到收藏库)
│   │   ├── ContentEditor (内容编辑器)
│   │   │   ├── LineNumberArea (行号区域)
│   │   │   ├── ContentArea (内容区域)
│   │   │   └── VerticalScrollbar (垂直滚动条)
│   │   └── EditorToolbar (底部工具栏)
│   │       ├── StatisticsInfo (统计信息)
│   │       │   ├── WordCount (词数)
│   │       │   └── CharCount (字符数)
│   │       └── ActionButtons (操作按钮)
│   │           ├── AiAnalyzeButton (AI 分析按钮)
│   │           ├── ClearButton (清空按钮)
│   │           └── SaveButton (保存按钮)
│   └── AiAssistPanel (AI 辅助面板)
│       ├── PanelHeader (面板标题)
│       └── CommentList (评论列表)
│           └── CommentCard (评论卡片)
│               ├── LineNumberBadge (行号标记)
│               └── CommentText (评论内容)
└── Sidebar (侧边栏)
```

## 组件详细说明

### 1. 顶部栏 (VocabularyTopBar)
**位置**: 界面顶部
**组件**: `VocabularyTopBar`

#### 1.1 菜单按钮 (MenuButton)
- **图标**: `Icons.Default.Menu`
- **功能**: 打开侧边栏
- **回调**: `onMenuClick`
- **样式**: 圆形背景，灰色 (#F1F5F9)

#### 1.2 语法信息区 (GrammarInfo)
**位置**: 菜单按钮右侧
**背景**: 浅灰色 (#F1F5F9)，圆角 8dp

##### 1.2.1 语法评分 (GrammarScore)
- **格式**: "语法评分：000"（三位数，不足补零）
- **状态**: `grammarScore` (Int)
- **字体大小**: 12sp
- **颜色**: #64748B
- **功能**: 实时显示当前文本的语法评分

##### 1.2.2 当前词语类型 (CurrentWordType)
- **格式**: "词语类型：adj"
- **状态**: `currentWordType` (String)
- **字体大小**: 12sp
- **颜色**: #8B5CF6 (紫色)
- **功能**: 显示正在输入的词语类型
- **识别方式**:
  - 优先使用 AI 分析
  - AI 响应慢时使用数据库对比
  - 词语输入完毕后进行分析

#### 1.3 AI 辅助按钮 (AiAssistButton)
- **图标**: `Icons.Default.AutoAwesome` ⭐
- **功能**: 切换 AI 辅助面板显示/隐藏
- **回调**: `onConversationClick`
- **状态**: `showAiPanel` (Boolean)
- **样式**:
  - 胶囊背景
  - 激活时紫色 (#8B5CF6)
  - 未激活时透明

#### 1.4 作文收藏库按钮 (CollectionButton)
- **图标**: `Icons.Default.Book`
- **功能**: 打开作文收藏库
- **回调**: `onBookmarkClick`
- **样式**: 胶囊背景
- **说明**: 用于查看和管理已保存的作文

---

### 2. 主编辑区 (MainEditArea)
**位置**: 界面中央
**布局**: 占 60% 宽度（AI 面板打开时），100% 宽度（AI 面板关闭时）

#### 2.1 标题输入框 (TitleTextField)
- **组件**: `TitleTextField`
- **占位符**: "标题"
- **状态**: `title` (String)
- **样式**:
  - 字体大小: 28sp
  - 字体粗细: Bold
  - 颜色: #1E293B
  - 背景: 白色，圆角 12dp

##### 2.1.1 长按滑动操作 (SwipeActions)
**触发方式**: 长按标题输入框
**显示时长**: 3 秒后自动隐藏
**布局**: 与标题输入框等高等宽的悬浮按钮

###### 清空操作 (ClearAction)
- **位置**: 左侧
- **图标**: `Icons.Default.Delete`
- **文本**: "清空"
- **颜色**: 红色 (#EF4444)
- **功能**: 清空标题内容
- **回调**: `onClear`

###### 保存到收藏库 (SaveToCollectionAction)
- **位置**: 右侧
- **图标**: `Icons.Default.Book`
- **文本**: "收藏"
- **颜色**: 绿色 (#10B981)
- **功能**: 保存当前作文到收藏库
- **回调**: `onSaveToCollection`

#### 2.2 内容编辑器 (ContentEditor)
- **组件**: `ContentEditor`
- **占位符**: "开始写作..."
- **状态**: `content` (String)
- **特性**: 带行号显示和垂直滚动条

##### 2.2.1 行号区域 (LineNumberArea)
- **位置**: 编辑器左侧
- **背景**: #F8FAFC
- **字体大小**: 16sp
- **行高**: 28sp
- **颜色**: #94A3B8
- **功能**: 显示行号（从 1 开始）
- **滚动**: 与内容区域同步滚动

##### 2.2.2 内容区域 (ContentArea)
- **位置**: 行号右侧
- **字体大小**: 18sp
- **行高**: 28sp
- **颜色**: #1E293B
- **光标颜色**: #2563EB
- **滚动**: 垂直滚动

##### 2.2.3 垂直滚动条 (VerticalScrollbar)
- **位置**: 编辑器右侧
- **宽度**: 6dp
- **背景**: #F1F5F9
- **滑块颜色**: #94A3B8
- **滑块高度**: 30% 可视区域
- **圆角**: 3dp
- **功能**: 显示滚动位置，可拖动浏览内容

---

### 3. 底部工具栏 (EditorToolbar)
**位置**: 主编辑区底部
**组件**: `EditorToolbar`

#### 3.1 统计信息 (StatisticsInfo)
- **词数 (WordCount)**: `$wordCount 词`
- **字符数 (CharCount)**: `$charCount 字符`
- **颜色**: #64748B
- **字体大小**: 14sp

#### 3.2 操作按钮 (ActionButtons)

##### 3.2.1 AI 分析按钮 (AiAnalyzeButton)
- **图标**: `Icons.Default.AutoAwesome`
- **颜色**: #8B5CF6 (紫色)
- **功能**: 触发 AI 分析，生成评论
- **回调**: `onAiAssist`
- **描述**: "AI 辅助"

##### 3.2.2 清空按钮 (ClearButton)
- **图标**: `Icons.Default.Delete`
- **颜色**: #64748B (灰色)
- **功能**: 清空标题和内容
- **回调**: `onClear`
- **描述**: "清空"

##### 3.2.3 保存按钮 (SaveButton)
- **图标**: `Icons.Default.Save`
- **文本**: "保存"
- **颜色**: #2563EB (蓝色)
- **功能**: 保存草稿（待实现）
- **回调**: `onSave`
- **描述**: "保存"

---

### 4. AI 辅助面板 (AiAssistPanel)
**位置**: 界面右侧
**组件**: `AiAssistPanel`
**布局**: 占 40% 宽度
**显示条件**: `showAiPanel == true`

#### 4.1 面板标题 (PanelHeader)
- **文本**: "AI 辅助"
- **图标**: `Icons.Default.AutoAwesome`
- **字体大小**: 18sp
- **字体粗细**: Bold
- **颜色**: #1E293B
- **图标颜色**: #8B5CF6

#### 4.2 评论列表 (CommentList)
- **空状态提示**: "点击 AI 按钮获取写作建议"
- **数据源**: `aiComments` (List<AiComment>)

#### 4.3 评论卡片 (CommentCard)
**组件**: `CommentCard`
**数据结构**: `AiComment(lineNumber, comment, type)`

##### 评论类型 (Comment Types)
1. **grammar** (语法)
   - 背景色: #FEF3C7 (浅黄)
   - 图标色: #F59E0B (橙黄)

2. **style** (风格)
   - 背景色: #DDD6FE (浅紫)
   - 图标色: #8B5CF6 (紫色)

3. **positive** (正面反馈)
   - 背景色: #D1FAE5 (浅绿)
   - 图标色: #10B981 (绿色)

##### 4.3.1 行号标记 (LineNumberBadge)
- **尺寸**: 24dp × 24dp
- **形状**: 圆角矩形 (4dp)
- **内容**: 行号数字
- **字体大小**: 12sp
- **字体粗细**: Bold
- **文字颜色**: 白色

##### 4.3.2 评论内容 (CommentText)
- **字体大小**: 14sp
- **颜色**: #1E293B

---

### 5. 侧边栏 (Sidebar)
**位置**: 左侧滑出
**组件**: `Sidebar`
**显示条件**: `showSidebar == true`
**功能**: 导航到设置、编辑器模式等

---

## 状态管理

### 编辑器状态
```kotlin
var title by remember { mutableStateOf("") }           // 标题
var content by remember { mutableStateOf("") }         // 内容
```

### UI 状态
```kotlin
var showSidebar by remember { mutableStateOf(false) }  // 侧边栏显示
var showAiPanel by remember { mutableStateOf(false) }  // AI 面板显示
```

### AI 状态
```kotlin
var aiComments by remember {
    mutableStateOf<List<AiComment>>(emptyList())
}
var grammarScore by remember { mutableStateOf(0) }     // 语法评分
var currentWordType by remember { mutableStateOf("") } // 当前词语类型
```

### 统计信息
```kotlin
val wordCount = content.trim()
    .split("\\s+".toRegex())
    .filter { it.isNotEmpty() }
    .size
val charCount = content.length
```

---

## 数据结构

### AiComment
```kotlin
data class AiComment(
    val lineNumber: Int,    // 行号
    val comment: String,    // 评论内容
    val type: String        // 类型: "grammar", "style", "positive"
)
```

---

## 回调函数

### 顶部栏回调
- `onMenuClick: () -> Unit` - 打开侧边栏
- `onConversationClick: () -> Unit` - 切换 AI 面板
- `onBookmarkClick: () -> Unit` - 打开作文收藏库

### 标题输入框回调
- `onClear: () -> Unit` - 清空标题
- `onSaveToCollection: () -> Unit` - 保存到收藏库

### 工具栏回调
- `onSave: () -> Unit` - 保存草稿
- `onClear: () -> Unit` - 清空内容
- `onAiAssist: () -> Unit` - 触发 AI 分析

### 导航回调
- `onNavigateToSettings: () -> Unit` - 导航到设置

---

## 颜色规范

| 用途 | 颜色代码 | 说明 |
|------|---------|------|
| 主文本 | #1E293B | 深灰色 |
| 次要文本 | #64748B | 中灰色 |
| 占位符 | #CBD5E1 | 浅灰色 |
| 行号文本 | #94A3B8 | 灰蓝色 |
| 主按钮 | #2563EB | 蓝色 |
| AI 按钮 | #8B5CF6 | 紫色 |
| 词语类型 | #8B5CF6 | 紫色 |
| 清空按钮 | #EF4444 | 红色 |
| 保存到收藏 | #10B981 | 绿色 |
| 背景 | #FAFAFA | 浅灰背景 |
| 卡片背景 | #FFFFFF | 白色 |
| 行号背景 | #F8FAFC | 极浅灰 |
| 按钮背景 | #F1F5F9 | 浅灰 |
| 滚动条背景 | #F1F5F9 | 浅灰 |
| 滚动条滑块 | #94A3B8 | 灰蓝色 |
| 语法提示 | #FEF3C7 | 浅黄 |
| 风格提示 | #DDD6FE | 浅紫 |
| 正面反馈 | #D1FAE5 | 浅绿 |

---

## 交互说明

### 长按标题输入框
1. 用户长按标题输入框
2. 显示滑动操作按钮（清空/收藏）
3. 3 秒后自动隐藏
4. 点击任一按钮后立即隐藏

### 滚动内容编辑器
1. 行号区域与内容区域同步滚动
2. 右侧显示垂直滚动条
3. 滚动条滑块高度为可视区域的 30%
4. 滚动条位置实时反映当前滚动位置

### 词语类型识别
1. 用户输入词语时实时分析
2. 词语输入完毕后显示类型
3. 优先使用 AI 分析
4. AI 响应慢时使用数据库对比
5. 显示格式：adj, noun, verb 等

### AI 辅助面板
1. 点击顶部栏 AI 按钮切换显示
2. 面板打开时编辑器宽度调整为 60%
3. 面板关闭时编辑器恢复 100% 宽度
4. 点击工具栏 AI 分析按钮生成评论

---

## 使用示例

### 触发 AI 分析
```kotlin
onAiAssist = {
    aiComments = listOf(
        AiComment(3, "建议：这句话可以更简洁", "grammar"),
        AiComment(7, "很好的表达！", "positive"),
        AiComment(12, "注意时态一致性", "grammar")
    )
    grammarScore = 85
}
```

### 更新词语类型
```kotlin
// 监听内容变化
LaunchedEffect(content) {
    val lastWord = content.split("\\s+".toRegex()).lastOrNull()
    if (lastWord != null && lastWord.isNotEmpty()) {
        currentWordType = analyzeWordType(lastWord) // "adj", "noun", etc.
    }
}
```

### 清空内容
```kotlin
onClear = {
    title = ""
    content = ""
    currentWordType = ""
    grammarScore = 0
}
```

### 切换 AI 面板
```kotlin
onConversationClick = {
    showAiPanel = !showAiPanel
}
```

### 保存到收藏库
```kotlin
onSaveToCollection = {
    // 保存当前作文到收藏库
    saveToCollection(title, content)
}
```

---

## 待实现功能 (TODO)

1. **保存草稿** - `onSave` 回调实现
2. **作文收藏库** - 收藏库界面和数据管理
3. **本地大模型接入** - 替换模拟的 AI 评论生成
4. **词语类型识别** - 实现 AI 或数据库识别
5. **语法评分算法** - 实时计算语法评分
6. **草稿持久化** - 保存到本地数据库
7. **评论交互** - 点击评论跳转到对应行
8. **实时 AI 建议** - 输入时自动分析
9. **滚动条拖动** - 支持拖动滚动条快速浏览
