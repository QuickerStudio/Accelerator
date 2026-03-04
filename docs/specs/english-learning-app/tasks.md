# 实现计划：英语学习应用 (English Learning App)

## 概述

基于 Kotlin + Jetpack Compose + Room + Hilt + 本地 3B LLM (Qwen2.5-3B, kotlinllamacpp) + Whisper ASR + Android TTS 构建英语学习 Android 应用原型。采用**统一 LLM 架构 + 轻量级 Agent 系统**，通过系统提示词实现不同 AI 角色。按照"先跑起来、再逐步优化"的原则，依次完成基础架构 → 数据层 → AI 服务 → Agent 系统 → 各功能 UI → 导航集成。

**核心技术特性**：
- **统一 LLM**：Qwen2.5-3B-Instruct (Q4_K_M, ~2GB) 处理所有文本任务
- **Agent 系统**：5 个 AI 角色通过系统提示词实现（单词助手、语法检查、作文批改、口语陪练、学习规划）
- **完全离线**：所有 AI 功能本地运行，无需网络
- **现代架构**：MVVM + Clean Architecture + Jetpack Compose
- **自动切换**：Agent 角色根据页面自动切换（单词页→单词助手，口语页→口语陪练等）

**存储需求**：~2.6GB（Qwen2.5-3B: 2GB + Whisper Tiny: 75MB + 应用: 50MB + 用户数据: 500MB）

**系统要求**：Android 10+ (API 29) 最低，Android 14 (API 34) 推荐，6GB+ RAM, ARMv8-A 64位处理器

## 任务

- 1. 搭建项目基础架构
  - 在 `build.gradle.kts` 中配置 Kotlin、Compose、Room、Hilt、kotlinllamacpp (0.2.0)、Whisper、Coroutines、Timber、DataStore 等所有依赖
  - 配置 ARM 原生库支持（arm64-v8a）和 GGUF 模型加载
  - 创建 `util/Result.kt`，定义密封类 `Result<T>`（Success / Error）
  - 创建 `di/AppModule.kt`、`di/DatabaseModule.kt`、`di/DataStoreModule.kt`、`di/AIModule.kt` Hilt 模块骨架
  - 创建 `EnglishLearningApp.kt` Application 类，添加 `@HiltAndroidApp` 注解并初始化 Timber
  - 创建 `MainActivity.kt`，添加 `@AndroidEntryPoint` 注解
  - _Requirements: 11.1_

