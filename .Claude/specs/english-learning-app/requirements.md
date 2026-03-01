# 需求文档

## 简介

本文档定义英语学习 Android 应用原型阶段的核心需求，聚焦三大功能模块：单词学习（卡片翻转与标记）、AI 口语训练（语音录制 + LLM 对话 + TTS 播放）、写作练习（文本编辑 + 语法检查标注）。技术栈为 Kotlin + Jetpack Compose + 本地 1.5B LLM（kotlinllamacpp）+ Whisper ASR + Android TTS + Room Database。

## 词汇表

- **App**：英语学习 Android 应用整体
- **VocabularyScreen**：单词学习页面
- **WordCard**：展示单词正面（单词 + 音标）和背面（释义 + 例句）的翻转卡片组件
- **SpeakingScreen**：AI 口语训练页面
- **WritingScreen**：写作练习页面
- **LLMService**：本地 3B 参数大语言模型推理服务（Qwen2.5-3B-Instruct）
- **ASRService**：基于 Whisper 的本地语音识别服务
- **TTSService**：Android 系统 TTS 语音合成服务
- **GrammarChecker**：基于 LLMService 的语法检查服务
- **WordRepository**：单词数据访问层
- **EssayRepository**：作文数据访问层
- **ConversationRepository**：对话历史数据访问层
- **GrammarError**：语法错误对象，包含文本索引范围、错误类型和修改建议

---

## 需求

### 需求 1：单词卡片展示

**用户故事：** 作为学习者，我希望以卡片形式浏览今日单词，以便快速了解单词的读音、释义和例句。

#### 验收标准

1. WHEN 用户打开 VocabularyScreen，THE App SHALL 从 WordRepository 加载今日单词列表并展示第一张 WordCard。
2. WHEN 单词列表加载中，THE VocabularyScreen SHALL 显示加载指示器。
3. IF 单词列表加载失败，THEN THE VocabularyScreen SHALL 显示错误提示信息。
4. THE WordCard SHALL 在正面显示单词文本和音标，在背面显示中文释义和英文例句。
5. WHEN 用户点击 WordCard，THE WordCard SHALL 执行翻转动画并切换到背面内容。
6. WHEN WordCard 已处于背面，WHEN 用户再次点击，THE WordCard SHALL 翻转回正面。

---

### 需求 2：单词发音播放

**用户故事：** 作为学习者，我希望点击按钮即可听到单词的标准发音，以便纠正自己的读音。

#### 验收标准

1. WHEN 用户点击 WordCard 上的发音按钮，THE TTSService SHALL 以英语朗读当前单词。
2. IF TTSService 不可用，THEN THE App SHALL 显示提示"语音功能不可用"并保持其他功能正常。

---

### 需求 3：标记单词已学

**用户故事：** 作为学习者，我希望将已掌握的单词标记为"已学"，以便追踪学习进度。

#### 验收标准

1. WHEN 用户点击"已掌握"按钮，THE App SHALL 调用 WordRepository 将该单词状态更新为 LEARNED。
2. WHEN 单词状态更新成功，THE VocabularyScreen SHALL 自动跳转到下一张 WordCard。
3. IF WordRepository 更新失败，THEN THE App SHALL 显示错误提示并保持当前 WordCard 不变。
4. THE VocabularyScreen SHALL 显示当前已学单词数量与今日总单词数量的进度。

---

### 需求 4：AI 口语对话启动

**用户故事：** 作为学习者，我希望点击按钮启动与 AI 的英语对话，以便开始口语练习。

#### 验收标准

1. WHEN 用户点击"开始对话"按钮，THE SpeakingScreen SHALL 调用 LLMService 生成开场白并展示在对话列表中。
2. WHEN LLMService 生成开场白成功，THE TTSService SHALL 自动朗读该开场白文本。
3. IF LLMService 不可用，THEN THE SpeakingScreen SHALL 显示错误提示"AI 服务暂不可用"。

---

### 需求 5：语音录制与识别

**用户故事：** 作为学习者，我希望按住按钮录制我的语音并自动转文字，以便与 AI 进行对话。

#### 验收标准

