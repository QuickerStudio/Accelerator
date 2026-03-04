# Agent System - AI Role-Playing Service

Agent 系统是应用中所有 AI 对话的唯一入口，通过角色扮演实现不同的教学场景。

## 架构概述

```
AgentService (接口)
    └── AgentServiceImpl (实现)
         └── InferenceEngine (推理引擎)
              └── MediaPipe LLM (官方架构)
```

**核心原则：**
- **唯一入口** - 所有 AI 对话必须通过 AgentService
- **角色扮演** - Agent 扮演不同角色（英语老师、语法专家、对话伙伴等）
- **统一推理** - 使用 InferenceEngine（基于官方 MediaPipe 架构）

## 核心组件

### 1. AgentService.kt
**职责：** Agent 服务接口

**核心方法：**
```kotlin
interface AgentService {
    // 获取当前 Agent 角色
    fun getCurrentAgent(): AgentRole

    // 切换 Agent 角色
    suspend fun switchAgent(agent: AgentRole): Result<Unit>

    // 生成 AI 响应
    suspend fun generate(userInput: String, context: List<Message>): Result<String>

    // 获取当前 Prompt
    fun getCurrentPrompt(): String

    // 更新自定义 Prompt
    suspend fun updateCustomPrompt(prompt: String): Result<Unit>

    // 重置为预设 Prompt
    suspend fun resetToPreset(): Result<Unit>
}
```

### 2. AgentServiceImpl.kt
**职责：** Agent 服务实现

**关键特性：**
- 使用 `InferenceEngine` 进行推理（官方 MediaPipe 架构）
- 支持多种 Agent 角色
- 支持自定义 Prompt
- 自动压缩上下文（保留最近 10 条消息）

**使用示例：**
```kotlin
// 创建 Agent 服务
val agentService = AgentServiceImpl(context)

// 切换到语法检查角色
agentService.switchAgent(AgentRole.GRAMMAR_CHECKER)

// 生成响应
val result = agentService.generate(
    userInput = "I goes to school yesterday",
    context = emptyList()
)

when (result) {
    is Result.Success -> println(result.value)
    is Result.Failure -> println("Error: ${result.exception.message}")
}
```

### 3. AgentRole.kt
**职责：** 定义所有可用的 Agent 角色

**可用角色：**
- `VOCABULARY_TUTOR` - 词汇导师
- `GRAMMAR_CHECKER` - 语法检查专家
- `SPEAKING_PARTNER` - 口语对话伙伴
- `ESSAY_REVIEWER` - 作文评审专家
- `PRONUNCIATION_COACH` - 发音教练

### 4. AgentPrompts.kt
**职责：** 管理每个角色的系统 Prompt

**Prompt 结构：**
```kotlin
object AgentPrompts {
    fun getPrompt(role: AgentRole): String {
        return when (role) {
            AgentRole.GRAMMAR_CHECKER -> """
                You are an English grammar expert...
            """.trimIndent()
            // ... 其他角色
        }
    }
}
```

### 5. Message.kt
**职责：** 消息数据模型

**字段：**
```kotlin
data class Message(
    val role: String,      // "system", "user", "assistant"
    val content: String,   // 消息内容
    val timestamp: Long = System.currentTimeMillis()
)
```

### 6. PromptMode.kt
**职责：** Prompt 模式枚举

**模式：**
- `PRESET` - 使用预设 Prompt
- `CUSTOM` - 使用自定义 Prompt

### 7. AgentConfig.kt
**职责：** Agent 配置管理

## 使用指南

### 在 UI 层使用 Agent

**WritingScreen（语法检查）：**
```kotlin
val agentService = AgentServiceImpl(context)

// 切换到语法检查角色
agentService.switchAgent(AgentRole.GRAMMAR_CHECKER)

// 检查语法
val result = agentService.generate(
    userInput = userText,
    context = emptyList()
)
```

**SpeakingScreen（对话练习）：**
```kotlin
val agentService = AgentServiceImpl(context)

// 切换到对话伙伴角色
agentService.switchAgent(AgentRole.SPEAKING_PARTNER)

// 生成对话响应
val result = agentService.generate(
    userInput = userMessage,
    context = conversationHistory
)
```

### 自定义 Prompt

```kotlin
// 更新自定义 Prompt
agentService.updateCustomPrompt("""
    You are a friendly English teacher who specializes in...
""".trimIndent())

// 重置为预设 Prompt
agentService.resetToPreset()
```

## 架构优势

1. **统一入口** - 所有 AI 对话都通过 AgentService，便于管理和监控
2. **角色扮演** - 通过切换角色实现不同的教学场景
3. **上下文管理** - 自动压缩对话历史，避免超出 Token 限制
4. **灵活配置** - 支持预设和自定义 Prompt
5. **官方架构** - 使用 MediaPipe 官方的 Engine + Session 架构

## 与其他模块的关系

```
UI 层 (WritingScreen, SpeakingScreen, etc.)
    ↓
AgentService (唯一入口)
    ↓
InferenceEngine (推理引擎)
    ↓
MediaPipe LLM (官方库)
```

**重要原则：**
- ❌ UI 层不应该直接调用 InferenceEngine
- ✅ UI 层必须通过 AgentService 进行所有 AI 对话
- ✅ 这样 Agent 才能扮演不同角色，提供个性化的教学体验

## 扩展 Agent 角色

添加新角色的步骤：

1. 在 `AgentRole.kt` 中添加新的角色枚举
2. 在 `AgentPrompts.kt` 中添加对应的系统 Prompt
3. 在 UI 层切换到新角色并使用

示例：
```kotlin
// 1. 添加新角色
enum class AgentRole {
    // ... 现有角色
    PRONUNCIATION_COACH  // 新角色
}

// 2. 添加 Prompt
fun getPrompt(role: AgentRole): String {
    return when (role) {
        // ... 现有 Prompt
        AgentRole.PRONUNCIATION_COACH -> """
            You are a pronunciation coach...
        """.trimIndent()
    }
}

// 3. 在 UI 中使用
agentService.switchAgent(AgentRole.PRONUNCIATION_COACH)
```

## 性能优化

1. **上下文压缩** - 自动保留最近 10 条消息
2. **懒加载引擎** - InferenceEngine 使用 lazy 初始化
3. **单例模式** - InferenceEngine 是单例，避免重复加载模型

## 常见问题

### Q: 为什么所有对话都要通过 AgentService？
A: 这样可以确保 Agent 能够扮演不同角色，提供个性化的教学体验。如果 UI 直接调用推理引擎，就无法实现角色扮演。

### Q: 如何添加新的 Agent 角色？
A: 在 `AgentRole.kt` 中添加枚举，在 `AgentPrompts.kt` 中添加 Prompt，然后在 UI 中切换到新角色。

### Q: 上下文历史会被自动管理吗？
A: 是的，AgentService 会自动压缩上下文，保留最近 10 条消息（5 轮对话）。

## 参考资料

- [InferenceEngine 文档](../llm/README.md)
- [MediaPipe LLM 官方文档](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference)