- 2. 实现数据模型与 Room 数据库
  - 2.1 创建领域模型实体
    - 创建 `domain/model/Word.kt`：`@Entity` + `WordStatus` + `DifficultyLevel` 枚举
    - 创建 `domain/model/Conversation.kt`：`Conversation`、`ConversationTurn`、`Role`、`ConversationStatus`、`SpeakingFeedback` 等数据类
    - 创建 `domain/model/Essay.kt`：`Essay`、`EssayFeedback`、`GrammarError`、`Suggestion`、`StyleComment` 等数据类
    - 创建 `domain/model/Note.kt`：`Note` 和 `NoteGroup` 实体（支持笔记管理和分组）
    - 创建 `domain/model/UserProgress.kt`：用户学习进度数据（单词数、对话数、作文数、连续天数等）
    - 创建 `domain/model/WordLearningLog.kt`：单词学习日志（记录学习、复习、收藏、发音等操作）
    - 创建 `domain/model/UserSettings.kt`：用户设置数据类（主题、TTS、模型参数、学习提醒、自动朗读、Agent 提示词等）
    - _Requirements: 1.4, 3.1, 7.3, 8.3, 9.3, 12.3, 12.4, 14.1, 16.1, 17.1_

  - 2.2 创建 DAO 和 Room 数据库
    - 创建 `data/local/database/WordDao.kt`：`getWordsByDate`、`updateWordStatus`、`getWordById`、`insertWords`、`getBookmarkedWords`
    - 创建 `data/local/database/ConversationDao.kt`：`insertConversation`、`insertTurn`、`getTurnsByConversationId`、`getAllConversations`
    - 创建 `data/local/database/EssayDao.kt`：`insertEssay`、`getEssayById`、`getAllEssays`、`deleteEssay`
    - 创建 `data/local/database/NoteDao.kt`：`insertNote`、`updateNote`、`deleteNote`、`getNotesByGroup`、`searchNotes`、`pinNote`
    - 创建 `data/local/database/NoteGroupDao.kt`：`insertGroup`、`updateGroup`、`deleteGroup`、`getAllGroups`
    - 创建 `data/local/database/UserProgressDao.kt`：`getUserProgress`、`updateProgress`、`incrementWordsLearned`、`updateStreak`
    - 创建 `data/local/database/WordLearningLogDao.kt`：`insertLog`、`getLogsByDate`、`getLogsByDateRange`、`getPinnedLogs`
    - 创建 `data/local/database/AppDatabase.kt`：`@Database` 注解，注册所有实体，提供 TypeConverters（LocalDate、枚举、Map 等）
    - _Requirements: 1.1, 3.1, 7.3, 8.3, 12.3, 12.4_

  - 2.3 实现 Repository 接口与实现类
    - 创建 `domain/repository/` 下接口：`WordRepository`、`ConversationRepository`、`EssayRepository`、`NoteRepository`、`NoteGroupRepository`、`UserProgressRepository`、`WordLearningLogRepository`、`UserSettingsRepository`
    - 创建 `data/repository/WordRepositoryImpl.kt`：实现接口，`updateWordStatus` 时若 status 为 LEARNED/MASTERED 则 reviewCount+1 并更新 updatedAt
    - 创建 `data/repository/ConversationRepositoryImpl.kt`、`EssayRepositoryImpl.kt`、`NoteRepositoryImpl.kt`、`NoteGroupRepositoryImpl.kt`
    - 创建 `data/repository/UserProgressRepositoryImpl.kt`：管理用户学习进度数据
    - 创建 `data/repository/WordLearningLogRepositoryImpl.kt`：记录单词学习日志
    - 创建 `data/local/datastore/UserSettingsDataStore.kt`：使用 DataStore 存储用户设置（替代 SharedPreferences）
    - 创建 `data/repository/UserSettingsRepositoryImpl.kt`：管理用户设置（主题、TTS、模型参数、Agent 提示词等）
    - 在 `di/DatabaseModule.kt` 和 `di/DataStoreModule.kt` 中绑定接口与实现
    - _Requirements: 1.1, 3.1, 7.3, 8.3, 12.3, 12.4, 14.1, 16.1, 17.1_

  - [ ] 2.4 为 WordRepository 编写属性测试
    - **属性 4：标记已学更新仓库状态** — 对任意有效 wordId，markWordAsLearned 后 status 应为 LEARNED
    - **属性 13：作文保存 Round-Trip** — saveEssay 后 getEssayById 取回的 content 应与保存时相同
    - **Validates: Requirements 3.1, 8.3**

- 3. 实现 AI 服务接口与 Mock 实现
  - 3.1 定义 AI 服务接口
    - 创建 `ai/llm/LLMService.kt`：`generateResponse`、`analyzeGrammar`、`reviewEssay`、`generateConversationResponse`
    - 创建 `ai/speech/SpeechRecognitionService.kt`：`transcribe`、`startListening`、`stopListening`
    - 创建 `ai/speech/TTSService.kt`：`speak`、`stop`、`setSpeed`、`setVoice`、`setVolume`
    - 创建 `ai/grammar/GrammarCheckerService.kt`：`checkGrammar`、`getSuggestions`
    - _Requirements: 4.1, 5.3, 6.1, 9.1, 11.1, 15.6_

  - 3.2 实现 Mock AI 服务（用于原型快速验证）
    - 创建 `ai/llm/MockLLMService.kt`：`generateResponse` 返回固定字符串，`generateConversationResponse` 返回带 mock feedback 的 AIResponse，`checkGrammar` 返回空列表
    - 创建 `ai/speech/MockASRService.kt`：`transcribe` 返回 `Result.Success("Hello, I am practicing English.")`，`startListening` 延迟 1s 后回调固定文本
    - 创建 `ai/speech/AndroidTTSService.kt`：使用 Android 系统 `TextToSpeech` 实现真实 TTS，支持音色、语速、音量设置
    - 在 `di/AIModule.kt` 中绑定 Mock 实现（后续替换为真实实现）
    - _Requirements: 4.1, 4.3, 5.3, 6.1, 11.2, 15.6_

  - 3.3 实现 LLM 服务真实骨架（异步加载）
    - 创建 `ai/llm/LLMServiceImpl.kt`：在 `init` 中于 `Dispatchers.IO` 异步加载 GGUF 模型，维护 `_isModelLoaded: StateFlow<Boolean>`
    - 模型未加载时所有推理方法返回 `Result.Error("Model not loaded")`
    - 在 `di/AIModule.kt` 中提供 `LLMServiceImpl`（可通过 flag 切换 Mock/Real）
    - _Requirements: 11.1, 11.2, 11.3, 11.4_

  - [ ] 3.4 为 LLM 服务编写属性测试
    - **属性 8：AI 回复非空性** — 对任意非空 userInput，模型已加载时 generateConversationResponse 返回 Success 且 text 非空
    - **属性 18：LLM 推理 maxTokens 约束** — 生成的 token 数量不超过 maxTokens（最大 2048）
    - **Validates: Requirements 6.1, 11.4**

