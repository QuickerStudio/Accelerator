# 项目文件目录结构

## 概述

本文档详细描述了英语学习应用的完整文件目录结构，遵循 Clean Architecture 和 MVVM 架构模式，采用模块化设计。

## 完整目录结构

```
EnglishLearningApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/quickerstudio/englishlearning/
│   │   │   │   ├── EnglishLearningApplication.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   │   │
│   │   │   │   ├── ui/                              # UI Layer (Presentation)
│   │   │   │   │   ├── MainScreen.kt
│   │   │   │   │   │
│   │   │   │   │   ├── vocabulary/                  # 单词学习模块
│   │   │   │   │   │   ├── VocabularyScreen.kt
│   │   │   │   │   │   ├── VocabularyViewModel.kt
│   │   │   │   │   │   ├── VocabularyUiState.kt
│   │   │   │   │   │   ├── VocabularyEvent.kt
│   │   │   │   │   │   ├── WordBookScreen.kt
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       ├── WordCard.kt
│   │   │   │   │   │       ├── SwipeableWordCard.kt
│   │   │   │   │   │       ├── WordProgressBar.kt
│   │   │   │   │   │       ├── WordListItem.kt
│   │   │   │   │   │       ├── SwipeHintText.kt
│   │   │   │   │   │       ├── WordPhonetic.kt
│   │   │   │   │   │       ├── WordDefinition.kt
│   │   │   │   │   │       ├── WordExample.kt
│   │   │   │   │   │       └── BookmarkButton.kt
│   │   │   │   │   │
│   │   │   │   │   ├── speaking/                    # AI口语训练模块
│   │   │   │   │   │   ├── SpeakingPracticeScreen.kt
│   │   │   │   │   │   ├── SpeakingViewModel.kt
│   │   │   │   │   │   ├── SpeakingUiState.kt
│   │   │   │   │   │   ├── SpeakingEvent.kt
│   │   │   │   │   │   ├── ConversationHistoryScreen.kt
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       ├── ChatBubble.kt
│   │   │   │   │   │       ├── AIChatBubble.kt
│   │   │   │   │   │       ├── UserChatBubble.kt
│   │   │   │   │   │       ├── RecordButton.kt
│   │   │   │   │   │       ├── ConversationList.kt
│   │   │   │   │   │       ├── FeedbackCard.kt
│   │   │   │   │   │       ├── LoadingBubble.kt
│   │   │   │   │   │       ├── VoiceWaveAnimation.kt
│   │   │   │   │   │       └── TopicSelector.kt
│   │   │   │   │   │
│   │   │   │   │   ├── writing/                     # 写作练习模块
│   │   │   │   │   │   ├── WritingPracticeScreen.kt
│   │   │   │   │   │   ├── WritingViewModel.kt
│   │   │   │   │   │   ├── WritingUiState.kt
│   │   │   │   │   │   ├── WritingEvent.kt
│   │   │   │   │   │   ├── EssayHistoryScreen.kt
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       ├── TextEditor.kt
│   │   │   │   │   │       ├── GrammarErrorHighlight.kt
│   │   │   │   │   │       ├── ErrorDetailDialog.kt
│   │   │   │   │   │       ├── GrammarScoreCard.kt
│   │   │   │   │   │       ├── ErrorCountBadge.kt
│   │   │   │   │   │       ├── SuggestionChip.kt
│   │   │   │   │   │       ├── WritingToolbar.kt
│   │   │   │   │   │       └── ReviewPanel.kt
│   │   │   │   │   │
│   │   │   │   │   ├── settings/                    # 设置页面模块（我的页面）
│   │   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   │   ├── SettingsViewModel.kt
│   │   │   │   │   │   ├── SettingsUiState.kt
│   │   │   │   │   │   ├── SettingsEvent.kt
│   │   │   │   │   │   ├── VoiceSettingsScreen.kt
│   │   │   │   │   │   ├── ModelSettingsScreen.kt
│   │   │   │   │   │   ├── GeneralSettingsScreen.kt
│   │   │   │   │   │   ├── ThemeSelectionScreen.kt
│   │   │   │   │   │   ├── TasksScreen.kt
│   │   │   │   │   │   ├── EditProfileScreen.kt
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       ├── UserInfoCard.kt
│   │   │   │   │   │       ├── StatsTabRow.kt
│   │   │   │   │   │       ├── StatsTabItem.kt
│   │   │   │   │   │       ├── LevelCard.kt
│   │   │   │   │   │       ├── AIAdviceSection.kt
│   │   │   │   │   │       ├── SettingItem.kt
│   │   │   │   │   │       ├── ThemePreviewCard.kt
│   │   │   │   │   │       ├── SliderWithLabel.kt
│   │   │   │   │   │       ├── VoiceSelector.kt
│   │   │   │   │   │       ├── ModelParameterSlider.kt
│   │   │   │   │   │       ├── DayOfWeekSelector.kt
│   │   │   │   │   │       ├── TimePickerField.kt
│   │   │   │   │   │       └── DataManagementSection.kt
│   │   │   │   │   │
│   │   │   │   │   ├── sidebar/                     # 侧边栏模块（笔记和日志）
│   │   │   │   │   │   ├── Sidebar.kt
│   │   │   │   │   │   ├── SidebarViewModel.kt
│   │   │   │   │   │   ├── SidebarUiState.kt
│   │   │   │   │   │   ├── SidebarEvent.kt
│   │   │   │   │   │   ├── NoteDetailScreen.kt
│   │   │   │   │   │   ├── NoteEditorScreen.kt
│   │   │   │   │   │   ├── NoteGroupScreen.kt
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       ├── BrandHeader.kt
│   │   │   │   │   │       ├── CreateNoteButton.kt
│   │   │   │   │   │       ├── NoteCard.kt
│   │   │   │   │   │       ├── NoteListItem.kt
│   │   │   │   │   │       ├── NoteGroupGrid.kt
│   │   │   │   │   │       ├── NoteGroupItem.kt
│   │   │   │   │   │       ├── AddGroupButton.kt
│   │   │   │   │   │       ├── LearningLogSection.kt
│   │   │   │   │   │       ├── LogCategoryHeader.kt
│   │   │   │   │   │       ├── LogItem.kt
│   │   │   │   │   │       └── SidebarScrim.kt
│   │   │   │   │   │
│   │   │   │   │   ├── note/                        # 笔记管理模块
│   │   │   │   │   │   ├── NoteViewModel.kt
│   │   │   │   │   │   ├── NoteUiState.kt
│   │   │   │   │   │   └── NoteEvent.kt
│   │   │   │   │   │
│   │   │   │   │   ├── navigation/                  # 导航
│   │   │   │   │   │   ├── NavGraph.kt
│   │   │   │   │   │   ├── Screen.kt
│   │   │   │   │   │   ├── NavigationActions.kt
│   │   │   │   │   │   └── BottomNavigationBar.kt
│   │   │   │   │   │
│   │   │   │   │   ├── theme/                       # 主题系统（4种主题）
│   │   │   │   │   │   ├── Theme.kt
│   │   │   │   │   │   ├── Color.kt
│   │   │   │   │   │   ├── LightTheme.kt
│   │   │   │   │   │   ├── DarkTheme.kt
│   │   │   │   │   │   ├── AppleGreenTheme.kt
│   │   │   │   │   │   ├── BrightPurpleTheme.kt
│   │   │   │   │   │   ├── Type.kt
│   │   │   │   │   │   ├── Shape.kt
│   │   │   │   │   │   ├── Spacing.kt
│   │   │   │   │   │   └── AppTheme.kt
│   │   │   │   │   │
│   │   │   │   │   └── common/                      # 通用组件
│   │   │   │   │       ├── AppButton.kt
│   │   │   │   │       ├── AppCard.kt
│   │   │   │   │       ├── AppTopBar.kt
│   │   │   │   │       ├── BottomInputArea.kt
│   │   │   │   │       ├── LoadingIndicator.kt
│   │   │   │   │       ├── ErrorView.kt
│   │   │   │   │       ├── EmptyState.kt
│   │   │   │   │       ├── ProgressBar.kt
│   │   │   │   │       ├── IconButton.kt
│   │   │   │   │       ├── TextField.kt
│   │   │   │   │       ├── Dialog.kt
│   │   │   │   │       ├── BottomSheet.kt
│   │   │   │   │       ├── SwipeableCard.kt
│   │   │   │   │       └── AnimatedVisibilityWrapper.kt
│   │   │   │   │
│   │   │   │   ├── domain/                          # Domain Layer (Business Logic)
│   │   │   │   │   ├── model/                       # 数据模型
│   │   │   │   │   │   ├── Word.kt
│   │   │   │   │   │   ├── WordStatus.kt
│   │   │   │   │   │   ├── DifficultyLevel.kt
│   │   │   │   │   │   ├── Conversation.kt
│   │   │   │   │   │   ├── ConversationTurn.kt
│   │   │   │   │   │   ├── ConversationStatus.kt
│   │   │   │   │   │   ├── Essay.kt
│   │   │   │   │   │   ├── EssayStatus.kt
│   │   │   │   │   │   ├── EssayFeedback.kt
│   │   │   │   │   │   ├── GrammarError.kt
│   │   │   │   │   │   ├── Note.kt
│   │   │   │   │   │   ├── NoteGroup.kt
│   │   │   │   │   │   ├── UserSettings.kt
│   │   │   │   │   │   ├── UserProgress.kt
│   │   │   │   │   │   ├── WordLearningLog.kt
│   │   │   │   │   │   ├── AppTheme.kt
│   │   │   │   │   │   └── DayOfWeek.kt
│   │   │   │   │   │
│   │   │   │   │   ├── usecase/                     # 业务用例
│   │   │   │   │   │   ├── word/
│   │   │   │   │   │   │   ├── LearnWordUseCase.kt
│   │   │   │   │   │   │   ├── GetWordListUseCase.kt
│   │   │   │   │   │   │   ├── GetTodayWordsUseCase.kt
│   │   │   │   │   │   │   ├── MarkWordAsLearnedUseCase.kt
│   │   │   │   │   │   │   ├── MarkWordAsDifficultUseCase.kt
│   │   │   │   │   │   │   ├── BookmarkWordUseCase.kt
│   │   │   │   │   │   │   ├── PlayWordPronunciationUseCase.kt
│   │   │   │   │   │   │   ├── GetBookmarkedWordsUseCase.kt
│   │   │   │   │   │   │   └── UpdateWordStatusUseCase.kt
│   │   │   │   │   │   ├── conversation/
│   │   │   │   │   │   │   ├── StartConversationUseCase.kt
│   │   │   │   │   │   │   ├── ProcessSpeechInputUseCase.kt
│   │   │   │   │   │   │   ├── GenerateAIResponseUseCase.kt
│   │   │   │   │   │   │   ├── SaveConversationUseCase.kt
│   │   │   │   │   │   │   ├── GetConversationHistoryUseCase.kt
│   │   │   │   │   │   │   ├── EvaluatePronunciationUseCase.kt
│   │   │   │   │   │   │   └── StopConversationUseCase.kt
│   │   │   │   │   │   ├── essay/
│   │   │   │   │   │   │   ├── CheckGrammarUseCase.kt
│   │   │   │   │   │   │   ├── ReviewEssayUseCase.kt
│   │   │   │   │   │   │   ├── SaveEssayUseCase.kt
│   │   │   │   │   │   │   ├── GetEssayHistoryUseCase.kt
│   │   │   │   │   │   │   ├── CalculateGrammarScoreUseCase.kt
│   │   │   │   │   │   │   └── AcceptSuggestionUseCase.kt
│   │   │   │   │   │   ├── note/
│   │   │   │   │   │   │   ├── CreateNoteUseCase.kt
│   │   │   │   │   │   │   ├── UpdateNoteUseCase.kt
│   │   │   │   │   │   │   ├── DeleteNoteUseCase.kt
│   │   │   │   │   │   │   ├── GetNoteByIdUseCase.kt
│   │   │   │   │   │   │   ├── GetAllNotesUseCase.kt
│   │   │   │   │   │   │   ├── CreateNoteGroupUseCase.kt
│   │   │   │   │   │   │   ├── SearchNotesUseCase.kt
│   │   │   │   │   │   │   └── PinNoteUseCase.kt
│   │   │   │   │   │   ├── settings/
│   │   │   │   │   │   │   ├── UpdateUserSettingsUseCase.kt
│   │   │   │   │   │   │   ├── UpdateThemeUseCase.kt
│   │   │   │   │   │   │   ├── UpdateTTSSettingsUseCase.kt
│   │   │   │   │   │   │   ├── UpdateModelSettingsUseCase.kt
│   │   │   │   │   │   │   ├── UpdateLearningSettingsUseCase.kt
│   │   │   │   │   │   │   ├── UpdateAutoReadSettingsUseCase.kt
│   │   │   │   │   │   │   └── GenerateLearningAdviceUseCase.kt
│   │   │   │   │   │   ├── progress/
│   │   │   │   │   │   │   ├── GetUserProgressUseCase.kt
│   │   │   │   │   │   │   ├── UpdateProgressUseCase.kt
│   │   │   │   │   │   │   ├── IncrementWordsLearnedUseCase.kt
│   │   │   │   │   │   │   ├── UpdateStreakUseCase.kt
│   │   │   │   │   │   │   └── CalculateLevelsUseCase.kt
│   │   │   │   │   │   ├── log/
│   │   │   │   │   │   │   ├── LogWordActionUseCase.kt
│   │   │   │   │   │   │   ├── GetLogsByDateUseCase.kt
│   │   │   │   │   │   │   ├── GetLogsByDateRangeUseCase.kt
│   │   │   │   │   │   │   └── GetPinnedLogsUseCase.kt
│   │   │   │   │   │   └── data/
│   │   │   │   │   │       ├── ExportDataUseCase.kt
│   │   │   │   │   │       ├── ImportDataUseCase.kt
│   │   │   │   │   │       └── ClearCacheUseCase.kt
│   │   │   │   │   │
│   │   │   │   │   └── repository/                  # Repository 接口
│   │   │   │   │       ├── WordRepository.kt
│   │   │   │   │       ├── ConversationRepository.kt
│   │   │   │   │       ├── EssayRepository.kt
│   │   │   │   │       ├── NoteRepository.kt
│   │   │   │   │       ├── NoteGroupRepository.kt
│   │   │   │   │       ├── UserSettingsRepository.kt
│   │   │   │   │       ├── UserProgressRepository.kt
│   │   │   │   │       └── WordLearningLogRepository.kt
│   │   │   │   │
│   │   │   │   ├── data/                            # Data Layer
│   │   │   │   │   ├── local/                       # 本地数据源
│   │   │   │   │   │   ├── database/
│   │   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   │   ├── WordDao.kt
│   │   │   │   │   │   │   │   ├── ConversationDao.kt
│   │   │   │   │   │   │   │   ├── ConversationTurnDao.kt
│   │   │   │   │   │   │   │   ├── EssayDao.kt
│   │   │   │   │   │   │   │   ├── NoteDao.kt
│   │   │   │   │   │   │   │   ├── NoteGroupDao.kt
│   │   │   │   │   │   │   │   ├── UserProgressDao.kt
│   │   │   │   │   │   │   │   └── WordLearningLogDao.kt
│   │   │   │   │   │   │   └── entity/
│   │   │   │   │   │   │       ├── WordEntity.kt
│   │   │   │   │   │   │       ├── ConversationEntity.kt
│   │   │   │   │   │   │       ├── ConversationTurnEntity.kt
│   │   │   │   │   │   │       ├── EssayEntity.kt
│   │   │   │   │   │   │       ├── NoteEntity.kt
│   │   │   │   │   │   │       ├── NoteGroupEntity.kt
│   │   │   │   │   │   │       ├── UserProgressEntity.kt
│   │   │   │   │   │   │       └── WordLearningLogEntity.kt
│   │   │   │   │   │   │
│   │   │   │   │   │   ├── datastore/               # DataStore (设置持久化)
│   │   │   │   │   │   │   ├── UserSettingsSerializer.kt
│   │   │   │   │   │   │   └── UserSettingsDataStore.kt
│   │   │   │   │   │   │
│   │   │   │   │   │   └── file/                    # 文件存储
│   │   │   │   │   │       ├── FileManager.kt
│   │   │   │   │   │       └── AudioFileManager.kt
│   │   │   │   │   │
│   │   │   │   │   ├── repository/                  # Repository 实现
│   │   │   │   │   │   ├── WordRepositoryImpl.kt
│   │   │   │   │   │   ├── ConversationRepositoryImpl.kt
│   │   │   │   │   │   ├── EssayRepositoryImpl.kt
│   │   │   │   │   │   ├── NoteRepositoryImpl.kt
│   │   │   │   │   │   ├── NoteGroupRepositoryImpl.kt
│   │   │   │   │   │   ├── UserSettingsRepositoryImpl.kt
│   │   │   │   │   │   ├── UserProgressRepositoryImpl.kt
│   │   │   │   │   │   └── WordLearningLogRepositoryImpl.kt
│   │   │   │   │   │
│   │   │   │   │   └── mapper/                      # 数据映射器
│   │   │   │   │       ├── WordMapper.kt
│   │   │   │   │       ├── ConversationMapper.kt
│   │   │   │   │       ├── EssayMapper.kt
│   │   │   │   │       └── NoteMapper.kt
│   │   │   │   │
│   │   │   │   ├── ai/                              # AI Services Layer
│   │   │   │   │   ├── agent/                       # Agent 系统
│   │   │   │   │   │   ├── AgentService.kt
│   │   │   │   │   │   ├── AgentServiceImpl.kt
│   │   │   │   │   │   ├── AgentRole.kt
│   │   │   │   │   │   ├── PromptMode.kt
│   │   │   │   │   │   ├── PromptCache.kt
│   │   │   │   │   │   └── Message.kt
│   │   │   │   │   │
│   │   │   │   │   ├── llm/                         # 大语言模型
│   │   │   │   │   │   ├── LLMService.kt
│   │   │   │   │   │   ├── LLMServiceImpl.kt
│   │   │   │   │   │   ├── ModelLoader.kt
│   │   │   │   │   │   ├── ModelConfig.kt
│   │   │   │   │   │   ├── PromptTemplate.kt
│   │   │   │   │   │   ├── PromptBuilder.kt
│   │   │   │   │   │   ├── TokenCounter.kt
│   │   │   │   │   │   ├── ResponseParser.kt
│   │   │   │   │   │   └── StreamingHandler.kt
│   │   │   │   │   │
│   │   │   │   │   ├── speech/                      # 语音服务
│   │   │   │   │   │   ├── SpeechRecognitionService.kt
│   │   │   │   │   │   ├── WhisperService.kt
│   │   │   │   │   │   ├── WhisperServiceImpl.kt
│   │   │   │   │   │   ├── TTSService.kt
│   │   │   │   │   │   ├── TTSServiceImpl.kt
│   │   │   │   │   │   ├── AudioRecorder.kt
│   │   │   │   │   │   ├── AudioPlayer.kt
│   │   │   │   │   │   ├── AudioProcessor.kt
│   │   │   │   │   │   ├── VoiceConfig.kt
│   │   │   │   │   │   └── PronunciationEvaluator.kt
│   │   │   │   │   │
│   │   │   │   │   └── grammar/                     # 语法检查
│   │   │   │   │       ├── GrammarCheckerService.kt
│   │   │   │   │       ├── GrammarCheckerImpl.kt
│   │   │   │   │       ├── GrammarRuleEngine.kt
│   │   │   │   │       ├── ErrorDetector.kt
│   │   │   │   │       ├── SuggestionGenerator.kt
│   │   │   │   │       └── ScoreCalculator.kt
│   │   │   │   │
│   │   │   │   ├── worker/                          # 后台任务（WorkManager）
│   │   │   │   │   ├── AutoReadWorker.kt
│   │   │   │   │   ├── AutoReadScheduler.kt
│   │   │   │   │   ├── DataSyncWorker.kt
│   │   │   │   │   ├── ReminderWorker.kt
│   │   │   │   │   ├── WorkerScheduler.kt
│   │   │   │   │   └── WorkerFactory.kt
│   │   │   │   │
│   │   │   │   ├── di/                              # 依赖注入
│   │   │   │   │   ├── AppModule.kt
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   ├── DataStoreModule.kt
│   │   │   │   │   ├── RepositoryModule.kt
│   │   │   │   │   ├── UseCaseModule.kt
│   │   │   │   │   ├── AIModule.kt
│   │   │   │   │   └── WorkerModule.kt
│   │   │   │   │
│   │   │   │   └── util/                            # 工具类
│   │   │   │       ├── Result.kt
│   │   │   │       ├── Resource.kt
│   │   │   │       ├── UiState.kt
│   │   │   │       ├── Extensions.kt
│   │   │   │       ├── StringExtensions.kt
│   │   │   │       ├── FlowExtensions.kt
│   │   │   │       ├── ComposeExtensions.kt
│   │   │   │       ├── Constants.kt
│   │   │   │       ├── DateTimeUtil.kt
│   │   │   │       ├── FileUtil.kt
│   │   │   │       ├── ValidationUtil.kt
│   │   │   │       ├── PermissionUtil.kt
│   │   │   │       ├── NetworkUtil.kt
│   │   │   │       └── LogUtil.kt
│   │   │   │
│   │   │   ├── res/                                 # 资源文件
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── themes.xml
│   │   │   │   │   ├── dimens.xml
│   │   │   │   │   └── styles.xml
│   │   │   │   ├── values-zh-rCN/
│   │   │   │   │   └── strings.xml
│   │   │   │   ├── drawable/
│   │   │   │   ├── mipmap/
│   │   │   │   └── xml/
│   │   │   │       └── data_store_preferences.xml
│   │   │   │
│   │   │   ├── assets/                              # 资产文件
│   │   │   │   ├── models/
│   │   │   │   │   ├── qwen2.5-1.5b-instruct.gguf
│   │   │   │   │   ├── whisper_small.gguf
│   │   │   │   │   └── INTEGRATION_GUIDE.md
│   │   │   │   └── data/
│   │   │   │       └── sample_words.json
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   ├── test/                                    # 单元测试
│   │   │   └── java/com/quickerstudio/englishlearning/
│   │   │       ├── viewmodel/
│   │   │       │   ├── VocabularyViewModelTest.kt
│   │   │       │   ├── SpeakingViewModelTest.kt
│   │   │       │   ├── WritingViewModelTest.kt
│   │   │       │   └── SettingsViewModelTest.kt
│   │   │       ├── usecase/
│   │   │       │   ├── LearnWordUseCaseTest.kt
│   │   │       │   ├── CheckGrammarUseCaseTest.kt
│   │   │       │   └── ExportDataUseCaseTest.kt
│   │   │       ├── repository/
│   │   │       │   ├── WordRepositoryTest.kt
│   │   │       │   ├── ConversationRepositoryTest.kt
│   │   │       │   └── EssayRepositoryTest.kt
│   │   │       └── ai/
│   │   │           ├── LLMServiceTest.kt
│   │   │           ├── TTSServiceTest.kt
│   │   │           └── GrammarCheckerTest.kt
│   │   │
│   │   └── androidTest/                             # UI测试
│   │       └── java/com/quickerstudio/englishlearning/
│   │           ├── ui/
│   │           │   ├── VocabularyScreenTest.kt
│   │           │   ├── SpeakingScreenTest.kt
│   │           │   ├── WritingScreenTest.kt
│   │           │   └── SettingsScreenTest.kt
│   │           └── database/
│   │               └── AppDatabaseTest.kt
│   │
│   ├── build.gradle.kts                             # 应用级构建配置
│   └── proguard-rules.pro                           # 混淆规则
│
├── buildSrc/                                        # 构建脚本
│   ├── src/main/kotlin/
│   │   ├── Dependencies.kt
│   │   └── Versions.kt
│   └── build.gradle.kts
│
├── gradle/                                          # Gradle配置
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
│
├── build.gradle.kts                                 # 项目级构建配置
├── settings.gradle.kts                              # 项目设置
├── gradle.properties                                # Gradle属性
├── gradlew                                          # Gradle包装器(Unix)
├── gradlew.bat                                      # Gradle包装器(Windows)
├── .gitignore
└── README.md
```

