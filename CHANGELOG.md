# CHANGELOG

## [v2.0.0] - 2026-03-05

### 🎨 架构重构 - 插件式节点架构

#### UI 模块重构
- **重构所有 UI 模块为插件式节点架构**
  - speaking: 节点管理器 + nodes/（AgentBubble, UserBubble, ChatWindow, InputBox, NavBar, History）
  - vocabulary: 节点管理器 + nodes/（CardStack, WordCardNode, BookmarkNode）
  - writing: 节点管理器 + nodes/（EssayPanel, EssayList, Keyboard）
  - sidebar: 节点管理器 + nodes/（DragDrop, Editor）
  - settings: 节点管理器 + nodes/（按功能分类：about, learning, data, model, perms）

- **统一架构模式**
  - 每个模块只有一个根组件（节点管理器）
  - 所有子组件都是可替换的节点
  - 节点独立、解耦、可插拔

#### AI 推理架构简化
- **简化 InferenceEngine.kt**
  - 删除同步推理，只保留异步流式推理
  - 专注核心功能：模型加载、异步推理、Token 计数
  - 移除复杂的辅助方法和包装层

- **创建新的 Agent 架构**
  - MainAgent.kt - 主 Agent
  - AgentService.kt - 统一服务层
  - Prompts.kt - 系统提示词
  - tools/ - 工具层（ReadTool, WriteTool, MessageTool, UpdateTitleTool）

- **删除冗余文件**
  - 删除 agent 目录旧文件（AgentConfig, AgentPrompts, AgentServiceImpl, Message, PromptMode, ThreadTitleAgent）
  - 删除 llm 目录冗余文件（InferenceConfig, InferenceService, InferenceTool, Prompts）
  - 合并 InferenceConfig 到 InferenceEngine.kt

### 📚 文档更新
- 为所有 UI 模块添加 README.md
- 更新 settings README 为新架构
- 清晰说明节点管理器和插件式架构

### 🧹 代码清理
- 删除过时的 AI 测试文件
- 删除 mediapipe 示例目录
- 简化命名方式

---

## [v0.5.0] - 2026-03-02

### 对话界面完成 🎉

#### 核心功能
- **输入区域布局**（7:3 比例）
  - 文本输入框 70%（相机按钮 + 输入区 + 发送按钮）
  - 语音按钮 30%（按住录音，松开发送）
  - 发送按钮位置：offset x = -108.dp