1. WHEN 用户按住录音按钮，THE App SHALL 请求麦克风权限（如未授权）并开始录音。
2. IF 麦克风权限被拒绝，THEN THE App SHALL 显示提示"需要麦克风权限才能使用口语练习"。
3. WHEN 用户松开录音按钮，THE ASRService SHALL 对录制的音频执行语音识别并返回转录文本。
4. WHEN 转录成功，THE SpeakingScreen SHALL 将用户发言添加到对话列表并触发 AI 回复流程。
5. IF 转录结果为空（静音），THEN THE SpeakingScreen SHALL 提示"未检测到语音，请重试"。

---

### 需求 6：AI 回复与 TTS 播放

**用户故事：** 作为学习者，我希望 AI 能回复我的发言并朗读出来，以便进行自然的英语对话练习。

#### 验收标准

1. WHEN 用户发言转录完成，THE LLMService SHALL 基于对话历史生成 AI 回复文本。
2. WHEN LLMService 生成回复成功，THE SpeakingScreen SHALL 将 AI 回复添加到对话列表。
3. WHEN AI 回复添加到对话列表，THE TTSService SHALL 自动朗读该回复文本。
4. WHILE LLMService 正在生成回复，THE SpeakingScreen SHALL 显示加载状态并禁用录音按钮。
5. IF LLMService 生成回复失败，THEN THE SpeakingScreen SHALL 显示错误提示并重新启用录音按钮。

---

### 需求 7：对话历史展示

**用户故事：** 作为学习者，我希望看到完整的对话记录，以便回顾练习内容。

#### 验收标准

1. THE SpeakingScreen SHALL 以列表形式展示当前会话的所有对话轮次，区分用户发言和 AI 回复。
2. WHEN 新消息添加到对话列表，THE SpeakingScreen SHALL 自动滚动到最新消息。
3. THE App SHALL 将对话历史持久化到 ConversationRepository。

---

### 需求 8：写作文本编辑

**用户故事：** 作为学习者，我希望在应用内输入和编辑英文作文，以便进行写作练习。

#### 验收标准

1. THE WritingScreen SHALL 提供多行文本输入框供用户输入英文内容。
2. WHEN 用户输入文本，THE WritingViewModel SHALL 实时更新内部文本状态。
3. WHEN 用户点击"保存"按钮，THE App SHALL 将作文内容持久化到 EssayRepository。
4. IF EssayRepository 保存失败，THEN THE WritingScreen SHALL 显示错误提示"保存失败，请重试"。

---

### 需求 9：语法检查与错误标注

**用户故事：** 作为学习者，我希望应用自动检查我的语法错误并在文中标注，以便我了解并改正错误。

#### 验收标准

1. WHEN 用户点击"检查语法"按钮，THE GrammarChecker SHALL 调用 LLMService 分析当前文本并返回 GrammarError 列表。
2. WHEN GrammarChecker 返回结果，THE WritingScreen SHALL 在文本中以高亮方式标注所有 GrammarError 的位置。
3. THE GrammarChecker SHALL 返回的所有 GrammarError 的 startIndex 和 endIndex 必须在文本有效范围内，且 startIndex < endIndex。
4. THE GrammarChecker SHALL 返回的 GrammarError 列表按 startIndex 升序排列。
5. WHILE GrammarChecker 正在检查，THE WritingScreen SHALL 显示加载状态并禁用"检查语法"按钮。
6. IF 文本为空，THEN THE WritingScreen SHALL 不触发语法检查并提示"请先输入内容"。

---

### 需求 10：语法错误建议与采纳

**用户故事：** 作为学习者，我希望点击标注的错误查看修改建议并一键采纳，以便快速改正写作错误。

#### 验收标准

1. WHEN 用户点击文本中的错误标注，THE WritingScreen SHALL 显示该 GrammarError 的错误说明和修改建议列表。
2. WHEN 用户选择一条修改建议，THE WritingViewModel SHALL 用建议文本替换原文中对应的错误片段。
3. WHEN 建议被采纳，THE WritingScreen SHALL 关闭建议弹窗并移除该错误的高亮标注。
4. IF GrammarError 没有可用建议，THEN THE WritingScreen SHALL 仅显示错误说明而不显示建议列表。

---

### 需求 11：LLM 服务初始化

