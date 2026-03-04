# AI Module - English Learning Assistant

AI 模块是应用的核心，提供基于大语言模型的英语学习辅助功能。

## 架构概览

```
ai/
├── llm/                    # LLM 推理引擎（基于官方 MediaPipe）
│   ├── InferenceEngine.kt  # 统一推理引擎
│   ├── InferenceConfig.kt  # 推理配置
│   └── ...
│
├── agent/                  # Agent 系统（唯一对话入口）
│   ├── AgentService.kt     # Agent 服务接口
│   ├── AgentServiceImpl.kt # Agent 服务实现
│   ├── AgentRole.kt        # Agent 角色定义
│   └── ...
│
├── downloader/            # 模型下载管理
│   ├── DManager.kt        # 下载管理器
│   ├── DEngine.kt         # 下载引擎
│   └── ...
│
├── session/               # 会话管理
│   ├── Session.kt         # 会话数据模型
│   └── SessionManager.kt  # 会话管理器
│
├── history/               # 对话历史
│   ├── ConversationHistory.kt
│   └── HistoryManager.kt
│
└── model/                 # 模型配置
    └── ModelConfig.kt     # 模型状态管理
```

## 核心流程

### 1. 模型下载流程

```
用户点击下载 → DManager → DEngine → 下载模型文件
                    ↓
              ModelConfig 记录状态
```

### 2. 模型初始化流程

```
用户点击加载 → InferenceEngine.getInstance()
                    ↓
              创建 LlmInference (Engine)
                    ↓
              创建 LlmInferenceSession (Session)
                    ↓
              ModelConfig 标记为已初始化
```

### 3. 对话流程（核心）

```
UI 层 (WritingScreen, SpeakingScreen, etc.)
    ↓
AgentService.switchAgent(role)  # 切换角色
    ↓
AgentService.generate(input, context)  # 生成响应
    ↓
InferenceEngine.generateSync(prompt)  # 推理
    ↓
MediaPipe LLM  # 官方库
    ↓
返回响应 → UI 层显示
```

### 4. 会话管理流程

```
创建会话 → SessionManager.createSession()
    ↓
添加消息 → SessionManager.addMessage()
    ↓
保存会话 → HistoryManager.saveConversation()
```

## 核心原则

### 1. AgentService 是唯一入口

**所有 AI 对话必须通过 AgentService**，这样才能实现角色扮演：

```kotlin
// ✅ 正确：通过 AgentService
val agentService = AgentServiceImpl(context)
agentService.switchAgent(AgentRole.GRAMMAR_CHECKER)
val response = agentService.generate(userInput, context)

// ❌ 错误：直接调用推理引擎
val engine = InferenceEngine.getInstance(context, config)
val response = engine.generateSync(prompt)  // 绕过了 Agent，无法角色扮演
```

### 2. 统一推理引擎

只有一个推理引擎：`InferenceEngine`（基于官方 MediaPipe 架构）

- 所有推理都通过 `InferenceEngine`
- AgentService 内部使用 `InferenceEngine`
- UI 层不应该直接访问 `InferenceEngine`

### 3. 官方架构对齐

使用 MediaPipe 官方的两层架构：

```
InferenceEngine
    ├── LlmInference (Engine 层)
    └── LlmInferenceSession (Session 层)
```

## 使用示例

### 语法检查（WritingScreen）

```kotlin
val agentService = AgentServiceImpl(context)

// 切换到语法检查角色
agentService.switchAgent(AgentRole.GRAMMAR_CHECKER)

// 生成响应
val result = agentService.generate(
    userInput = "I goes to school yesterday",
    context = emptyList()
)
```

### 对话练习（SpeakingScreen）

```kotlin
val agentService = AgentServiceImpl(context)

// 切换到对话伙伴角色
agentService.switchAgent(AgentRole.SPEAKING_PARTNER)

// 生成响应
val result = agentService.generate(
    userInput = "How are you today?",
    context = conversationHistory
)
```

### 作文评审（WritingScreen）

```kotlin
val agentService = AgentServiceImpl(context)

// 切换到作文评审角色
agentService.switchAgent(AgentRole.ESSAY_REVIEWER)

// 生成响应
val result = agentService.generate(
    userInput = essayText,
    context = emptyList()
)
```

## 模块详细文档

每个子模块都有详细的 README 文档：

- [llm/README.md](llm/README.md) - LLM 推理引擎
- [agent/README.md](agent/README.md) - Agent 系统
- [downloader/README.md](downloader/README.md) - 模型下载
- [session/README.md](session/README.md) - 会话管理
- [history/README.md](history/README.md) - 对话历史
- [model/README.md](model/README.md) - 模型配置

## 架构优势

1. **统一入口** - AgentService 是所有对话的唯一入口
2. **角色扮演** - 通过切换 Agent 角色实现不同教学场景
3. **官方架构** - 基于 MediaPipe 官方的 Engine + Session 架构
4. **清晰职责** - 每个模块职责单一，易于维护
5. **易于扩展** - 添加新角色只需在 AgentRole 中定义

## 参考资料

- [MediaPipe LLM Inference 官方文档](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference)
- [官方示例代码](https://github.com/google-ai-edge/mediapipe-samples/tree/main/examples/llm_inference/android)
