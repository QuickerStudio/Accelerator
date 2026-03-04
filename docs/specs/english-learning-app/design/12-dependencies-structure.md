# 依赖项与项目结构

## 核心依赖

```kotlin
// Kotlin和协程
implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

// Jetpack Compose
implementation("androidx.compose.ui:ui:1.5.4")
implementation("androidx.compose.material3:material3:1.1.2")
implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
implementation("androidx.activity:activity-compose:1.8.1")
implementation("androidx.navigation:navigation-compose:2.7.5")

// ViewModel和Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

// Room数据库
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Hilt依赖注入
implementation("com.google.dagger:hilt-android:2.48.1")
ksp("com.google.dagger:hilt-compiler:2.48.1")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// LLM推理（kotlinllamacpp for GGUF models）
implementation("io.github.ljcamargo:llamacpp-kotlin:0.2.0")

// Whisper模型（语音识别）
implementation("com.github.whispercpp:whisper.android:1.0.0")

// 音频处理
implementation("androidx.media3:media3-exoplayer:1.2.0")
implementation("androidx.media3:media3-ui:1.2.0")

// 图片加载
implementation("io.coil-kt:coil-compose:2.5.0")

// JSON解析
implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

// 日志
implementation("com.jakewharton.timber:timber:5.0.1")

// Core Android
implementation("androidx.core:core-ktx:1.12.0")
```

## 测试依赖

```kotlin
// 单元测试
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("app.cash.turbine:turbine:1.0.0")
testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
testImplementation("io.kotest:kotest-assertions-core:5.8.0")
testImplementation("io.kotest:kotest-property:5.8.0")

// Android测试
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
androidTestImplementation("com.google.dagger:hilt-android-testing:2.48.1")
kaptAndroidTest("com.google.dagger:hilt-compiler:2.48.1")
```

## 外部模型文件

### 需要下载的模型

**LLM模型**: Qwen2.5-1.5B-Instruct-GGUF（约900MB）
- 来源: Hugging Face Model Hub / ModelScope
- 格式: GGUF (Q4_K_M 量化)
- 量化: Q4_K_M（平衡质量和速度）
- 文件名: qwen2.5-1.5b-instruct-q4_k_m.gguf

**Whisper模型**: whisper-small（约500MB）
- 来源: OpenAI Whisper
- 格式: CoreML/GGUF
- 语言: 多语言支持

---

## 项目结构

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/englishlearning/
│   │   │   ├── ui/
│   │   │   │   ├── vocabulary/
│   │   │   │   │   ├── VocabularyScreen.kt
│   │   │   │   │   ├── VocabularyViewModel.kt
│   │   │   │   │   └── components/
│   │   │   │   ├── speaking/
│   │   │   │   │   ├── SpeakingPracticeScreen.kt
│   │   │   │   │   ├── SpeakingViewModel.kt
│   │   │   │   │   └── components/
│   │   │   │   ├── writing/
│   │   │   │   │   ├── WritingPracticeScreen.kt
│   │   │   │   │   ├── WritingViewModel.kt
│   │   │   │   │   └── components/
│   │   │   │   ├── navigation/
│   │   │   │   │   └── NavGraph.kt
│   │   │   │   └── theme/
│   │   │   │       └── Theme.kt
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Word.kt
│   │   │   │   │   ├── Conversation.kt
│   │   │   │   │   └── Essay.kt
│   │   │   │   ├── usecase/
│   │   │   │   │   ├── LearnWordUseCase.kt
│   │   │   │   │   ├── GetWordListUseCase.kt
│   │   │   │   │   ├── StartConversationUseCase.kt
│   │   │   │   │   ├── ProcessSpeechInputUseCase.kt
│   │   │   │   │   ├── CheckGrammarUseCase.kt
│   │   │   │   │   └── ReviewEssayUseCase.kt
│   │   │   │   └── repository/
│   │   │   │       ├── WordRepository.kt
│   │   │   │       ├── ConversationRepository.kt
│   │   │   │       └── EssayRepository.kt
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   ├── WordDao.kt
│   │   │   │   │   │   ├── ConversationDao.kt
│   │   │   │   │   │   └── EssayDao.kt
│   │   │   │   │   └── preferences/
│   │   │   │   │       └── UserPreferences.kt
│   │   │   │   └── repository/
│   │   │   │       ├── WordRepositoryImpl.kt
│   │   │   │       ├── ConversationRepositoryImpl.kt
│   │   │   │       └── EssayRepositoryImpl.kt
│   │   │   ├── ai/
│   │   │   │   ├── llm/
│   │   │   │   │   ├── LLMService.kt
│   │   │   │   │   ├── LLMServiceImpl.kt
│   │   │   │   │   └── ModelLoader.kt
│   │   │   │   ├── speech/
│   │   │   │   │   ├── SpeechRecognitionService.kt
│   │   │   │   │   ├── WhisperService.kt
│   │   │   │   │   └── TTSService.kt
│   │   │   │   └── grammar/
│   │   │   │       ├── GrammarCheckerService.kt
│   │   │   │       └── GrammarCheckerImpl.kt
│   │   │   ├── di/
│   │   │   │   ├── AppModule.kt
│   │   │   │   ├── DatabaseModule.kt
│   │   │   │   └── AIModule.kt
│   │   │   └── util/
│   │   │       ├── Result.kt
│   │   │       ├── Extensions.kt
│   │   │       └── Constants.kt
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   └── drawable/
│   │   └── assets/
│   │       └── models/
│   │           ├── qwen2.5-1.5b-instruct.gguf
│   │           ├── INTEGRATION_GUIDE.md
│   │           └── whisper_small.gguf
│   └── test/
│       └── java/com/example/englishlearning/
│           ├── viewmodel/
│           ├── usecase/
│           ├── repository/
│           └── ai/
└── build.gradle.kts
```

---

## 实现路线图

### 阶段 1: 基础架构（2周）
- 搭建项目结构和依赖配置
- 实现数据库层（Room + DAO）
- 实现Repository层
- 配置Hilt依赖注入
- 实现基础UI框架和导航

### 阶段 2: 单词学习功能（1周）
- 实现单词数据模型和数据库表
- 实现单词学习Use Cases
- 开发单词学习UI（卡片、列表、进度）
- 集成TTS实现单词发音
- 实现间隔重复算法

### 阶段 3: LLM集成（2周）
- 集成 kotlinllamacpp 库
- 实现模型加载和推理逻辑（GGUF格式）
- 优化推理性能（ARM优化、流式输出）
- 实现提示词工程
- 测试模型推理准确性和速度

### 阶段 4: 口语训练功能（2周）
- 集成Whisper语音识别
- 实现音频录制和播放
- 开发对话UI和交互逻辑
- 实现对话管理和历史记录
- 集成LLM生成对话回复
- 实现发音和流利度评分

### 阶段 5: 写作练习功能（2周）
- 开发文本编辑器UI
- 实现语法检查服务
- 实现错误标记和建议显示
- 集成LLM进行全文审查
- 实现作文保存和历史管理

### 阶段 6: 优化和测试（2周）
- 性能优化（推理速度、内存、电池）
- 编写单元测试和集成测试
- UI/UX优化和动画
- 错误处理和边界情况
- 安全加固和数据加密

### 阶段 7: 发布准备（1周）
- 完整的端到端测试
- 文档编写
- 应用图标和启动画面
- Google Play发布准备
- 用户手册和帮助文档

**总计**: 约12周（3个月）
