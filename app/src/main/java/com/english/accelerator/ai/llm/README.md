# LLM Module (Deprecated)

⚠️ **This package is deprecated. Use `ai/core/` and `ai/service/` instead.**

The LLM inference functionality has been refactored into a cleaner architecture:

## New Architecture

```
com.english.accelerator.ai/
├── core/                          # Core inference engine
│   ├── InferenceEngine.kt         # Unified engine (Engine + Session)
│   └── InferenceConfig.kt         # Model configuration
│
├── service/                       # Business services
│   ├── ChatService.kt             # Async streaming chat
│   ├── GrammarService.kt          # Grammar checking
│   └── WritingService.kt          # Writing improvement
│
└── prompt/                        # Prompt templates
    ├── GrammarPrompts.kt
    └── WritingPrompts.kt
```

## 核心组件

### 1. InferenceModel.kt
**职责：** 模型加载和推理的核心实现

**关键功能：**
- `createEngine()` - 创建 LLM 推理引擎
- `createSession()` - 创建推理会话（管理温度、TopK、TopP）
- `generateResponseAsync()` - 异步流式推理
- `estimateTokensRemaining()` - Token 计数

**使用示例：**
```kotlin
// 获取单例
val model = InferenceModel.getInstance(context)

// 异步推理（流式输出）
val future = model.generateResponseAsync(prompt) { partialResult, done ->
    // 实时接收生成的文本片段
    if (!done) {
        updateUI(partialResult)
    }
}
```

### 2. ChatViewModel.kt
**职责：** 聊天状态管理和业务逻辑

**关键功能：**
- `sendMessage()` - 发送用户消息并触发推理
- `resetInferenceModel()` - 切换模型
- `recomputeSizeInTokens()` - 重新计算剩余 Token

**StateFlow：**
- `uiState` - 消息列表状态
- `tokensRemaining` - 剩余 Token 数
- `isTextInputEnabled` - 输入框启用状态

**使用示例：**
```kotlin
// 在 Composable 中使用
val viewModel: ChatViewModel = viewModel(
    factory = ChatViewModel.getFactory(context)
)

val uiState by viewModel.uiState.collectAsState()
val tokensRemaining by viewModel.tokensRemaining.collectAsState()

// 发送消息
viewModel.sendMessage("Hello, how are you?")
```

### 3. ChatUiState.kt
**职责：** 消息队列管理

**关键功能：**
- `addMessage()` - 添加新消息
- `createLoadingMessage()` - 创建加载中的消息
- `appendMessage()` - 追加文本到当前消息（流式输出）
- `clearMessages()` - 清空消息历史

**特殊功能：**
- 支持 "thinking mode"（思考模式）
- 自动处理 `</think>` 标记，分离思考过程和最终回答

### 4. ChatMessage.kt
**职责：** 消息数据模型

**字段：**
- `id` - 唯一标识符
- `rawMessage` - 原始消息内容
- `author` - 作者（USER_PREFIX 或 MODEL_PREFIX）
- `isLoading` - 是否正在加载
- `isThinking` - 是否处于思考模式

## 集成指南

### 步骤 1: 准备模型文件

确保模型文件已下载到正确位置：
```kotlin
val modelPath = File(context.filesDir, "models/your-model.task")
```

### 步骤 2: 配置 InferenceModel

修改 `InferenceModel.kt` 中的模型配置：
```kotlin
companion object {
    var model: Model = Model.GEMMA_3_1B_IT_GPU  // 选择你的模型
}
```

### 步骤 3: 在 UI 中使用

```kotlin
@Composable
fun ChatScreen() {
    val context = LocalContext.current
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModel.getFactory(context)
    )

    val uiState by viewModel.uiState.collectAsState()
    val isInputEnabled by viewModel.isTextInputEnabled.collectAsState()

    Column {
        // 消息列表
        LazyColumn {
            items(uiState.messages) { message ->
                MessageBubble(message)
            }
        }

        // 输入框
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            enabled = isInputEnabled
        )

        Button(
            onClick = {
                viewModel.sendMessage(inputText)
                inputText = ""
            },
            enabled = isInputEnabled
        ) {
            Text("发送")
        }
    }
}
```

## 与现有代码的集成

### 方案 A: 替换现有的 GemmaInferenceManager

如果你想完全使用官方实现：

1. 将 `ConversationScreen.kt` 中的推理逻辑改为使用 `ChatViewModel`
2. 删除或弃用 `GemmaInferenceManager.kt`

### 方案 B: 保留两套实现

如果你想保留现有实现作为备用：

1. 保持 `GemmaInferenceManager` 用于语法检查、写作建议等功能
2. 使用 `ChatViewModel` 专门用于对话功能
3. 根据场景选择合适的实现

## 关键差异对比

| 特性 | GemmaInferenceManager | InferenceModel (官方) |
|------|----------------------|---------------------|
| 架构 | 单层（直接使用 LlmInference） | 两层（Engine + Session） |
| 推理方式 | 同步阻塞 | 异步流式 |
| Token 管理 | 无 | 完整支持 |
| 会话管理 | 无 | 支持会话重置 |
| 思考模式 | 无 | 支持 |
| 状态管理 | StateFlow | ViewModel + StateFlow |

## 性能优化建议

1. **内存管理**
   - 及时调用 `model.close()` 释放资源
   - 监听 `onLowMemory()` 事件

2. **Token 优化**
   - 使用 `estimateTokensRemaining()` 避免超出上下文限制
   - 定期清理历史消息：`uiState.clearMessages()`

3. **推理优化**
   - 调整 `MAX_TOKENS` 和 `DECODE_TOKEN_OFFSET` 平衡速度和质量
   - 根据设备性能选择 CPU 或 GPU 后端

## 常见问题

### Q: 如何切换模型？
```kotlin
// 修改 InferenceModel.kt 中的 model 变量
InferenceModel.model = Model.QWEN2_1_5B_INSTRUCT

// 重置实例
val newModel = InferenceModel.resetInstance(context)
viewModel.resetInferenceModel(newModel)
```

### Q: 如何处理长对话？
```kotlin
// 监听 Token 数量
viewModel.tokensRemaining.collect { remaining ->
    if (remaining < 100) {
        // 提示用户或自动清理历史
        viewModel.uiState.value.clearMessages()
    }
}
```

### Q: 如何自定义推理参数？
修改 `InferenceModel.createSession()` 中的参数：
```kotlin
val sessionOptions = LlmInferenceSessionOptions.builder()
    .setTemperature(0.7f)  // 创造性 (0.0-1.0)
    .setTopK(50)           // 候选词数量
    .setTopP(0.9f)         // 累积概率阈值
    .build()
```

## 下一步工作

- [ ] 添加多模态支持（图片理解）
- [ ] 集成语音识别（audio_classifier）
- [ ] 实现历史对话搜索（text_embedder）
- [ ] 添加模型下载和管理 UI
- [ ] 实现对话导出功能

## 参考资料

- [MediaPipe LLM Inference 官方文档](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference)
- [官方示例代码](https://github.com/google-ai-edge/mediapipe-samples/tree/main/examples/llm_inference/android)