## 目录说明

### UI Layer (ui/)
负责用户界面展示和用户交互，使用 Jetpack Compose 构建。

- **vocabulary/**: 单词学习模块，包含单词卡片、滑动交互等
- **speaking/**: AI口语训练模块，包含对话界面、录音功能等
- **writing/**: 写作练习模块，包含文本编辑器、语法检查等
- **settings/**: 设置页面模块，包含用户设置、主题选择等
- **sidebar/**: 侧边栏模块，包含笔记管理、学习日志等
- **navigation/**: 导航管理，包含路由定义和底部导航栏
- **theme/**: 主题系统，包含颜色、字体、形状定义
- **common/**: 通用UI组件，可在多个模块中复用

### Domain Layer (domain/)
包含业务逻辑和领域模型，不依赖于具体的框架和实现。

- **model/**: 领域模型，定义核心业务实体
  - 单词相关：Word, WordStatus, DifficultyLevel
  - 对话相关：Conversation, ConversationTurn, ConversationStatus
  - 作文相关：Essay, EssayStatus, EssayFeedback, GrammarError
  - 笔记相关：Note, NoteGroup
  - 设置相关：UserSettings, AppTheme, DayOfWeek
  - 进度相关：UserProgress, WordLearningLog

- **usecase/**: 业务用例，封装单一业务功能
  - word/: 单词学习相关用例
  - conversation/: 对话相关用例
  - essay/: 作文相关用例
  - note/: 笔记管理相关用例
  - data/: 数据导入导出用例

- **repository/**: Repository 接口定义，数据访问抽象层

### Data Layer (data/)
负责数据的获取、存储和管理。

- **local/database/**: Room 数据库
  - dao/: 数据访问对象，定义数据库操作
  - entity/: 数据库实体，对应数据库表

- **local/datastore/**: DataStore 配置
  - 用于存储用户设置，替代 SharedPreferences
  - 支持类型安全和协程

- **local/file/**: 文件存储管理
  - 管理用户头像、音频文件等

- **repository/**: Repository 接口实现
  - 协调本地数据源
  - 实现数据缓存策略

- **mapper/**: 数据映射器
  - Entity ↔ Domain Model 转换

### AI Services Layer (ai/)
提供 AI 相关服务。

- **agent/**: Agent 系统
  - AgentService: Agent 管理接口
  - AgentServiceImpl: Agent 服务实现
  - AgentRole: Agent 角色枚举（5种角色）
  - PromptMode: 提示词模式（预设/自定义）
  - PromptCache: 提示词缓存
  - Message: 消息数据类

- **llm/**: 大语言模型服务
  - LLMService: 模型推理接口
  - ModelLoader: 模型加载和管理
  - PromptTemplate: 提示词模板
  - PromptBuilder: 提示词构建器

- **speech/**: 语音服务
  - SpeechRecognitionService: 语音识别（Whisper）
  - TTSService: 文本转语音
  - AudioRecorder: 音频录制

- **grammar/**: 语法检查服务
  - GrammarCheckerService: 语法检查接口
  - GrammarRuleEngine: 语法规则引擎

### Worker Layer (worker/)
后台任务管理，使用 WorkManager。

- **AutoReadWorker**: 自动朗读定时任务
- **DataSyncWorker**: 数据同步任务
- **WorkerScheduler**: 任务调度器

### Dependency Injection (di/)
使用 Hilt 进行依赖注入。

- **AppModule**: 应用级依赖
- **DatabaseModule**: 数据库依赖
- **DataStoreModule**: DataStore 依赖
- **RepositoryModule**: Repository 依赖
- **UseCaseModule**: UseCase 依赖
- **AIModule**: AI 服务依赖
- **WorkerModule**: Worker 依赖

### Utilities (util/)
工具类和扩展函数。

- **Result.kt**: 统一的结果封装类
- **Resource.kt**: 资源状态封装
- **Extensions.kt**: Kotlin 扩展函数
- **Constants.kt**: 常量定义
- **DateTimeUtil.kt**: 日期时间工具
- **FileUtil.kt**: 文件操作工具
- **ValidationUtil.kt**: 数据验证工具

### Resources (res/)
Android 资源文件。

- **values/**: 默认资源（英文）
- **values-zh-rCN/**: 中文资源
- **drawable/**: 图片资源
- **mipmap/**: 应用图标
- **xml/**: XML 配置文件

### Assets (assets/)
应用资产文件。

- **models/**: AI 模型文件
  - qwen2.5-1.5b-instruct.gguf: LLM 模型
  - whisper_small.gguf: 语音识别模型

- **data/**: 初始数据
  - sample_words.json: 示例单词数据

### Test Directories
测试代码组织。

- **test/**: 单元测试
  - viewmodel/: ViewModel 测试
  - usecase/: UseCase 测试
  - repository/: Repository 测试
  - ai/: AI 服务测试

- **androidTest/**: UI 测试和集成测试
  - ui/: Compose UI 测试
  - database/: 数据库测试

## 模块依赖关系

```
UI Layer (ui/)
    ↓ depends on
ViewModel Layer (ui/*ViewModel.kt)
    ↓ depends on
Domain Layer (domain/usecase/)
    ↓ depends on
Repository Interface (domain/repository/)
    ↑ implemented by
Data Layer (data/repository/)
    ↓ depends on
Data Sources (data/local/)

AI Services (ai/)
    ↑ used by
Use Cases (domain/usecase/)
```

## 命名规范

### 文件命名
- **Screen**: `*Screen.kt` (例如: VocabularyScreen.kt)
- **ViewModel**: `*ViewModel.kt` (例如: VocabularyViewModel.kt)
- **UseCase**: `*UseCase.kt` (例如: LearnWordUseCase.kt)
- **Repository**: `*Repository.kt` (接口) / `*RepositoryImpl.kt` (实现)
- **DAO**: `*Dao.kt` (例如: WordDao.kt)
- **Entity**: `*Entity.kt` (例如: WordEntity.kt)
- **Service**: `*Service.kt` (接口) / `*ServiceImpl.kt` (实现)

### 包命名
- 使用小写字母
- 使用复数形式表示集合 (例如: components, usecases)
- 按功能模块组织 (例如: vocabulary, speaking, writing)

## 代码组织原则

1. **单一职责**: 每个类只负责一个功能
2. **依赖倒置**: 高层模块依赖抽象，不依赖具体实现
3. **模块化**: 按功能模块组织代码，便于维护和测试
4. **可测试性**: 使用依赖注入，便于编写单元测试
5. **清晰的分层**: UI → ViewModel → UseCase → Repository → DataSource

## 构建配置

### buildSrc/
集中管理依赖版本和配置。

- **Dependencies.kt**: 定义所有依赖库
- **Versions.kt**: 定义版本号

### build.gradle.kts
- 项目级: 配置插件和仓库
- 应用级: 配置依赖、编译选项、构建类型

## 版本控制

### .gitignore
忽略以下文件：
- build/ 目录
- .gradle/ 目录
- local.properties
- *.iml
- .idea/ (部分文件)
- assets/models/*.gguf (AI 模型文件过大)

## 下一步

参考此目录结构创建项目骨架，然后按照 [tasks.md](../tasks.md) 中的任务顺序逐步实现功能。


## 功能模块详细说明（基于 UI 设计）

### 1. 单词学习模块 (vocabulary/)

**设计思路**：卡片式学习，支持滑动交互和语音播报

**核心组件**：
- `VocabularyScreen.kt`: 主屏幕，包含顶部栏、单词卡片、底部输入区域
- `VocabularyViewModel.kt`: 管理单词列表、学习状态、TTS 播放
- `SwipeableWordCard.kt`: 可滑动的单词卡片
  - 左滑：标记"未记住"
  - 右滑：标记"已记住"
  - 双击：播放发音
  - 长按：收藏到单词本

**状态管理**：
```kotlin
data class VocabularyUiState(
    val currentWord: Word?,
    val wordList: List<Word>,
    val currentIndex: Int,
    val isPlaying: Boolean,
    val isBookmarked: Boolean,
    val showSwipeHint: Boolean
)

sealed class VocabularyEvent {
    data class SwipeLeft(val wordId: String) : VocabularyEvent()
    data class SwipeRight(val wordId: String) : VocabularyEvent()
    data class DoubleTap(val word: String) : VocabularyEvent()
    data class LongPress(val wordId: String) : VocabularyEvent()
    data class SendMessage(val message: String) : VocabularyEvent()
}
```

**交互流程**：
1. 加载今日单词列表
2. 显示当前单词卡片
3. 用户滑动/点击交互
4. 更新单词状态
5. 显示下一张卡片
6. 记录学习日志

### 2. AI 口语训练模块 (speaking/)

**设计思路**：对话式界面，支持语音输入和 AI 回复

**核心组件**：
- `SpeakingPracticeScreen.kt`: 对话界面，包含消息列表和输入区域
- `SpeakingViewModel.kt`: 管理对话状态、语音识别、AI 生成
- `ChatBubble.kt`: 消息气泡（AI 和用户）
- `RecordButton.kt`: 录音按钮，支持按住说话
- `VoiceWaveAnimation.kt`: 语音波形动画

**状态管理**：
```kotlin
data class SpeakingUiState(
    val conversationId: String?,
    val messages: List<ConversationTurn>,
    val isRecording: Boolean,
    val isProcessing: Boolean,
    val isAISpeaking: Boolean,
    val currentTopic: String?
)

sealed class SpeakingEvent {
    object StartConversation : SpeakingEvent()
    object StartRecording : SpeakingEvent()
    object StopRecording : SpeakingEvent()
    data class SendTextMessage(val text: String) : SpeakingEvent()
    object StopConversation : SpeakingEvent()
}
```

**交互流程**：
1. 开始对话（可选主题）
2. 用户录音或输入文本
3. 语音识别转文本
4. AI 生成回复
5. TTS 播放 AI 回复
6. 显示发音评估反馈
7. 保存对话历史

### 3. 写作练习模块 (writing/)

**设计思路**：全屏编辑器，实时语法检测和评分

**核心组件**：
- `WritingPracticeScreen.kt`: 写作界面，包含编辑器和评分卡片
- `WritingViewModel.kt`: 管理文本内容、语法检查、AI 审查
- `TextEditor.kt`: 多行文本编辑器
- `GrammarErrorHighlight.kt`: 语法错误高亮显示
- `ErrorDetailDialog.kt`: 错误详情弹窗
- `GrammarScoreCard.kt`: 语法评分卡片（0-100分）

**状态管理**：
```kotlin
data class WritingUiState(
    val essayId: String?,
    val title: String,
    val content: String,
    val wordCount: Int,
    val grammarScore: Int,
    val errors: List<GrammarError>,
    val isChecking: Boolean,
    val isReviewing: Boolean
)

sealed class WritingEvent {
    data class OnTextChanged(val text: String) : WritingEvent()
    object RequestGrammarCheck : WritingEvent()
    object RequestFullReview : WritingEvent()
    data class AcceptSuggestion(val errorId: String, val suggestion: String) : WritingEvent()
    data class SaveEssay(val title: String) : WritingEvent()
}
```

**交互流程**：
1. 用户输入文本
2. 实时语法检查（防抖）
3. 显示错误标注
4. 点击错误查看详情
5. 接受建议修改
6. 请求 AI 全文审查
7. 显示评分和建议
8. 保存作文

### 4. 设置页面模块 (settings/)

**设计思路**：个人中心，展示学习数据和功能设置

**核心组件**：
- `SettingsScreen.kt`: 设置主页面
- `SettingsViewModel.kt`: 管理用户信息、学习数据、设置项
- `UserInfoCard.kt`: 用户信息卡片（头像、用户名）
- `StatsTabRow.kt`: 水平滚动的统计选项卡
- `LevelCard.kt`: 英语学业水平卡片
- `AIAdviceSection.kt`: AI 学习建议区域
- `SettingItem.kt`: 设置项列表项

**子页面**：
- `VoiceSettingsScreen.kt`: TTS 音色设置
- `ModelSettingsScreen.kt`: AI 模型参数设置
- `GeneralSettingsScreen.kt`: 通用设置（学习提醒、自动朗读）
- `ThemeSelectionScreen.kt`: 主题选择（4种主题）

**状态管理**：
```kotlin
data class SettingsUiState(
    val userInfo: UserInfo,
    val stats: LearningStats,
    val settings: UserSettings,
    val aiAdvice: String,
    val isLoading: Boolean
)

data class LearningStats(
    val favoriteCount: Int,
    val wordCount: Int,
    val writingScore: Int,
    val speakingScore: Int,
    val grammarScore: Int
)

sealed class SettingsEvent {
    data class UpdateUsername(val name: String) : SettingsEvent()
    data class UpdateAvatar(val path: String) : SettingsEvent()
    data class SelectTheme(val theme: AppTheme) : SettingsEvent()
    data class UpdateTTSSettings(val voice: String, val speed: Float) : SettingsEvent()
    data class UpdateModelSettings(val temperature: Float, val maxTokens: Int) : SettingsEvent()
    object ExportData : SettingsEvent()
    object ImportData : SettingsEvent()
    object ClearCache : SettingsEvent()
}
```

**AI 学习建议生成**：
```kotlin
fun generateLearningAdvice(stats: LearningStats): String {
    val suggestions = buildList {
        if (stats.wordCount < 100) add("建议每日学习10-15个新单词")
        if (stats.writingScore < 70) add("需要加强写作练习")
        if (stats.speakingScore < 70) add("建议每日进行15分钟对话练习")
        if (stats.grammarScore < 70) add("语法基础需要巩固")
    }
    return suggestions.joinToString("。") + "。"
}
```

### 5. 侧边栏模块 (sidebar/)

**设计思路**：抽屉式面板，笔记管理和学习日志

**核心组件**：
- `Sidebar.kt`: 侧边栏容器，支持手势滑动
- `SidebarViewModel.kt`: 管理笔记、分组、学习日志
- `BrandHeader.kt`: 顶部品牌区域（Accelerator）
- `CreateNoteButton.kt`: 新建笔记按钮（渐变背景）
- `NoteCard.kt`: 笔记卡片（水平滚动列表）
- `NoteGroupGrid.kt`: 笔记分组网格（2行）
- `LearningLogSection.kt`: 学习日志区域（按时间分组）

**状态管理**：
```kotlin
data class SidebarUiState(
    val isOpen: Boolean,
    val notes: List<Note>,
    val noteGroups: List<NoteGroup>,
    val learningLogs: Map<LogCategory, List<WordLearningLog>>,
    val isLoading: Boolean
)

sealed class SidebarEvent {
    object Open : SidebarEvent()
    object Close : SidebarEvent()
    object CreateNote : SidebarEvent()
    data class OpenNote(val noteId: String) : SidebarEvent()
    data class OpenNoteGroup(val groupId: String) : SidebarEvent()
    object CreateNoteGroup : SidebarEvent()
    data class SearchNotes(val query: String) : SidebarEvent()
}

enum class LogCategory {
    PINNED,    // 📌 置顶
    TODAY,     // 📅 今天
    THIS_WEEK, // 📅 本周
    EARLIER    // 📅 更早
}
```

**手势交互**：
- 从左边缘向右滑动：打开侧边栏
- 向左滑动或点击遮罩：关闭侧边栏
- 滑动距离 > 30% 屏幕宽度：自动展开/收回

### 6. 主题系统模块 (theme/)

**设计思路**：4种预设主题，实时预览和切换

**主题配置**：
```kotlin
// 1. 白色主题 (LIGHT)
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6366F1),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F5F5)
)

// 2. 暗色主题 (DARK)
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6366F1),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

// 3. 苹果绿主题 (APPLE_GREEN)
val AppleGreenColorScheme = darkColorScheme(
    primary = Color(0xFF34C759),
    background = Color(0xFF1C1C1E),
    surface = Color(0xFF2C2C2E)
)

// 4. 亮紫主题 (BRIGHT_PURPLE)
val BrightPurpleColorScheme = darkColorScheme(
    primary = Color(0xFF8B5CF6),
    background = Color(0xFF1A1A2E),
    surface = Color(0xFF16213E)
)
```

**主题选择界面**：
- 2×2 网格布局
- 每个主题卡片显示预览
- 点击切换主题（300ms 动画）
- 使用 DataStore 持久化

### 7. Agent 系统模块 (ai/agent/)

**设计思路**: 轻量级 Agent 架构，通过系统提示词实现不同角色

**核心组件**:
- `AgentService.kt`: Agent 管理服务接口
- `AgentServiceImpl.kt`: Agent 服务实现
- `AgentRole.kt`: Agent 角色枚举
- `PromptMode.kt`: 提示词模式枚举
- `PromptCache.kt`: 提示词缓存

**5 个 Agent 角色**:
1. **单词学习助手** (VocabularyTutor)
   - 解释单词、提供例句、记忆技巧
   - Temperature: 0.7, Max Tokens: 512
   
2. **语法检查助手** (GrammarChecker)
   - 检查语法错误、给出修改建议
   - Temperature: 0.3, Max Tokens: 1024
   
3. **作文批改老师** (EssayReviewer)
   - 批改作文、评分、给出改进建议
   - Temperature: 0.5, Max Tokens: 2048
   
4. **口语陪练伙伴** (SpeakingPartner)
   - 对话练习、纠正错误、鼓励表达
   - Temperature: 0.8, Max Tokens: 512
   
5. **学习规划师** (LearningPlanner)
   - 分析学习状态、制定学习计划
   - Temperature: 0.5, Max Tokens: 1024

**状态管理**:
```kotlin
data class AgentState(
    val currentAgent: AgentRole,
    val promptMode: PromptMode,
    val customPrompt: String,
    val isLoading: Boolean
)

enum class AgentRole(
    val displayName: String,
    val icon: String,
    val defaultPrompt: String,
    val defaultTemperature: Float,
    val defaultMaxTokens: Int
) {
    VOCABULARY_TUTOR("单词学习助手", "📚", "...", 0.7f, 512),
    GRAMMAR_CHECKER("语法检查助手", "🔍", "...", 0.3f, 1024),
    ESSAY_REVIEWER("作文批改老师", "✍️", "...", 0.5f, 2048),
    SPEAKING_PARTNER("口语陪练伙伴", "💬", "...", 0.8f, 512),
    LEARNING_PLANNER("学习规划师", "📊", "...", 0.5f, 1024)
}

enum class PromptMode {
    PRESET,   // 预设提示词
    CUSTOM    // 自定义提示词
}
```

**核心方法**:
```kotlin
interface AgentService {
    fun getCurrentAgent(): AgentRole
    suspend fun switchAgent(agent: AgentRole): Result<Unit>
    fun getCurrentPrompt(): String
    suspend fun updateCustomPrompt(prompt: String): Result<Unit>
    suspend fun resetToPreset(): Result<Unit>
    suspend fun generate(
        userInput: String,
        context: List<Message> = emptyList()
    ): Result<String>
}
```

**使用流程**:
1. 根据页面自动选择 Agent 角色
2. 构建系统提示词
3. 调用 LLM 生成回复
4. 解析和返回结果

**优势**:
- 单一模型，多种角色
- 角色切换即时（无需重新加载）
- 用户可自定义提示词
- 易于扩展新角色

参考详细文档：[design/13-agent-system.md](13-agent-system.md)

---

## 通用组件模块 (common/)

**底部输入区域 (BottomInputArea)**：
```kotlin
@Composable
fun BottomInputArea(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onCameraClick: () -> Unit,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 相机按钮 + 文本输入框 + 上传按钮 + 发送/停止按钮
}
```

**可滑动卡片 (SwipeableCard)**：
```kotlin
@Composable
fun SwipeableCard(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onDoubleTap: () -> Unit,
    onLongPress: () -> Unit,
    content: @Composable () -> Unit
) {
    // 支持左右滑动、双击、长按手势
}
```

## MVVM 架构实现

### ViewModel 模式
每个功能模块都遵循 MVVM 模式：

```kotlin
class VocabularyViewModel @Inject constructor(
    private val getWordListUseCase: GetWordListUseCase,
    private val markWordAsLearnedUseCase: MarkWordAsLearnedUseCase,
    private val playWordPronunciationUseCase: PlayWordPronunciationUseCase,
    private val bookmarkWordUseCase: BookmarkWordUseCase
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(VocabularyUiState())
    val uiState: StateFlow<VocabularyUiState> = _uiState.asStateFlow()
    
    // Event Handler
    fun onEvent(event: VocabularyEvent) {
        when (event) {
            is VocabularyEvent.SwipeLeft -> handleSwipeLeft(event.wordId)
            is VocabularyEvent.SwipeRight -> handleSwipeRight(event.wordId)
            is VocabularyEvent.DoubleTap -> handleDoubleTap(event.word)
            is VocabularyEvent.LongPress -> handleLongPress(event.wordId)
            is VocabularyEvent.SendMessage -> handleSendMessage(event.message)
        }
    }
    
    // Private handlers
    private fun handleSwipeLeft(wordId: String) {
        viewModelScope.launch {
            // 标记为未记住
            // 记录日志
            // 显示下一张卡片
        }
    }
    
    // ... 其他处理方法
}
```

### 单向数据流
```
User Action → Event → ViewModel → UseCase → Repository → DataSource
                ↓
            UI State ← ViewModel ← Result
```

### 状态管理最佳实践
1. 使用 `StateFlow` 管理 UI 状态
2. 使用 `sealed class` 定义事件
3. 使用 `data class` 定义状态
4. ViewModel 不持有 Context 引用
5. 使用 Hilt 进行依赖注入

## 模块间通信

### 导航
```kotlin
sealed class Screen(val route: String) {
    object Vocabulary : Screen("vocabulary")
    object Speaking : Screen("speaking")
    object Writing : Screen("writing")
    object Settings : Screen("settings")
    data class NoteDetail(val noteId: String) : Screen("note/{noteId}")
    data class WordBook(val filter: String) : Screen("wordbook/{filter}")
}
```

### 共享数据
- 使用 Repository 层共享数据
- 使用 DataStore 共享设置
- 使用 Room 数据库共享持久化数据

### 事件总线
- 使用 SharedFlow 处理一次性事件
- 使用 Channel 处理命令式事件

## 性能优化

### UI 层优化
1. 使用 `remember` 缓存计算结果
2. 使用 `derivedStateOf` 优化派生状态
3. 使用 `LazyColumn` 实现虚拟滚动
4. 避免在 Composable 中执行耗时操作

### 数据层优化
1. 使用 Room 数据库索引
2. 使用 DataStore 替代 SharedPreferences
3. 实现分页加载
4. 使用缓存策略

### AI 服务优化
1. 模型预加载
2. 推理结果缓存
3. 流式输出
4. 后台线程执行

## 测试策略

### 单元测试
- ViewModel 测试：测试状态变化和事件处理
- UseCase 测试：测试业务逻辑
- Repository 测试：测试数据访问

### UI 测试
- Compose UI 测试：测试用户交互
- 截图测试：测试 UI 一致性

### 集成测试
- 数据库测试：测试 Room 操作
- AI 服务测试：测试模型推理

## 下一步实现建议

### 阶段 1：基础架构（任务 1-5）
1. 创建项目结构
2. 配置依赖注入
3. 实现数据模型和数据库
4. 实现 Repository 层
5. 实现基础 UI 框架

### 阶段 2：核心功能（任务 6-13）
1. 实现单词学习模块
2. 实现 AI 口语训练模块
3. 实现写作练习模块
4. 集成 AI 服务

### 阶段 3：扩展功能（任务 14-23）
1. 实现设置页面
2. 实现侧边栏
3. 实现主题系统
4. 实现数据导入导出
5. 实现自动朗读功能

参考 [tasks.md](../tasks.md) 获取详细的任务列表和实现顺序。