- 4. 实现 Agent 系统
  - 4.1 创建 Agent 数据模型
    - 创建 `ai/agent/AgentRole.kt`：定义 5 个 Agent 角色枚举（VOCABULARY_TUTOR, GRAMMAR_CHECKER, ESSAY_REVIEWER, SPEAKING_PARTNER, LEARNING_PLANNER）
    - 每个角色包含：displayName、icon、defaultPrompt、defaultTemperature、defaultMaxTokens
    - 创建 `ai/agent/PromptMode.kt`：定义提示词模式枚举（PRESET, CUSTOM）
    - 创建 `ai/agent/Message.kt`：对话消息数据类（role, content）
    - _Requirements: 16.1, 16.2_

  - 4.2 实现 AgentService 接口和实现类
    - 创建 `ai/agent/AgentService.kt`：定义接口（getCurrentAgent, switchAgent, getCurrentPrompt, updateCustomPrompt, resetToPreset, generate）
    - 创建 `ai/agent/AgentServiceImpl.kt`：实现接口，管理当前 Agent 角色、提示词模式、自定义提示词
    - 实现 `generate` 方法：构建完整提示词（system + context + user），调用 LLMService
    - 实现提示词缓存机制（LruCache）
    - 在 `di/AIModule.kt` 中绑定 AgentService
    - _Requirements: 16.1, 16.2, 16.3, 16.4_

  - 4.3 实现 Agent 自动切换机制
    - 在 `ui/MainScreen.kt` 中监听导航状态变化
    - 根据当前页面自动切换 Agent 角色：
      - 单词页 → VOCABULARY_TUTOR
      - 口语页 → SPEAKING_PARTNER
      - 写作页 → ESSAY_REVIEWER（默认）或 GRAMMAR_CHECKER（语法检查时）
      - 设置页 → LEARNING_PLANNER
    - 保存 Agent 切换状态到 UserSettings
    - _Requirements: 16.1_

  - 4.4 扩展 UserSettings 支持 Agent 配置
    - 在 `domain/model/UserSettings.kt` 中添加：agentPrompts (Map<AgentRole, String>)、modelTemperature、modelMaxTokens、modelTopP
    - 在 `UserSettingsDataStore` 中实现 Agent 配置的持久化
    - 在 `UserSettingsRepository` 中添加：updateAgentPrompts、updateModelSettings 方法
    - _Requirements: 16.1, 16.6, 16.7_

- 5. 实现单词学习功能
  - 5.1 实现 Use Cases
    - 创建 `domain/usecase/GetWordListUseCase.kt`：调用 `WordRepository.getWordsByDate(date)`
    - 创建 `domain/usecase/LearnWordUseCase.kt`：调用 `WordRepository.updateWordStatus(wordId, LEARNED)`
    - 创建 `domain/usecase/BookmarkWordUseCase.kt`：调用 `WordRepository.updateWordStatus(wordId, BOOKMARKED)`
    - _Requirements: 1.1, 3.1, 19.6_

  - 5.2 实现 VocabularyViewModel
    - 创建 `ui/vocabulary/VocabularyViewModel.kt`：`uiState: StateFlow<VocabularyUiState>`（Loading / Success / Error）
    - 实现 `loadTodayWords()`、`markWordAsLearned(wordId)`、`markWordAsDifficult(wordId)`、`bookmarkWord(wordId)`、`playWordPronunciation(word)`
    - `playWordPronunciation` 调用 `TTSService.speak(word, "en-US")`
    - 集成 AgentService：使用 VOCABULARY_TUTOR 角色解释单词
    - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 3.1, 3.2, 3.3, 19.6_

  - [ ] 5.3 为 VocabularyViewModel 编写属性测试
    - **属性 1：单词列表加载一致性** — loadTodayWords 后 uiState 中的单词集合与仓库返回完全相同
    - **属性 5：学习进度计数正确性** — 显示的已学数量等于 status 为 LEARNED 的单词数量
    - **Validates: Requirements 1.1, 3.4**

  - 5.4 实现 VocabularyScreen UI
    - 创建 `ui/vocabulary/VocabularyScreen.kt`：根据 uiState 显示 Loading / Error / 单词卡片
    - 创建 `ui/vocabulary/components/WordCard.kt`：支持翻转动画、滑动手势（左滑未记住、右滑已记住）、双击播放发音、长按收藏
    - 实现滑动手势检测（超过30%屏幕宽度触发）
    - 实现卡片飞出和新卡片进入动画
    - 添加震动反馈和提示
    - 集成底部输入区域（用于单词解释）
    - 顶部显示进度条：已学数量 / 今日总数
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 2.1, 2.2, 3.2, 3.3, 3.4, 19.1, 19.2, 19.3, 19.4, 19.5, 19.6, 20.1_

  - [ ] 5.5 为 WordCard 编写属性测试
    - **属性 2：WordCard 内容完整性** — 对任意 Word，正面包含 word 和 phonetic，背面包含 translation 和 exampleSentence
    - **属性 3：WordCard 双击翻转回正面（Round-Trip）** — 翻转操作是自逆的
    - **Validates: Requirements 1.4, 1.6**