**用户故事：** 作为开发者，我希望 LLM 模型在应用启动时完成加载，以便各功能模块能正常调用。

#### 验收标准

1. WHEN 应用启动，THE LLMService SHALL 在后台线程异步加载本地 ONNX 模型文件。
2. WHILE LLMService 模型加载中，THE App SHALL 对依赖 AI 的功能显示不可用状态。
3. IF 模型文件加载失败，THEN THE App SHALL 记录错误日志并向用户显示"AI 功能暂不可用，请检查存储空间"。
4. WHEN 模型加载成功，THE LLMService SHALL 接受推理请求，且每次推理的 maxTokens 不超过 2048。

---

### 需求 12：侧边栏笔记管理

**用户故事：** 作为学习者，我希望通过侧边栏管理我的学习笔记和分组，以便更好地组织学习内容。

#### 验收标准

1. WHEN 用户向右滑动屏幕，THE App SHALL 显示侧边栏。
2. THE 侧边栏 SHALL 显示品牌名称、搜索按钮、设置按钮。
3. THE 侧边栏 SHALL 显示"新建笔记"按钮，点击后创建新笔记。
4. THE 侧边栏 SHALL 显示最近5个笔记，支持水平滚动查看更多。
5. THE 侧边栏 SHALL 显示笔记分组（2行网格布局），支持水平滚动。
6. THE 侧边栏 SHALL 显示单词学习日志，按时间分组（置顶、今天、本周、更早）。
7. WHEN 用户点击笔记，THE App SHALL 打开笔记详情页面。
8. WHEN 用户点击"+"按钮，THE App SHALL 创建新的笔记分组。

---

### 需求 13：主题切换

**用户故事：** 作为学习者，我希望选择不同的应用主题，以便个性化我的学习环境。

#### 验收标准

1. THE App SHALL 支持4种主题：白色、暗色、苹果绿、亮紫。
2. WHEN 用户进入主题选择页面，THE App SHALL 以2×2网格显示所有主题。
3. WHEN 用户点击主题卡片，THE App SHALL 实时预览该主题效果。
4. WHEN 用户确认选择，THE App SHALL 应用新主题并保存到 DataStore。
5. WHEN 应用重启，THE App SHALL 自动加载用户上次选择的主题。
6. THE 主题切换 SHALL 包含颜色过渡动画（300ms）。

---

### 需求 14：设置页面（我的页面）

**用户故事：** 作为学习者，我希望在设置页面查看我的学习数据和管理应用设置，以便了解学习进度和定制应用。

#### 验收标准

1. THE 设置页面 SHALL 显示用户头像和用户名，点击可编辑。
2. THE 设置页面 SHALL 显示水平滚动的选项卡（收藏、单词、写作、口语、个人简历）。
3. THE 设置页面 SHALL 显示英语学业水平卡片，包含：
   - 学习单词数
   - 写作水平评分
   - 口语水平评分
   - 语法水平评分
   - AI生成的学习建议
4. THE 设置页面 SHALL 显示功能设置列表：任务、主题色、朗读音色、模型设置、设置。
5. WHEN 用户点击功能设置项，THE App SHALL 导航到对应的设置子页面。
6. THE AI学习建议 SHALL 根据用户的学习数据每日自动更新。

---

### 需求 15：TTS 音色设置

**用户故事：** 作为学习者，我希望调整 TTS 朗读的音色、语速和音量，以便获得最佳的听力体验。

#### 验收标准

1. THE 朗读音色设置页面 SHALL 显示可用的 TTS 音色列表。
2. WHEN 用户选择音色，THE App SHALL 播放示例文本以预览效果。
3. THE 页面 SHALL 提供语速滑块（范围0.5x-2.0x，默认1.0x）。
4. THE 页面 SHALL 提供音量滑块（范围0-100%，默认100%）。
5. WHEN 用户调整设置，THE App SHALL 实时应用并保存到 DataStore。
6. THE 设置 SHALL 应用到所有 TTS 功能（单词发音、对话朗读）。

---

### 需求 16：AI 模型参数设置

**用户故事：** 作为高级用户，我希望调整 AI 模型的推理参数，以便控制生成内容的质量和风格。

#### 验收标准

