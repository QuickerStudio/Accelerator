# Conversation History

对话历史管理模块，负责保存和检索用户的对话记录。

## 核心组件

### ConversationHistory.kt
对话历史数据模型。

**数据结构：**
```kotlin
data class ConversationHistory(
    val id: String,
    val title: String,
    val messages: List<Message>,
    val timestamp: Long,
    val agentRole: AgentRole
)
```

### HistoryManager.kt
对话历史管理器，提供保存、加载、删除等功能。

**主要功能：**
- 保存对话历史
- 加载对话历史
- 删除对话历史
- 搜索对话历史
- 导出对话历史

## 使用示例

```kotlin
val historyManager = HistoryManager(context)

// 保存对话
historyManager.saveConversation(
    title = "English Practice",
    messages = messageList,
    agentRole = AgentRole.SPEAKING_PARTNER
)

// 加载所有对话
val conversations = historyManager.loadAllConversations()

// 加载特定对话
val conversation = historyManager.loadConversation(conversationId)

// 删除对话
historyManager.deleteConversation(conversationId)

// 搜索对话
val results = historyManager.searchConversations("grammar")
```

## 存储格式

对话历史以 JSON 格式存储在应用内部存储中：

```
/data/user/0/com.english.accelerator/files/
└── conversations/
    ├── conversation_1.json
    ├── conversation_2.json
    └── ...
```

## 特性

- **持久化存储** - 对话历史永久保存
- **快速检索** - 支持按标题、内容搜索
- **分类管理** - 按 Agent 角色分类
- **导出功能** - 支持导出为 JSON 或文本格式