- 6. 检查点 — 单词学习功能可运行
  - 确保所有测试通过，单词卡片可正常展示、翻转、滑动、标记已学、播放发音、收藏，如有问题请向用户说明。

- 7. 实现 AI 口语训练功能
  - 7.1 实现口语训练 Use Cases
    - 创建 `domain/usecase/StartConversationUseCase.kt`：调用 AgentService（SPEAKING_PARTNER）生成开场白，创建 Conversation 并保存到 ConversationRepository
    - 创建 `domain/usecase/ProcessSpeechInputUseCase.kt`：接收 audioData，调用 ASRService.transcribe，再调用 AgentService.generate
    - _Requirements: 4.1, 5.3, 6.1_

  - 7.2 实现 SpeakingViewModel
    - 创建 `ui/speaking/SpeakingViewModel.kt`：`uiState: StateFlow<SpeakingUiState>`
    - 实现 `startConversation(topic)`：调用 AgentService（SPEAKING_PARTNER）生成开场白 → 添加到对话列表 → TTSService 朗读
    - 实现 `startRecording()`：请求麦克风权限，调用 `ASRService.startListening`，更新 isRecording = true
    - 实现 `stopRecording()`：调用 `ASRService.stopListening`，转录完成后调用 `processUserInput`
    - 实现 `processUserInput(text)`：添加 USER turn → 调用 AgentService → 添加 ASSISTANT turn → TTSService 朗读 → 保存到 ConversationRepository
    - _Requirements: 4.1, 4.2, 4.3, 5.1, 5.2, 5.3, 5.4, 5.5, 6.1, 6.2, 6.3, 6.4, 6.5, 7.3_

  - [ ] 7.3 为 SpeakingViewModel 编写属性测试
    - **属性 7：用户发言添加到对话列表** — 对任意非空转录文本，processUserInput 后对话列表新增 role=USER 的 turn
    - **属性 9：AI 回复添加到对话列表** — LLM 成功回复后对话列表新增 role=ASSISTANT 的 turn
    - **属性 11：对话历史持久化 Round-Trip** — 保存 ConversationTurn 后通过 conversationId 可取回完整内容
    - **Validates: Requirements 5.4, 6.2, 7.3**

  - 7.4 实现 SpeakingScreen UI
    - 创建 `ui/speaking/SpeakingScreen.kt`：顶部"开始对话"按钮，中部 `LazyColumn` 展示对话历史（区分 USER/ASSISTANT 气泡）
    - 创建 `ui/speaking/components/ConversationBubble.kt`：根据 role 左右对齐，显示 content 文本
    - 创建 `ui/speaking/components/RecordButton.kt`：按住变红色录音状态，松开触发 stopRecording
    - 集成底部输入区域（支持文本输入和语音输入）
    - LLM 生成中显示加载动画并禁用录音按钮；新消息添加后自动滚动到底部
    - _Requirements: 4.1, 4.2, 4.3, 5.1, 5.2, 5.4, 5.5, 6.3, 6.4, 6.5, 7.1, 7.2, 20.1_

  - [ ] 7.5 为对话历史渲染编写属性测试
    - **属性 6：语音识别触发完整性** — 对任意非空音频数据，stopRecording 后 ASRService.transcribe 被调用一次
    - **属性 10：对话历史渲染完整性** — SpeakingScreen 渲染后展示所有 ConversationTurn，角色标签与 turn.role 一致
    - **Validates: Requirements 5.3, 7.1**

- 8. 检查点 — 口语训练功能可运行
  - 确保所有测试通过，对话可正常启动、录音、转录、AI 回复、TTS 播放，如有问题请向用户说明。

