# 代码重构修复计划

## 编译错误分类统计

### 📊 错误总览
- **总错误数**: 108 个编译错误
- **涉及文件**: 11 个文件
- **主要问题**: 旧架构遗留代码 + 缺失基础接口

---

## 🗂️ 按模块分类

### 1️⃣ MainActivity (2 个错误)
**文件**: `MainActivity.kt`
**错误类型**: 旧测试代码遗留
**错误详情**:
- 行 34: `import VoiceInputTestScreen` - 未解析
- 行 97: `VoiceInputTestScreen()` 调用 - 未解析

**修复方案**: ❌ **删除**
- 删除 import 语句
- 删除 testMode 相关代码块

**优先级**: 🔴 高 (简单删除)

---

### 2️⃣ SettingsScreen (6 个错误)
**文件**: `ui/settings/SettingsScreen.kt`
**错误类型**: 缺失状态类型
**错误详情**:
- 行 40, 52, 115, 129, 136: `ModelState` 未解析

**修复方案**: ✅ **创建新文件**
```kotlin
// ai/llm/ModelState.kt
sealed class ModelState {
    object Idle : ModelState()
    object Loading : ModelState()
    object Ready : ModelState()
    data class Error(val message: String) : ModelState()
}
```

**优先级**: 🟡 中 (需要创建)

---

### 3️⃣ WritingScreen (11 个错误)
**文件**: `ui/writing/WritingScreen.kt`
**错误类型**: 旧架构遗留 (GrammarSuggestion)
**错误详情**:
- 行 26: import `GrammarSuggestion`
- 行 212, 214: `onApplySuggestion` 参数使用 `suggestion.original/corrected`
- 行 519: `AiAssistPanel` 参数类型
- 行 664-724: `SuggestionCard` 组件完整实现

**修复方案**: ❌ **删除**
1. 删除 import 语句
2. 删除 `onApplySuggestion` 参数和回调
3. 删除 `SuggestionCard` 整个组件
4. 简化 `AiAssistPanel` 接口

**架构说明**:
- 旧架构: 直接使用 `GrammarSuggestion` 数据类
- 新架构: 通过 `AgentService` + `AgentRole.GRAMMAR_CHECKER` 实现

**优先级**: 🔴 高 (精简优先)

---

### 4️⃣ SpeakingScreen (7 个错误)
**文件**: `ui/speaking/SpeakingScreen.kt`
**错误类型**: 接口调用不匹配
**错误详情**:
- 行 148: `ChatWindow` 参数 `onMessageRender` 不存在
- 行 148: 缺少 `agentBubble` 和 `userBubble` 参数
- 行 149-152: lambda 中调用 `Render()` 不在 Composable 上下文
- 行 197: `Conversation` 类型未解析

**当前代码**:
```kotlin
ChatWindow(
    messages = messages,
    onMessageRender = { message ->  // ❌ 错误参数
        if (message.isFromUser) {
            UserBubble(message).Render()
        } else {
            AgentBubble(message).Render()
        }
    }
).Render()
```

**修复方案**: 🔧 **适配新架构**
```kotlin
ChatWindow(
    messages = messages,
    agentBubble = AgentBubble(message),  // ✅ 传递节点实例
    userBubble = UserBubble(message)     // ✅ 传递节点实例
).Render()
```

**优先级**: 🟡 中 (需要理解 Node 架构)

---

### 5️⃣ Speaking Nodes (60+ 个错误)
**涉及文件**:
- `nodes/AgentBubble.kt` (15 个错误)
- `nodes/UserBubble.kt` (8 个错误)
- `nodes/ChatWindow.kt` (14 个错误)
- `nodes/History.kt` (16 个错误)
- `nodes/InputBox.kt` (4 个错误)
- `nodes/NavBar.kt` (4 个错误)

