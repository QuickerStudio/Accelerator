# Session Management

会话管理模块，负责管理当前对话会话的状态和上下文。

## 核心组件

### Session.kt
会话数据模型。

**数据结构：**
```kotlin
data class Session(
    val id: String,
    val agentRole: AgentRole,
    val messages: MutableList<Message>,
    val createdAt: Long,
    val lastActiveAt: Long
)
```

### SessionManager.kt
会话管理器，提供会话创建、更新、清理等功能。

**主要功能：**
- 创建新会话
- 添加消息到会话
- 获取会话历史
- 清空会话
- 会话持久化

## 使用示例

```kotlin
val sessionManager = SessionManager(context)

// 创建新会话
val session = sessionManager.createSession(AgentRole.SPEAKING_PARTNER)

// 添加消息
sessionManager.addMessage(
    sessionId = session.id,
    message = Message(role = "user", content = "Hello")
)

// 获取会话消息
val messages = sessionManager.getMessages(session.id)

// 清空会话
sessionManager.clearSession(session.id)

// 获取当前活跃会话
val activeSession = sessionManager.getActiveSession()
```

## 会话生命周期

```
创建会话 → 添加消息 → 保存会话 → 清空会话
    ↓           ↓           ↓           ↓
  Session   Message    Persist    Clear
```

## 与其他模块的关系

```
UI 层
  ↓
AgentService
  ↓
SessionManager (管理当前会话)
  ↓
HistoryManager (保存历史会话)
```

## 特性

- **会话隔离** - 每个会话独立管理
- **自动保存** - 会话自动持久化
- **上下文管理** - 自动管理对话上下文
- **会话恢复** - 支持恢复之前的会话