- 9. 实现写作练习功能
  - 9.1 实现写作 Use Cases
    - 创建 `domain/usecase/CheckGrammarUseCase.kt`：调用 AgentService（GRAMMAR_CHECKER）检查语法
    - 创建 `domain/usecase/ReviewEssayUseCase.kt`：调用 AgentService（ESSAY_REVIEWER）批改作文
    - _Requirements: 9.1, 9.2_

  - 9.2 实现 WritingViewModel
    - 创建 `ui/writing/WritingViewModel.kt`：`uiState: StateFlow<WritingUiState>`（包含 content、grammarErrors、grammarScore、errorCount）
    - 实现 `onTextChanged(text)`：更新 content，防抖 500ms 后自动触发语法检查
    - 实现 `requestGrammarCheck()`：text 为空时提示"请先输入内容"；否则调用 AgentService（GRAMMAR_CHECKER），更新 grammarErrors 和 grammarScore
    - 实现 `requestFullReview()`：调用 AgentService（ESSAY_REVIEWER），更新 essayFeedback
    - 实现 `acceptSuggestion(suggestion)`：用 suggestion.text 替换 [startIndex, endIndex) 范围文本，从 grammarErrors 中移除该错误
    - 实现 `saveEssay()`：构建 Essay 对象并调用 EssayRepository.saveEssay
    - 实现语法评分算法（基于错误数量、句子复杂度、词汇丰富度）
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 9.1, 9.5, 9.6, 10.2, 10.3_

  - [ ] 9.3 为 WritingViewModel 编写属性测试
    - **属性 12：文本状态同步** — 对任意字符串，onTextChanged(text) 后 uiState.content 等于该字符串
    - **属性 16：采纳建议后文本替换正确性** — acceptSuggestion 后新文本 [startIndex, endIndex) 被替换为 suggestion.text，其余部分不变
    - **属性 17：采纳建议后错误从列表移除** — acceptSuggestion 后该 GrammarError 不再出现在 grammarErrors 中
    - **Validates: Requirements 8.2, 10.2, 10.3**

  - 9.4 实现 GrammarCheckerService 真实骨架
    - 创建 `ai/grammar/GrammarCheckerImpl.kt`：调用 AgentService（GRAMMAR_CHECKER），解析 JSON 响应为 GrammarError 列表，按 startIndex 排序，过滤索引越界的错误
    - 在 `di/AIModule.kt` 中绑定实现
    - _Requirements: 9.1, 9.3, 9.4_

  - [ ] 9.5 为 GrammarCheckerService 编写属性测试
    - **属性 14：语法错误索引有效性** — 对任意文本，返回的所有 GrammarError 满足 0 ≤ startIndex < endIndex ≤ text.length
    - **属性 15：语法错误列表有序性** — 返回的错误列表按 startIndex 升序排列
    - **Validates: Requirements 9.3, 9.4**

  - 9.6 实现 WritingScreen UI
    - 创建 `ui/writing/WritingScreen.kt`：顶部显示语法评分和错误数、标题输入框 + 全屏多行文本编辑区
    - 创建 `ui/writing/components/AnnotatedTextEditor.kt`：使用 `AnnotatedString` 对 grammarErrors 位置高亮标注（红色波浪下划线）
    - 创建 `ui/writing/components/ErrorDetailsDialog.kt`：点击错误标注弹出对话框，显示错误说明和建议列表，点击建议调用 `acceptSuggestion`
    - 集成底部输入区域（用于写作辅助）
    - 语法检查中禁用"检查语法"按钮并显示加载状态；全文审查结果显示在底部卡片
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 9.1, 9.2, 9.5, 9.6, 10.1, 10.2, 10.3, 10.4, 20.1_

- 10. 检查点 — 写作练习功能可运行
  - 确保所有测试通过，文本编辑、语法检查标注、语法评分、建议采纳、保存功能正常，如有问题请向用户说明。

- 11. 实现导航与主界面
  - 11.1 实现底部导航和 NavGraph
    - 创建 `ui/navigation/NavGraph.kt`：定义4个路由（vocabulary / speaking / writing / settings），使用 `NavHost` + `composable`
    - 创建 `ui/MainScreen.kt`：`Scaffold` + `NavigationBar`，底部4个 Tab（单词学习 / 口语训练 / 写作练习 / 设置），图标使用 Material Icons
    - 集成侧边栏组件，实现右滑手势打开
    - 在 `MainActivity.kt` 中设置 `setContent { MainScreen() }`
    - _Requirements: 1.1, 4.1, 8.1, 12.1, 14.5_

  - 11.2 实现 LLM 加载状态全局感知
    - 在 `MainScreen` 中观察 `LLMService._isModelLoaded`，模型加载中时在口语训练和写作练习 Tab 上显示不可用状态（灰色 + 提示文字）
    - 模型加载失败时显示 Snackbar："AI 功能暂不可用，请检查存储空间"
    - _Requirements: 11.2, 11.3_

  - 11.3 实现应用主题
    - 创建 `ui/theme/Theme.kt`：Material3 主题，支持4种主题（白色、暗色、苹果绿、亮紫）
    - 创建 `ui/theme/Color.kt`：定义4种主题的完整配色方案
    - 创建 `ui/theme/Type.kt`：定义字体样式
    - 在 MainScreen 中观察主题设置并应用
    - _Requirements: 1.4, 13.1, 13.5, 13.6_