**错误类型**: 缺失基础接口和类型
**错误详情**:
1. 所有节点: `Node` 接口未解析
2. AgentBubble, UserBubble, ChatWindow: `models.Message` 未解析
3. History: `models.Conversation` 未解析
4. 所有节点: `override` 关键字错误 (因为 Node 接口不存在)

**修复方案**: ✅ **创建基础架构**

#### 需要创建的文件:

**1. Node.kt** (基础接口)
```kotlin
// ui/speaking/Node.kt
interface Node {
    val id: String

    @Composable
    fun Render()
}
```

**2. models/Message.kt** (消息模型)
```kotlin
// ui/speaking/models/Message.kt
data class Message(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val inferenceStats: InferenceStats? = null
)

data class InferenceStats(
    val startTime: Long,
    val endTime: Long,
    val tokensGenerated: Int,
    val memoryUsedMB: Long
) {
    val durationSeconds: Float
    val tokensPerSecond: Float
}
```

**3. models/Conversation.kt** (对话模型)
```kotlin
// ui/speaking/models/Conversation.kt
data class Conversation(
    val id: String,
    val title: String,
    val timestamp: Long,
    val messageCount: Int,
    val preview: String
)
```

**优先级**: 🔴 高 (基础架构，必须先创建)

---

### 6️⃣ VocabularyScreen (1 个错误)
**文件**: `ui/vocabulary/VocabularyScreen.kt`
**错误类型**: 导入路径问题
**错误详情**:
- 行 64: `BookmarkNode` 未解析

**当前代码**:
```kotlin
BookmarkNode(onBackClick = { showBookmarkScreen = false }).Render()
```

**问题分析**:
- `BookmarkNode.kt` 文件存在于 `ui/vocabulary/nodes/`
- 但缺少 import 语句

**修复方案**: 🔧 **添加 import**
```kotlin
import com.english.accelerator.ui.vocabulary.nodes.BookmarkNode
```

**优先级**: 🟢 低 (简单 import)

---

## 📋 修复执行计划

### 阶段 1: 精简 - 删除旧代码 (优先)
1. ✅ MainActivity - 删除 VoiceInputTestScreen 引用
2. ⚠️ WritingScreen - 删除 GrammarSuggestion 相关代码

### 阶段 2: 创建基础架构
3. 创建 `ui/speaking/Node.kt`
4. 创建 `ui/speaking/models/Message.kt`
5. 创建 `ui/speaking/models/Conversation.kt`
6. 创建 `ai/llm/ModelState.kt`

### 阶段 3: 适配连接
7. 修复 SpeakingScreen 的 ChatWindow 调用
8. 修复 VocabularyScreen 的 BookmarkNode import

### 阶段 4: 验证
9. 运行构建验证所有错误已修复
10. 检查运行时是否有问题

---

## 🏗️ 新架构说明

### Agent 系统架构
```
AgentService (统一入口)
    └── MainAgent (推理逻辑)
         └── InferenceEngine (MediaPipe LLM)
              └── 官方 MediaPipe 架构
```

**核心原则**:
- 所有 AI 对话通过 AgentService
- 通过 AgentRole 实现角色扮演
- 统一推理引擎

### Speaking 模块 Node 架构
```
SpeakingScreen (节点管理器)
    └── nodes/ (可替换节点)
         ├── ChatWindow
         ├── AgentBubble
         ├── UserBubble
         ├── InputBox
         ├── NavBar
         └── History
```

**特点**:
- 插件式架构
- 节点独立可替换
- 统一 Node 接口

---

## ⚠️ 注意事项

1. **不要创建不必要的文件** - 只创建架构必需的基础文件
2. **优先删除旧代码** - 精简优先于添加
3. **理解新架构** - 不要盲目修复，要理解设计意图
4. **保持简洁** - 避免过度工程

---

## 📊 进度追踪

- [ ] 阶段 1: 精简旧代码
- [ ] 阶段 2: 创建基础架构
- [ ] 阶段 3: 适配连接
- [ ] 阶段 4: 验证构建

---

生成时间: 2026-03-05
