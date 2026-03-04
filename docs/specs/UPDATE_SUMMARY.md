# 后端设计文档更新总结

## 更新日期
2026-03-01

## 更新原因
根据最新的 UI 设计文档（`english-learning-app/ui-design/`）更新后端设计文档，确保后端架构与前端 UI 设计保持一致。

---

## 更新内容概览

### 1. design.md 更新
**文件**: `english-learning-app/design.md`

#### 新增数据模型
- **Note** 和 **NoteGroup**：支持笔记管理和分组功能
- **UserSettings**：统一管理用户设置（主题、TTS、AI模型参数、学习设置、自动朗读设置）
- **WordLearningLog**：记录单词学习行为日志
- **AppTheme** 枚举：4种主题（白色、暗色、苹果绿、亮紫）
- **DayOfWeek** 枚举：支持日期选择

#### 更新的数据模型
- **UserProgress**：添加 `writingLevel`、`speakingLevel`、`grammarLevel` 字段

#### 新增 UI 组件
- **SettingsScreen**：设置页面（我的页面）
- **Sidebar**：侧边栏组件
- **VoiceSettingsScreen**：TTS 音色设置页面
- **ModelSettingsScreen**：AI 模型参数设置页面
- **GeneralSettingsScreen**：通用设置页面
- **ThemeSelectionScreen**：主题选择页面

#### 新增 ViewModel
- **SettingsViewModel**：管理用户设置和进度
- **SidebarViewModel**：管理笔记和学习日志

#### 新增 Repository 接口
- **NoteRepository**：笔记数据访问
- **NoteGroupRepository**：笔记分组数据访问
- **UserSettingsRepository**：用户设置数据访问
- **UserProgressRepository**：用户进度数据访问
- **WordLearningLogRepository**：学习日志数据访问

#### 技术栈更新
- 将 **SharedPreferences** 改为 **DataStore**（用于设置持久化）

---

### 2. requirements.md 更新
**文件**: `english-learning-app/requirements.md`

#### 新增需求（需求 12-20）

**需求 12：侧边栏笔记管理**
- 右滑手势打开侧边栏
- 显示笔记列表（最近5个，可滚动）
- 显示笔记分组（2行网格）
- 显示单词学习日志（按时间分组）
- 支持创建笔记和分组

**需求 13：主题切换**
- 支持4种主题（白色、暗色、苹果绿、亮紫）
- 2×2网格显示主题选择
- 实时预览主题效果
- 主题切换动画（300ms）
- 主题持久化到 DataStore

**需求 14：设置页面（我的页面）**
- 用户信息区域（头像、用户名，可编辑）
- 水平滚动选项卡（收藏、单词、写作、口语、个人简历）
- 英语学业水平卡片（学习数据统计 + AI 学习建议）
- 功能设置列表（任务、主题色、朗读音色、模型设置、设置）

**需求 15：TTS 音色设置**
- 音色列表和预览
- 语速滑块（0.5x-2.0x）
- 音量滑块（0-100%）
- 实时应用和保存

**需求 16：AI 模型参数设置**
- Temperature 滑块（0.0-2.0）
- Max Tokens 滑块（1-2048）
- Top P 滑块（0.0-1.0）
- 参数说明和建议值
- 参数验证和保存

**需求 17：自动朗读设置**
- 3个开关（文本/单词/语法）
- 日期选择（周一至周日多选）
- 时间设置
- 定时触发 TTS 朗读

**需求 18：数据导入导出**
- 可折叠的数据管理区域
- 导出数据（选择类型和格式）
- 导入数据（文件选择和验证）
- 清除缓存（保留学习数据）

**需求 19：单词卡片滑动交互**
- 左滑标记"未记住"
- 右滑标记"已记住"
- 双击播放发音
- 长按收藏到单词本
- 滑动动画和震动反馈

**需求 20：底部输入区域**
- 统一的底部输入组件
- 相机按钮（拍照识别）
- 文本输入框（多行、自动换行）
- 上传按钮（文件选择）
- 发送/停止按钮（状态变化）

---

### 3. tasks.md 更新
**文件**: `english-learning-app/tasks.md`

#### 新增任务（任务 14-23）

**任务 14：实现主题系统**
- 创建主题数据模型和 DataStore
- 实现主题颜色系统（4种主题配色）
- 实现主题选择 UI（2×2网格、实时预览）

**任务 15：实现设置页面（我的页面）**
- 创建用户设置数据模型
- 实现 SettingsViewModel
- 实现设置页面 UI（用户信息、选项卡、学业水平卡片、功能设置）

**任务 16：实现设置子页面**
- TTS 音色设置页面
- AI 模型参数设置页面
- 通用设置页面（学习提醒、自动朗读、权限管理、数据管理）

**任务 17：实现笔记管理功能**
- 创建笔记数据模型（Note、NoteGroup）
- 实现笔记 Use Cases
- 实现 SidebarViewModel
- 实现侧边栏 UI（品牌区域、笔记列表、分组网格、学习日志）

**任务 18：实现单词卡片滑动交互**
- 实现滑动手势检测（左滑/右滑）
- 实现双击和长按手势
- 实现单词收藏功能