- 12. 最终检查点 — 完整原型可运行
  - 确保所有测试通过，4个功能模块通过底部导航正常切换，侧边栏可正常打开，LLM 加载状态正确反映，如有问题请向用户说明。

- 13. 集成真实 LLM 模型（Qwen2.5-3B-Instruct）
  - 13.1 添加 kotlinllamacpp Android 依赖
    - 在 `build.gradle.kts` 中添加 kotlinllamacpp 库依赖（io.github.ljcamargo:llamacpp-kotlin:0.2.0）
    - 配置 ARM 原生库支持（arm64-v8a）
    - _Requirements: 11.1, 11.2_

  - 13.2 更新 LLMServiceImpl 使用 kotlinllamacpp
    - 替换 Mock 代码为 kotlinllamacpp 实现
    - 实现模型加载：从 assets 加载 GGUF 文件到缓存目录
    - 实现 `generateResponse`：使用 LlamaHelper 进行流式推理
    - 实现 `analyzeGrammar`：构建语法分析提示词
    - 实现 `reviewEssay`：构建作文审查提示词
    - 实现 `generateConversationResponse`：构建对话提示词
    - _Requirements: 11.1, 11.2, 11.3, 11.4_

  - 13.3 更新 AIModule 配置
    - 将 `USE_MOCK_LLM` 改为 `false`
    - 确保 LLMServiceImpl 被正确注入
    - _Requirements: 11.2_

  - 13.4 测试真实模型推理
    - 测试单词学习功能（TTS 发音、单词解释）
    - 测试口语训练功能（对话生成）
    - 测试写作练习功能（语法检查、作文审查）
    - 验证模型加载状态显示
    - 验证 Agent 自动切换机制
    - _Requirements: 11.1, 11.2, 11.3, 11.4_

- 14. 最终检查点 — 真实模型集成完成
  - 确保真实 LLM 模型可以正常加载和推理，所有功能模块使用真实 AI 服务，Agent 系统正常工作，如有问题请向用户说明。

## 备注

- 标有 `*` 的子任务为可选测试任务，可在 MVP 阶段跳过以加快进度
- Mock AI 服务（任务 3.2）让原型在无真实模型的情况下即可运行和验证 UI 交互
- 真实 LLM 推理（任务 3.3 和 12）使用 kotlinllamacpp 库在 Android 设备上直接运行 GGUF 模型（约 900MB）
- Whisper ASR 需要设备上存在对应模型文件（约 500MB）
- 属性测试使用 Kotest Property Testing 框架（`io.kotest:kotest-property`）
- 每个属性测试任务明确标注了对应的属性编号和需求条款，便于追溯


- 14. 实现主题系统
  - 14.1 创建主题数据模型和 DataStore
    - 创建 `ui/theme/AppTheme.kt`：定义 AppTheme 枚举（LIGHT, DARK, APPLE_GREEN, BRIGHT_PURPLE）
    - 创建 `data/local/preferences/ThemePreferences.kt`：使用 DataStore 存储主题选择
    - 在 `di/AppModule.kt` 中提供 DataStore 实例
    - _Requirements: 13.4, 13.5_

  - 14.2 实现主题颜色系统
    - 创建 `ui/theme/Color.kt`：定义4种主题的完整配色方案
    - 创建 `ui/theme/Theme.kt`：根据 AppTheme 枚举返回对应的 ColorScheme
    - 实现主题切换动画（颜色过渡300ms）
    - _Requirements: 13.1, 13.6_

  - 14.3 实现主题选择 UI
    - 创建 `ui/settings/ThemeSelectionScreen.kt`：2×2网格显示4种主题
    - 创建 `ui/settings/components/ThemeCard.kt`：主题预览卡片
    - 实现实时预览功能
    - 实现主题切换并保存到 DataStore
    - _Requirements: 13.2, 13.3, 13.4_