1. THE 模型设置页面 SHALL 显示当前使用的模型名称和版本。
2. THE 页面 SHALL 提供 Temperature 滑块（范围0.0-2.0，默认0.7）。
3. THE 页面 SHALL 提供 Max Tokens 滑块（范围1-2048，默认512）。
4. THE 页面 SHALL 提供 Top P 滑块（范围0.0-1.0，默认0.9）。
5. WHEN 用户调整参数，THE App SHALL 显示参数说明和建议值。
6. WHEN 用户保存设置，THE App SHALL 验证参数有效性并保存到 DataStore。
7. THE 新参数 SHALL 应用到所有 LLM 推理请求。

---

### 需求 17：自动朗读设置

**用户故事：** 作为学习者，我希望设置每日自动朗读功能，以便在固定时间进行英语听力练习。

#### 验收标准

1. THE 通用设置页面 SHALL 显示自动朗读设置区域。
2. THE 区域 SHALL 包含3个开关：
   - 每日自动朗读英语文本
   - 每日自动朗读英语单词
   - 每日自动阅读英语语法
3. THE 区域 SHALL 提供日期选择（周一至周日多选按钮）。
4. THE 区域 SHALL 提供时间输入框和设置按钮。
5. WHEN 用户开启自动朗读，THE App SHALL 在设定时间触发 TTS 朗读。
6. WHEN 用户修改设置，THE App SHALL 更新定时任务并保存到 DataStore。
7. THE 自动朗读 SHALL 使用用户设置的 TTS 音色和语速。

---

### 需求 18：数据导入导出

**用户故事：** 作为学习者，我希望导出我的学习数据进行备份，并能在需要时导入恢复，以便保护我的学习成果。

#### 验收标准

1. THE 通用设置页面 SHALL 显示可折叠的"数据管理"区域。
2. THE 区域 SHALL 包含"清除缓存"按钮和并排的"导入数据"、"导出数据"按钮。
3. WHEN 用户点击"导出数据"，THE App SHALL 显示数据类型选择（单词、对话、作文、笔记）。
4. WHEN 用户选择数据类型和格式（JSON/CSV），THE App SHALL 导出数据到文件。
5. WHEN 用户点击"导入数据"，THE App SHALL 打开文件选择器。
6. WHEN 用户选择文件，THE App SHALL 验证文件格式并导入数据。
7. WHEN 用户点击"清除缓存"，THE App SHALL 显示确认对话框。
8. WHEN 用户确认清除，THE App SHALL 删除临时文件但保留学习数据。

---

### 需求 19：单词卡片滑动交互

**用户故事：** 作为学习者，我希望通过滑动手势快速标记单词，以便提高学习效率。

#### 验收标准

1. WHEN 用户左滑单词卡片超过30%屏幕宽度，THE App SHALL 标记单词为"未记住"。
2. WHEN 用户右滑单词卡片超过30%屏幕宽度，THE App SHALL 标记单词为"已记住"。
3. WHEN 滑动触发，THE 卡片 SHALL 执行飞出动画，新卡片从对侧进入。
4. WHEN 用户双击单词卡片，THE App SHALL 播放单词发音。
5. WHEN 用户长按单词卡片（500ms），THE App SHALL 收藏单词到单词本。
6. WHEN 收藏成功，THE App SHALL 显示"已收藏"提示并震动反馈。

---

### 需求 20：底部输入区域

**用户故事：** 作为学习者，我希望在各个学习页面使用统一的输入区域，以便方便地输入文本、拍照或上传文件。

#### 验收标准

1. THE 单词学习、口语训练、写作练习页面 SHALL 包含底部输入区域。
2. THE 输入区域 SHALL 包含4个组件：相机按钮、文本输入框、上传按钮、发送/停止按钮。
3. WHEN 用户点击相机按钮，THE App SHALL 请求相机权限并打开相机。
4. WHEN 用户在文本框输入内容，THE 发送按钮 SHALL 变为主题色。
5. WHEN 用户点击发送按钮，THE App SHALL 发送文本到对应功能（单词解释/对话/写作辅助）。
6. WHEN 用户点击上传按钮，THE App SHALL 打开文件选择器。
7. THE 输入框 SHALL 支持多行文本输入和自动换行。