**任务 19：实现底部输入区域**
- 创建通用底部输入组件
- 集成到各个页面（单词、口语、写作）
- 实现相机和文件上传功能

**任务 20：实现自动朗读功能**
- 创建定时任务管理器（WorkManager）
- 实现自动朗读 Worker
- 集成到设置页面

**任务 21：实现数据导入导出**
- 创建数据导出功能（JSON/CSV）
- 创建数据导入功能
- 实现清除缓存功能

**任务 22：更新导航和主界面**
- 更新底部导航栏（4个按钮：单词、写作、对话、设置）
- 集成侧边栏（右滑手势）
- 更新主题应用

**任务 23：最终检查点**
- 完整功能集成测试

---

## 主要技术变更

### 1. 数据持久化
- **旧方案**：SharedPreferences
- **新方案**：DataStore（类型安全、协程支持、更好的性能）

### 2. 定时任务
- **新增**：WorkManager（用于自动朗读功能）

### 3. 数据格式
- **新增**：支持 JSON 和 CSV 格式的数据导入导出

### 4. 手势交互
- **新增**：单词卡片滑动手势（左滑/右滑/双击/长按）
- **新增**：侧边栏右滑手势

---

## UI 设计对应关系

| UI 设计文档 | 后端组件 | 数据模型 |
|------------|---------|---------|
| 02-vocabulary-screen.md | VocabularyScreen + VocabularyViewModel | Word, WordLearningLog |
| 03-speaking-screen.md | SpeakingScreen + SpeakingViewModel | Conversation, ConversationTurn |
| 04-writing-screen.md | WritingScreen + WritingViewModel | Essay, GrammarError |
| 08-settings-screen.md | SettingsScreen + SettingsViewModel | UserSettings, UserProgress |
| 09-sidebar.md | Sidebar + SidebarViewModel | Note, NoteGroup, WordLearningLog |
| 10-theme-styles.md | ThemeSelectionScreen | AppTheme (enum) |
| settings-pages/voice-settings.md | VoiceSettingsScreen | UserSettings (TTS 部分) |
| settings-pages/model-settings.md | ModelSettingsScreen | UserSettings (AI 模型部分) |
| settings-pages/general-settings.md | GeneralSettingsScreen | UserSettings (学习设置、自动朗读) |

---

## 下一步工作

1. **开始实现任务 14**：主题系统
2. **开始实现任务 15**：设置页面
3. **逐步完成任务 16-23**：其他新功能

---

## 文件变更记录

### 修改的文件
- `english-learning-app/design.md`（根目录设计文档）
- `english-learning-app/requirements.md`
- `english-learning-app/tasks.md`
- `english-learning-app/design/04-data-models.md`（详细数据模型文档）
- `english-learning-app/design/05-components-interfaces.md`（详细组件接口文档）

### Git 提交记录
```
d326e36 docs: 更新design目录下的详细设计文档
66b1232 docs: 添加后端设计文档更新总结
fc91d39 docs: 更新tasks.md - 添加新功能实现任务
e6b4231 docs: 更新requirements.md - 添加新功能需求
1f18f63 docs: 更新design.md - 添加新的数据模型和组件
8cd9b88 feat: 完善通用设置页面 - 添加自动朗读设置和数据管理功能
```

---

## design/ 目录详细文档更新

### 04-data-models.md 更新内容
- **新增 Note 和 NoteGroup 模型**：支持笔记管理和分组
- **新增 UserSettings 模型**：统一管理所有用户设置
- **新增 WordLearningLog 模型**：记录学习行为日志
- **新增数据模型关系图**：使用 Mermaid ER 图展示表关系
- **新增数据持久化策略**：
  - Room Database：结构化数据
  - DataStore：用户设置（替代 SharedPreferences）
  - File Storage：大文件（头像、音频、模型）
- **新增数据库迁移策略**：版本1→版本2的迁移代码示例

### 05-components-interfaces.md 更新内容
- **新增 UI 组件**：
  - SettingsScreen（设置页面）
  - Sidebar（侧边栏）
  - VoiceSettingsScreen、ModelSettingsScreen、GeneralSettingsScreen、ThemeSelectionScreen
  - BottomInputArea（通用底部输入）
  - SwipeableWordCard（支持滑动手势的单词卡片）
- **新增 ViewModel**：
  - SettingsViewModel（12个方法）
  - SidebarViewModel（5个方法）
- **新增 Repository 接口**：
  - NoteRepository（8个方法）
  - NoteGroupRepository（4个方法）
  - UserSettingsRepository（6个方法）
  - UserProgressRepository（6个方法）
  - WordLearningLogRepository（5个方法）
- **新增 Use Cases**：
  - 笔记管理（CreateNote、CreateNoteGroup、SearchNotes、PinNote）
  - 单词收藏（BookmarkWord）
  - 数据导入导出（ExportData、ImportData）
- **新增架构图**：组件依赖关系图（Mermaid）
- **新增设计原则**：接口设计的5大原则

---

## 总结

本次更新确保了后端设计文档与最新的 UI 设计文档完全同步，新增了：
- **5个新数据模型**
- **9个新需求**
- **10个新实现任务**
- **多个新 UI 组件和 ViewModel**

所有更新都遵循了原有的架构设计原则，保持了代码的一致性和可维护性。