- **语音录音功能**
  - 按住语音按钮开始录音
  - 录音时背景变蓝色 (#BFDBFE)
  - 松开发送语音消息
  - 模拟语音消息显示

- **界面优化**
  - 输入框与底部导航栏间距 45.dp
  - 切换导航时自动关闭单词界面输入框
  - 键盘点击空白处关闭
  - 白色主题设计

#### 顶部栏功能
- **持续对话模式** - Phone/PhonePaused 图标切换
- **对话历史** - 类似作文收藏库的界面设计
- 对话卡片显示标题、预览、消息数、时间

#### 技术实现
- BasicTextField 自定义输入框
- 手势检测（awaitEachGesture）
- 状态管理（isRecording）
- 模拟数据测试

#### 开发工具
- 添加 VoiceInputTestScreen 测试界面
- 使用 @Preview 可视化调整布局
- Android Studio Design 视图手动调整按钮位置

---

## [v0.4.0] - 2026-03-02

### New Features ✨

#### Writing Screen Implementation
- **Writing Interface**
  - Title input field with system keyboard support (multi-language)
  - Content editor with custom English keyboard (English-only practice)
  - Real-time word count and character count display
  - Grammar score display (placeholder for future AI integration)
  - Clear and Save buttons with soft color scheme

- **Custom English Keyboard**
  - QWERTY layout with 4 rows (Q-P, A-L, Shift+Z-M+Backspace, Punctuation+Space)
  - Shift key for uppercase/lowercase toggle with visual feedback
  - Close button to dismiss keyboard
  - 15dp bottom padding to avoid screen edge
  - Automatically hides system keyboard when active

- **Essay Collection Library**
  - Save essays with title, content, and grammar score
  - Side panel display (40% width) with essay list
  - Click to load essay, long-press (3 seconds) to delete
  - Auto-refresh after save operation
  - Persistent storage using SharedPreferences + Gson

- **AI Assist Panel (Placeholder)**
  - Side panel layout (40% width) ready for LLM integration
  - Comment card design with color-coded feedback types
  - Grammar (yellow), Style (purple), Positive (green)

### UI/UX Enhancements 🎨

- **Top Bar Updates**
  - AI button (AutoAwesome icon) toggles purple when active
  - Collection button (Book icon) toggles blue when active
  - Grammar score and word type display in horizontal layout

- **Bottom Navigation Bar Animation**
  - Fade out when custom keyboard is visible
  - Fade in when keyboard is closed
  - Smooth transitions for better user experience

- **Color Scheme**
  - Clear button: Light red (#FEE2E2) with dark red icon (#DC2626)
  - Save button: Light green (#D1FAE5) with dark green icon (#059669)
  - Keyboard background: Light gray (#E5E7EB)
  - Keyboard buttons: White with dark gray text

### Technical Implementation 🔧

- **File Structure**
  - `WritingScreen.kt` - Main writing interface
  - `SimpleEnglishKeyboard.kt` - Custom keyboard component
  - `EssayCollectionPanel.kt` - Essay collection side panel
  - `Essay.kt` - Essay data class
  - `EssayCollectionManager.kt` - Essay persistence manager

- **Key Features**
  - Dual keyboard strategy (system for title, custom for content)
  - English-only input filtering for content area
  - Side panel pattern (mutually exclusive AI/Collection panels)
  - Callback mechanism for bottom navigation bar control

### Design Decisions 📐

1. **Dual Keyboard Strategy**
   - Title field uses system keyboard (supports all languages for labeling)
   - Content field uses custom English keyboard (forces English practice)

2. **Side Panel Pattern**
   - Both AI assist and essay collection use 40% width side panels
   - Mutually exclusive (only one can be open at a time)
   - Consistent with word bookmark functionality

3. **Bottom Navigation Behavior**
   - Hides when custom keyboard is visible (more screen space)
   - Fade in/out animations for smooth transitions

4. **English-Only Input Filtering**
   - Allows: letters, digits, whitespace, English punctuation
   - Prevents accidental non-English input during practice

### Documentation 📚

- Created `docs/writing-screen-implementation.md` with comprehensive implementation details
- Documented features, architecture, design decisions, and future enhancements

### Git Commits 📝

- `1dd3a9a` - Implement custom English keyboard
- `9b894be` - Add fade in/out animation for bottom navigation bar when custom keyboard is visible
- `9b7599b` - Move keyboard buttons up by 15dp to avoid being too close to screen edge

### Known Limitations ⚠️

- Cursor positioning: Input always appends to the end
- No text selection support yet
- No undo/redo functionality
- AI features are placeholder only (no actual LLM integration)

### Future Enhancements 🚀

- AI integration with local LLM for grammar checking
- Advanced editing (undo/redo, text selection, cursor positioning)
- Export options (text files, sharing, backup/restore)
- Writing analytics (progress tracking, vocabulary usage, error patterns)
- Custom cursor implementation with positioning support

---

## [v0.3.0] - 2026-03-02

### 新增功能 ✨

#### 智能复习系统
- **洗牌算法**
  - 加载单词页面时自动混入复习单词
  - 按比例混合：每 8 个新单词插入 1 个复习单词（可配置 3-20）
  - 复习单词随机插入到新单词中（避开前 2 个位置）
  - 未记住的单词自动循环出现，直到被标记为"已记住"

- **技术实现**
  - 创建 `ReviewManager` 管理复习逻辑
  - 使用 `shuffleWithReviewWords()` 实现洗牌算法
  - 复习比例持久化保存到 SharedPreferences
  - 自然融入学习流程，不打断用户

#### 学习进度持久化
- **自动保存和恢复**
  - 保存当前页索引和页内索引
  - 每次滑动卡片后自动保存
  - 应用启动时自动恢复上次学习位置
  - 切换导航页不丢失进度

- **技术实现**
  - 创建 `LearningProgressManager` 管理学习进度
  - 使用 SharedPreferences 持久化存储
  - 数据：`current_page_index`, `current_index_in_page`

#### 收藏功能持久化
- **永久保存收藏**
  - 收藏的单词在应用重启后保持不变
  - 自动保存和加载收藏数据
  - 使用单词 ID 进行重复检测（更可靠）
  - 支持添加、移除、查询和清空操作

- **技术实现**
  - 为 `BookmarkManager` 添加 SharedPreferences 持久化
  - JSON 序列化存储 Word 列表
  - 自动去重和数据完整性检查

#### JSON 单词加载器
- **动态数据加载**
  - 从 JSON 资源文件加载单词数据（925KB）
  - 替代硬编码的 5000 个单词
  - 实现内存缓存机制避免重复加载
  - 首次加载约 200-300ms，后续 <10ms

- **技术实现**
  - 创建 `JsonWordLoader` 实现 JSON 解析
  - 使用 Gson 进行数据解析
  - 全局缓存优化性能

### 重大改进 🔧

#### 单词卡片 UI 重新设计
- **面积分配优化**（总高度 560dp）
  - 单词区域：30% (168dp) - 单词 + 音标
  - 释义区域：70% (392dp) - 中文释义

- **字体层次优化**
  - 单词：56sp（↑ 从 32sp），蓝色 (#2563EB)，加粗，字母间距 2sp
  - 音标：24sp（↑ 从 16sp），灰色 (#64748B)，中等粗细
  - 释义：22sp，深灰色，左对齐，行高 36sp

- **视觉优化**
  - 添加渐变背景增加深度感
  - 释义放在浅灰色圆角背景框中突出重点
  - 增加内边距和行间距提升可读性
  - 移除例句（数据集为占位符）
  - 专注于单词记忆核心内容

#### 数据架构重构
- **从硬编码到 JSON**
  - 删除 `EcdictWords.kt`（690KB 硬编码数据）
  - 创建 `ecdict_words.json`（925KB JSON 数据）
  - 减少代码约 5160 行
  - 提升构建性能和可维护性

- **加载机制优化**
  - `StreamingWordLoader` 使用 JSON 数据
  - `WordRepository` 简化加载逻辑
  - 保持 LruCache 缓存机制（2 页）

### 修复 🐛

- **Method too large 编译错误**
  - 问题：5000 个单词硬编码导致 JVM 64KB 方法大小限制错误
  - 解决：迁移到 JSON 资源文件，运行时动态加载

- **重复 context 声明**
  - 修复 VocabularyScreen 中的冲突变量声明

- **学习进度丢失**
  - 修复切换导航页或退出应用后进度丢失的问题

- **收藏数据丢失**
  - 修复应用重启后收藏数据丢失的问题

### 代码优化 🎨

- **移除调试日志**
  - 清理 `BookmarkManager`, `WordRepository`, `StreamingWordLoader`, `JsonWordLoader`
  - 移除所有 Log.d/e 语句（约 50 行）
  - 保留异常 printStackTrace 用于错误处理

- **代码简化**
  - 简化 WordRepository 初始化逻辑
  - 优化 StreamingWordLoader 加载流程
  - 清理未使用的导入和变量

### 数据统计 📊

- **单词数据**
  - 单词总数：4998 个
  - JSON 文件大小：925KB
  - 数据格式：包含 id, word, phonetic, translation, example, frequency, level, pos

- **代码变化**
  - 新增文件：5 个
  - 修改文件：8 个
  - 删除文件：1 个（EcdictWords.kt）
  - 代码减少：约 5160 行（删除硬编码数据）
  - 代码新增：约 500 行（新功能）
  - 净减少：约 4660 行

### 性能优化 ⚡

- **内存管理**
  - JSON 数据一次性加载，全局缓存
  - StreamingWordLoader 使用 LruCache（最多缓存 2 页）
  - 避免重复加载和解析

- **加载速度**
  - 首次加载：约 200-300ms（解析 JSON）
  - 后续加载：<10ms（使用缓存）
  - 页面切换：即时响应

### 工具脚本 🛠️

- **数据提取脚本**
  - 创建 `extract_words.py` 提取单词数据
  - 从 Kotlin 文件转换为 JSON 格式
  - 自动处理换行符和特殊字符
  - 输出文件大小统计

---

## [v0.2.0] - 2026-03-02

### 新增功能 ✨

#### 笔记拖拽分类功能
- **拖拽交互**
  - 长按笔记卡片触发拖拽（使用 `detectDragGesturesAfterLongPress`）
  - 拖拽预览层跟随手指移动
  - 拖拽时原卡片变透明，内容隐藏
  - 半透明预览卡片（透明度 0.9）

- **悬停反馈**
  - 拖拽到分组卡片上时，分组变绿色高亮（#10B981）
  - 实时检测手指位置是否在分组区域内
  - 视觉反馈清晰直观

- **自动更新分组**
  - 松手时自动更新笔记的分组关联
  - 无需弹窗确认，操作流程简化为 2 步
  - 支持拖拽到任意分组

- **技术实现**
  - 创建 `DragDropState` 类管理拖拽状态
  - 使用 `LocalDensity.toDp()` 正确转换像素到 dp
  - 坐标系转换：屏幕绝对坐标 → Box 内相对坐标
  - 拖拽预览层放在笔记列表 Box 内，确保正确的层级关系

#### 侧边栏功能
- **侧边栏基础结构**
  - 宽度 300dp，从左侧滑入/滑出（300ms 动画）
  - 半透明黑色遮罩层，点击关闭侧边栏
  - 适配状态栏高度
  - 白色主题设计

- **搜索功能**
  - 搜索框从右向左滑入（600ms 动画）
  - "Accelerator" 文字逐个字符消失动画（每个字符延迟 50ms）
  - 搜索框胶囊形状（圆角 20dp），高度 40dp
  - 搜索框有内容时按钮变绿色
  - 智能搜索：有内容时执行搜索，无内容时切换模式

- **笔记编辑器**
  - 创建 Note 数据模型和 NoteManager 管理器
  - 标题和内容输入框
  - 空内容保存提示（红色警告文字）
  - 编辑器状态持久化（关闭侧边栏不丢失内容）
  - 点击新建笔记按钮进入编辑器
  - 点击笔记卡片编辑已有笔记
  - 只显示有内容的笔记

- **全部笔记区域**
  - 深绿色圆角方形新建按钮（32dp）
  - 笔记卡片 100x120dp（高度大于宽度）
  - 按时间戳倒序排列
  - 排序切换按钮（正序/倒序）
  - 水平滚动列表

- **笔记分组区域**
  - 排序切换按钮
  - 添加分组按钮
  - 分组卡片水平滚动
  - 10 个示例分组

- **单词学习日志**
  - 置顶分类（默认展开，有背景）
  - 今天分类（默认展开，无背景）
  - 本周分类（默认折叠，无背景）
  - 更早分类（始终展开，有背景）
  - 可折叠功能，箭头旋转动画

- **设置按钮跳转**
  - 点击设置按钮跳转到设置页
  - 自动关闭侧边栏
  - 完成侧边栏与底部导航联动

### 技术改进 🔧

#### 状态管理优化
- 编辑器状态提升到页面组件
- 状态持久化，关闭侧边栏不丢失
- 清晰的回调传递机制

#### 动画效果
- 侧边栏滑入/滑出动画
- 文字逐个消失动画
- 箭头旋转动画
- 搜索框滑入动画

### 修复 🐛

- 修复编辑器状态持久化问题
- 修复搜索框垂直对齐问题
- 修复搜索框滑入速度过快问题
- 修复笔记排序问题

### 样式优化 🎨

- 新建笔记按钮改为深绿色
- 搜索框胶囊形状设计
- 排序按钮紧贴标题
- 笔记卡片尺寸优化

### 数据 📊

- 新增文件：3 个（Sidebar.kt、NoteEditor.kt、Note.kt）
- 修改文件：4 个
- 代码行数：约 500+ 行
- Git 提交：约 10 次

---

## [v0.1.0] - 2026-03-02

### 新增功能 ✨

#### 核心功能
- **单词卡片系统**
  - 实现单词卡片 UI，显示单词、音标、释义和例句
  - 卡片尺寸 560dp，圆角 24dp，带阴影效果
  - 白色背景适配浅色主题

- **卡片堆叠与滑动交互**
  - 3张卡片堆叠效果，后面的卡片向右偏移 6dp，向下偏移 10dp
  - 支持左滑/右滑切换单词（超过屏幕宽度 30% 触发）
  - 平滑的飞出和回弹动画
  - 滑动时卡片带轻微旋转效果

- **长按收藏功能**
  - 长按单词卡片（500ms）触发收藏
  - 震动反馈提升用户体验
  - 收藏本页面显示所有收藏的单词
  - 支持删除收藏（右上角 X 按钮）

- **自定义 Toast 提示**
  - 左滑"未记住" - 浅红色背景 (#FEE2E2)
  - 右滑"已记住" - 浅绿色背景 (#DCFCE7)
  - 长按"已收藏" - 浅蓝色背景 (#DEEDFF)
  - Toast 位于单词卡片上方 20dp
  - 自动在 1.5 秒后淡出消失

#### 导航系统
- **底部导航栏**
  - 4个导航按钮：单词、写作、对话、设置
  - Material3 简约设计风格
  - 支持选中/未选中状态切换

- **顶部导航栏**
  - 左侧菜单按钮（圆形背景）
  - 右侧对话按钮 + 收藏本按钮（胶囊背景）
  - 对话按钮支持 toggle 切换，激活时显示深灰色背景

#### 交互功能
- **底部输入区域**
  - 胶囊背景设计，圆角 28dp
  - 包含相机、文本输入框、上传、发送按钮
  - 按钮悬浮在输入框上层
  - 支持最多 2 行文本输入
  - 发送按钮根据输入状态改变颜色

- **对话模式切换**
  - 点击对话按钮显示/隐藏输入区域
  - 单词卡片位置动态调整
  - 平滑的滑入/滑出动画

- **键盘管理**
  - 点击卡片区域空白处隐藏键盘
  - 提升输入体验

### 技术改进 🔧

#### 架构优化
- 采用 MVVM 架构模式
- 组件化设计，高度可复用
- 清晰的状态管理

#### 动画效果
- 使用 `animateDpAsState` 实现平滑的尺寸动画
- 使用 `Animatable` 实现复杂的滑动动画
- 使用 `AnimatedVisibility` 实现淡入淡出效果

#### 用户体验
- 触觉反馈（震动）
- 视觉反馈（颜色变化、Toast 提示）
- 流畅的动画过渡
- 直观的手势交互

### 修复 🐛

- 修复单词卡片位置偏上的问题，调整底部内边距实现视觉居中
- 修复底部输入区域位置不正确的问题，移到 MainActivity 的 Scaffold bottomBar
- 修复键盘无法隐藏的问题，添加点击空白区域隐藏键盘功能
- 修复 Toast 位置不正确的问题，多次调整达到最佳视觉效果
- 修复输入区域按钮布局拥挤的问题，改为悬浮按钮设计
- 修复多次编译错误，添加缺失的导入语句

### 样式优化 🎨

- 单词卡片文字颜色优化（主文本 #1E293B，次要文本 #64748B）
- 底部输入区域背景色加深（#F1F5F9 → #E2E8F0）
- Toast 提示样式优化（宽度 200dp，文字居中，带阴影）
- 按钮间距优化（15dp）
- 顶部栏适配状态栏高度

### 数据 📊

- 新增文件：约 15 个
- 代码行数：约 1500+ 行
- Git 提交：约 20 次
- 主要语言：Kotlin
- UI 框架：Jetpack Compose

### 已知问题 ⚠️

以下功能尚未实现，将在后续版本中添加：

- 侧边栏功能
- 对话模式实际功能（相机、上传、发送按钮）
- 单词发音播放（TTS 服务）
- 数据持久化（Room 数据库）
- 单词学习进度追踪
- 动态加载单词列表

### 依赖 📦

- Kotlin 2.0.21
- Jetpack Compose BOM 2025.07.00
- Material3
- Material Icons Extended
- Android Gradle Plugin 9.0.0

---

## 下一步计划 🚀

- [ ] 实现数据持久化（Room 数据库）
- [ ] 集成 TTS 服务实现单词发音
- [ ] 实现侧边栏功能
- [ ] 完善对话模式的实际功能
- [ ] 添加单词学习进度追踪
- [ ] 优化性能和内存使用

---

**版本说明**：本版本为单词学习模块的初始版本，实现了核心的 UI 和交互功能，为后续功能开发奠定了基础。
笔记分组功能设计
核心概念
笔记分组卡片 = Toggle 按钮（用于筛选笔记）
全部笔记 = 所有笔记的集合
交互逻辑
长按笔记卡片 + 拖拽

长按"全部笔记"中的笔记卡片
拖拽到某个"笔记分组卡片"上
该笔记就被归类到这个分组中
点击笔记分组卡片（Toggle 功能）

第一次点击：只显示该分组中的笔记
再次点击：显示全部笔记（取消筛选）
视觉状态

未选中状态：显示全部笔记
选中状态：只显示该分组的笔记（分组卡片高亮显示）
举个例子
你有 10 个笔记
你长按"笔记1"拖到"工作"分组
你长按"笔记2"拖到"工作"分组
点击"工作"分组卡片 → 只显示"笔记1"和"笔记2"
再次点击"工作"分组卡片 → 显示全部 10 个笔记