- 15. 实现设置页面（我的页面）
  - 15.1 创建用户设置数据模型
    - 创建 `domain/model/UserSettings.kt`：包含所有用户设置字段
    - 创建 `data/local/preferences/UserSettingsPreferences.kt`：使用 DataStore 存储
    - 创建 `domain/repository/UserSettingsRepository.kt` 接口和实现
    - _Requirements: 14.1, 14.2, 14.3, 14.4, 14.5_

  - 15.2 实现 SettingsViewModel
    - 创建 `ui/settings/SettingsViewModel.kt`
    - 实现用户信息管理（updateUsername, updateAvatar）
    - 实现选项卡数据加载（收藏、单词、写作、口语统计）
    - 实现 AI 学习建议生成（基于用户进度数据）
    - _Requirements: 14.1, 14.2, 14.3, 14.6_

  - 15.3 实现设置页面 UI
    - 创建 `ui/settings/SettingsScreen.kt`：用户信息区域、选项卡、学业水平卡片、功能设置列表
    - 创建 `ui/settings/components/UserInfoSection.kt`：头像和用户名编辑
    - 创建 `ui/settings/components/TabSection.kt`：水平滚动选项卡
    - 创建 `ui/settings/components/LevelCard.kt`：英语学业水平卡片
    - 创建 `ui/settings/components/SettingsListItem.kt`：功能设置列表项
    - _Requirements: 14.1, 14.2, 14.3, 14.4, 14.5_

- 16. 实现设置子页面
  - 16.1 实现 TTS 音色设置
    - 创建 `ui/settings/VoiceSettingsScreen.kt`
    - 实现音色列表和预览功能
    - 实现语速和音量滑块
    - 保存设置到 DataStore 并应用到 TTSService
    - _Requirements: 15.1, 15.2, 15.3, 15.4, 15.5, 15.6_

  - 16.2 实现 AI 模型参数设置
    - 创建 `ui/settings/ModelSettingsScreen.kt`
    - 实现 Temperature、Max Tokens、Top P 滑块
    - 显示参数说明和建议值
    - 验证参数有效性并保存到 DataStore
    - _Requirements: 16.1, 16.2, 16.3, 16.4, 16.5, 16.6, 16.7_

  - 16.3 实现通用设置
    - 创建 `ui/settings/GeneralSettingsScreen.kt`
    - 实现学习提醒设置（开关、目标、计划、统计）
    - 实现自动朗读设置（3个开关、日期选择、时间设置）
    - 实现权限管理（通知、麦克风、相机、存储）
    - 实现数据管理（清除缓存、导入导出）
    - _Requirements: 17.1, 17.2, 17.3, 17.4, 17.5, 17.6, 17.7, 18.1, 18.2, 18.3, 18.4, 18.5, 18.6, 18.7, 18.8_

- 17. 实现笔记管理功能
  - 17.1 创建笔记数据模型
    - 创建 `domain/model/Note.kt`：Note 和 NoteGroup 实体
    - 创建 `data/local/database/NoteDao.kt` 和 `NoteGroupDao.kt`
    - 创建 `domain/repository/NoteRepository.kt` 接口和实现
    - 在 AppDatabase 中注册笔记相关实体
    - _Requirements: 12.3, 12.4, 12.7_

  - 17.2 实现笔记 Use Cases
    - 创建 `domain/usecase/CreateNoteUseCase.kt`
    - 创建 `domain/usecase/CreateNoteGroupUseCase.kt`
    - 创建 `domain/usecase/SearchNotesUseCase.kt`
    - 创建 `domain/usecase/PinNoteUseCase.kt`
    - _Requirements: 12.3, 12.7, 12.8_

  - 17.3 实现 SidebarViewModel
    - 创建 `ui/sidebar/SidebarViewModel.kt`
    - 实现笔记列表加载和搜索
    - 实现笔记分组管理
    - 实现单词学习日志加载（按时间分组）
    - _Requirements: 12.2, 12.3, 12.4, 12.5, 12.6_

  - 17.4 实现侧边栏 UI
    - 创建 `ui/sidebar/Sidebar.kt`：侧边栏主界面
    - 创建 `ui/sidebar/components/BrandHeader.kt`：品牌区域
    - 创建 `ui/sidebar/components/NotesList.kt`：笔记列表（水平滚动）
    - 创建 `ui/sidebar/components/NoteGroupsGrid.kt`：笔记分组网格
    - 创建 `ui/sidebar/components/LearningLogsList.kt`：学习日志列表
    - 实现右滑手势打开侧边栏
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6_

- 18. 实现单词卡片滑动交互
  - 18.1 实现滑动手势检测
    - 在 `ui/vocabulary/components/WordCard.kt` 中添加 `pointerInput` 手势检测
    - 实现左滑/右滑触发逻辑（超过30%屏幕宽度）
    - 实现卡片飞出和新卡片进入动画
    - _Requirements: 19.1, 19.2, 19.3_

  - 18.2 实现双击和长按手势
    - 实现双击播放发音功能
    - 实现长按收藏功能（500ms）
    - 添加震动反馈和提示
    - _Requirements: 19.4, 19.5, 19.6_

  - 18.3 实现单词收藏功能
    - 创建 `domain/usecase/BookmarkWordUseCase.kt`
    - 在 VocabularyViewModel 中添加 `bookmarkWord(wordId)` 方法
    - 在 WordRepository 中添加 `getBookmarkedWords()` 方法
    - _Requirements: 19.6_

- 19. 实现底部输入区域
  - 19.1 创建通用底部输入组件
    - 创建 `ui/components/BottomInputArea.kt`：包含相机、文本框、上传、发送按钮
    - 实现多行文本输入和自动换行
    - 实现发送按钮状态变化（有内容时变为主题色）
    - _Requirements: 20.1, 20.2, 20.4, 20.5, 20.7_

  - 19.2 集成到各个页面
    - 在 VocabularyScreen 中集成底部输入区域（用于单词解释）
    - 在 SpeakingScreen 中集成底部输入区域（用于文本输入对话）
    - 在 WritingScreen 中集成底部输入区域（用于写作辅助）
    - _Requirements: 20.1_

  - 19.3 实现相机和文件上传
    - 实现相机按钮功能（请求权限、打开相机）
    - 实现上传按钮功能（打开文件选择器）
    - 处理拍照和文件选择结果
    - _Requirements: 20.3, 20.6_

- 20. 实现自动朗读功能
  - 20.1 创建定时任务管理器
    - 创建 `util/SchedulerManager.kt`：使用 WorkManager 管理定时任务
    - 实现每日定时触发逻辑
    - 实现日期和时间配置
    - _Requirements: 17.5, 17.6_

  - 20.2 实现自动朗读 Worker
    - 创建 `worker/AutoReadWorker.kt`：执行自动朗读任务
    - 根据设置选择朗读内容（文本/单词/语法）
    - 调用 TTSService 进行朗读
    - _Requirements: 17.5, 17.7_

  - 20.3 集成到设置页面
    - 在 GeneralSettingsScreen 中实现自动朗读设置 UI
    - 保存设置到 DataStore
    - 更新定时任务
    - _Requirements: 17.1, 17.2, 17.3, 17.4, 17.6_

- 21. 实现数据导入导出
  - 21.1 创建数据导出功能
    - 创建 `util/DataExporter.kt`：导出数据到 JSON/CSV 格式
    - 实现数据类型选择（单词、对话、作文、笔记）
    - 实现文件保存到外部存储
    - _Requirements: 18.3, 18.4_

  - 21.2 创建数据导入功能
    - 创建 `util/DataImporter.kt`：从 JSON/CSV 导入数据
    - 实现文件格式验证
    - 实现数据解析和插入数据库
    - _Requirements: 18.5, 18.6_

  - 21.3 实现清除缓存功能
    - 创建 `util/CacheManager.kt`：管理应用缓存
    - 实现清除临时文件（保留学习数据）
    - 显示清除的数据大小
    - _Requirements: 18.7, 18.8_

- 22. 更新导航和主界面
  - 22.1 更新底部导航栏
    - 修改 `ui/navigation/NavGraph.kt`：4个路由（单词、写作、对话、设置）
    - 修改 `ui/MainScreen.kt`：底部导航栏4个按钮
    - 更新导航图标和文字
    - _Requirements: 14.5_

  - 22.2 集成侧边栏
    - 在 MainScreen 中添加侧边栏组件
    - 实现右滑手势打开侧边栏
    - 实现侧边栏滑动动画
    - _Requirements: 12.1_

  - 22.3 更新主题应用
    - 在 MainScreen 中观察主题设置
    - 应用主题到整个应用
    - 实现主题切换动画
    - _Requirements: 13.5, 13.6_

- 23. 最终检查点 — 完整功能集成
  - 确保所有新功能正常工作：
    - 主题切换和持久化
    - 设置页面和子页面
    - 侧边栏笔记管理
    - 单词卡片滑动交互
    - 底部输入区域
    - 自动朗读功能
    - 数据导入导出
  - 确保所有测试通过
  - 如有问题请向用户说明

## 备注

- 标有 `*` 的子任务为可选测试任务，可在 MVP 阶段跳过以加快进度
- 任务 14-23 为新增功能，基于更新后的 UI 设计文档
- DataStore 替代 SharedPreferences 用于设置持久化
- 侧边栏使用右滑手势打开，提供更好的用户体验
- 主题系统支持4种预设主题，可实时预览和切换
- 自动朗读功能使用 WorkManager 实现定时任务
- 数据导入导出支持 JSON 和 CSV 格式
- 所有新功能都需要相应的单元测试和集成测试
