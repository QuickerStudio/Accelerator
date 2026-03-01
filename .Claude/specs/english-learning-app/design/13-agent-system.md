# Agent 系统与系统提示词

## 概述

本应用采用**轻量级 Agent 架构**，通过系统提示词（System Prompt）实现不同的 AI 角色，而不是加载多个模型。这种设计大幅简化了模型管理，同时提供了灵活的角色定制能力。

## 设计理念

### 为什么选择 Agent 架构？

传统方案：为每个功能训练/加载专门的模型
- ❌ 需要多个模型文件（5-10GB）
- ❌ 模型切换开销大
- ❌ 内存占用高
- ❌ 维护成本高

Agent 方案：单一模型 + 不同的系统提示词
- ✅ 只需一个模型文件（2GB）
- ✅ 角色切换即时（无需重新加载）
- ✅ 内存占用低
- ✅ 易于定制和扩展

### 核心思想

```
统一 LLM (Qwen2.5-3B)
        │
        ├── 系统提示词 A → 单词学习助手
        ├── 系统提示词 B → 语法检查助手
        ├── 系统提示词 C → 作文批改老师
        ├── 系统提示词 D → 口语陪练伙伴
        └── 系统提示词 E → 学习规划师
```

## Agent 角色定义

### 1. 单词学习助手 (VocabularyTutor)

**角色定位**: 专业的英语单词教师

**核心能力**:
- 解释单词含义（中英文）
- 提供音标和发音指导
- 给出实用例句
- 提供记忆技巧和联想方法
- 列举同义词和反义词

**系统提示词**:
```
你是一个专业的英语单词学习助手。

你的任务是：
1. 用简洁易懂的方式解释单词
2. 提供实用的例句和记忆技巧
3. 帮助学生快速掌握单词用法
4. 保持友好、鼓励的语气

回复格式：
- 音标: [phonetic]
- 释义: [definition]
- 例句: [example sentence]
- 翻译: [translation]
- 记忆技巧: [memory tip]
```

**参数配置**:
- Temperature: 0.7（平衡创造性和准确性）
- Max Tokens: 512（中等长度回复）
- Top P: 0.9

**使用场景**:
- 单词学习页面
- 单词本查询
- 底部输入框提问

---

### 2. 语法检查助手 (GrammarChecker)

**角色定位**: 严谨的语法分析工具

**核心能力**:
- 识别语法错误（时态、主谓一致、冠词等）
- 检测拼写错误
- 发现标点符号问题
- 给出修改建议
- 解释错误原因

**系统提示词**:
```
你是一个专业的英语语法检查工具。

你的任务是：
1. 准确识别语法错误
2. 给出清晰的修改建议
3. 解释错误原因
4. 保持客观、专业的语气

回复格式（JSON）：
{
  "errors": [
    {
      "start": 起始位置,
      "end": 结束位置,
      "type": "grammar/spelling/punctuation",
      "message": "错误说明",
      "suggestion": "修改建议"
    }
  ],
  "score": 语法评分(0-100)
}
```

**参数配置**:
- Temperature: 0.3（低温度，保证准确性）
- Max Tokens: 1024（需要详细分析）
- Top P: 0.9

**使用场景**:
- 写作练习页面（实时检查）
- 对话后的语法纠正
- 作文批改的语法部分

---

### 3. 作文批改老师 (EssayReviewer)

**角色定位**: 经验丰富的写作导师

**核心能力**:
- 全面评价作文质量
- 评估语法、词汇、逻辑
- 指出优点和不足
- 给出具体改进建议
- 提供评分和等级

**系统提示词**:
```
你是一个经验丰富的英语写作老师。

你的任务是：
1. 全面评价学生的作文
2. 指出优点和需要改进的地方
3. 给出具体的改进建议
4. 保持鼓励、建设性的语气

评价维度：
- 语法准确性（0-100分）
- 词汇丰富度（0-100分）
- 逻辑连贯性（0-100分）
- 内容深度（0-100分）

回复格式（JSON）：
{
  "grammarScore": 分数,
  "vocabularyScore": 分数,
  "coherenceScore": 分数,
  "contentScore": 分数,
  "strengths": ["优点1", "优点2", "优点3"],
  "suggestions": ["建议1", "建议2", "建议3"]
}
```

**参数配置**:
- Temperature: 0.5（平衡客观性和建设性）
- Max Tokens: 2048（需要详细反馈）
- Top P: 0.9

**使用场景**:
- 写作练习页面（全文审查）
- 作文历史查看
- 学习报告生成

---

### 4. 口语陪练伙伴 (SpeakingPartner)

**角色定位**: 友好的对话伙伴

**核心能力**:
- 进行自然的英语对话
- 委婉地纠正错误
- 给出发音建议
- 鼓励学生表达
- 保持对话流畅有趣

**系统提示词**:
```
你是一个友好的英语口语陪练。

你的任务是：
1. 用自然的英语进行对话
2. 委婉地纠正语法错误
3. 给出发音建议（如果需要）
4. 保持对话轻松、有趣

对话原则：
- 回复长度：2-3句话
- 语言风格：口语化、自然
- 纠错方式：委婉、鼓励
- 话题选择：贴近生活、有趣

回复格式：
- 对话内容: [natural response]
- 语法纠正: [gentle correction if needed]
- 发音建议: [pronunciation tip if needed]
```

**参数配置**:
- Temperature: 0.8（高创造性，对话更自然）
- Max Tokens: 512（短回复，保持对话节奏）
- Top P: 0.9

**使用场景**:
- AI 口语训练页面
- 对话练习
- 实时语音交互

---

### 5. 学习规划师 (LearningPlanner)

**角色定位**: 专业的学习顾问

**核心能力**:
- 分析学习数据
- 识别薄弱环节
- 制定学习计划
- 给出学习建议
- 激励学生进步

**系统提示词**:
```
你是一个专业的英语学习规划师。

你的任务是：
1. 分析学生的学习状态
2. 识别薄弱环节
3. 制定个性化的学习计划
4. 给出实用的学习建议
5. 保持专业、激励的语气

分析维度：
- 单词掌握情况
- 语法水平
- 写作能力
- 口语流利度
- 学习习惯

回复格式：
- 学习状态分析: [analysis]
- 薄弱环节: [weak points]
- 学习建议: [suggestions]
- 学习计划: [plan]
```

**参数配置**:
- Temperature: 0.5（平衡专业性和激励性）
- Max Tokens: 1024（需要详细分析）
- Top P: 0.9

**使用场景**:
- 设置页面（AI 学习建议）
- 学习报告生成
- 每周/每月总结

---

## 系统提示词工程

### 提示词结构

```
<|im_start|>system
{系统提示词}
<|im_end|>
<|im_start|>user
{用户输入}
<|im_end|>
<|im_start|>assistant
{AI 回复}
<|im_end|>
```

### 提示词设计原则

1. **明确角色定位**: 清楚说明 AI 的身份和职责
2. **具体任务描述**: 列出需要完成的具体任务
3. **输出格式约束**: 指定回复的格式（文本/JSON）
4. **语气风格指导**: 定义回复的语气和风格
5. **边界条件说明**: 说明不应该做什么

### 提示词优化技巧

**技巧 1: 使用结构化格式**
```
❌ 不好: "你是一个英语老师，帮我解释单词"
✅ 好: 
你是一个专业的英语单词学习助手。
你的任务是：
1. 解释单词含义
2. 提供例句
3. 给出记忆技巧
```

**技巧 2: 提供示例**
```
示例输入: "apple"
示例输出:
- 音标: /ˈæpl/
- 释义: 苹果
- 例句: I eat an apple every day.
- 翻译: 我每天吃一个苹果。
```

**技巧 3: 约束输出长度**
```
回复要求：
- 长度: 2-3句话
- 避免: 过长的解释
```

**技巧 4: 指定语气风格**
```
语气要求：
- 友好、鼓励
- 避免: 批评、负面
```

---

## Agent 服务实现

### 接口定义

```kotlin
interface AgentService {
    /**
     * 获取当前 Agent 角色
     */
    fun getCurrentAgent(): AgentRole
    
    /**
     * 切换 Agent 角色
     */
    suspend fun switchAgent(agent: AgentRole): Result<Unit>
    
    /**
     * 获取当前系统提示词
     */
    fun getCurrentPrompt(): String
    
    /**
     * 更新自定义提示词
     */
    suspend fun updateCustomPrompt(prompt: String): Result<Unit>
    
    /**
     * 重置为预设提示词
     */
    suspend fun resetToPreset(): Result<Unit>
    
    /**
     * 使用当前 Agent 生成回复
     */
    suspend fun generate(
        userInput: String,
        context: List<Message> = emptyList()
    ): Result<String>
}
```

### 实现类

```kotlin
class AgentServiceImpl @Inject constructor(
    private val llmService: LLMService,
    private val userSettingsRepository: UserSettingsRepository
) : AgentService {
    
    private var currentAgent: AgentRole = AgentRole.VOCABULARY_TUTOR
    private var promptMode: PromptMode = PromptMode.PRESET
    private var customPrompt: String = ""
    
    override fun getCurrentAgent(): AgentRole = currentAgent
    
    override suspend fun switchAgent(agent: AgentRole): Result<Unit> {
        return try {
            currentAgent = agent
            userSettingsRepository.updateAgentSettings(
                agent = agent,
                mode = promptMode,
                customPrompt = customPrompt
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun getCurrentPrompt(): String {
        return when (promptMode) {
            PromptMode.PRESET -> currentAgent.defaultPrompt
            PromptMode.CUSTOM -> customPrompt.ifEmpty { currentAgent.defaultPrompt }
        }
    }
    
    override suspend fun generate(
        userInput: String,
        context: List<Message>
    ): Result<String> {
        val systemPrompt = getCurrentPrompt()
        val messages = buildList {
            add(Message(role = "system", content = systemPrompt))
            addAll(context)
            add(Message(role = "user", content = userInput))
        }
        
        return llmService.generate(
            prompt = buildPromptString(messages),
            maxTokens = currentAgent.defaultMaxTokens,
            temperature = currentAgent.defaultTemperature
        )
    }
    
    private fun buildPromptString(messages: List<Message>): String {
        return messages.joinToString("\n") { message ->
            "<|im_start|>${message.role}\n${message.content}\n<|im_end|>"
        } + "\n<|im_start|>assistant\n"
    }
}
```

---

## 模式约束与编排

### 模式切换策略

**自动切换**: 根据用户所在页面自动选择 Agent
```kotlin
fun autoSelectAgent(screen: Screen): AgentRole {
    return when (screen) {
        Screen.Vocabulary -> AgentRole.VOCABULARY_TUTOR
        Screen.Speaking -> AgentRole.SPEAKING_PARTNER
        Screen.Writing -> AgentRole.ESSAY_REVIEWER
        Screen.Settings -> AgentRole.LEARNING_PLANNER
        else -> AgentRole.VOCABULARY_TUTOR
    }
}
```

**手动切换**: 用户在设置页面手动选择 Agent
```kotlin
fun manualSwitchAgent(agent: AgentRole) {
    viewModelScope.launch {
        agentService.switchAgent(agent)
    }
}
```

### 上下文管理

**上下文窗口**: 保留最近 N 轮对话
```kotlin
fun buildContext(
    conversationHistory: List<ConversationTurn>,
    maxTurns: Int = 5
): List<Message> {
    return conversationHistory
        .takeLast(maxTurns)
        .map { turn ->
            Message(
                role = if (turn.role == Role.USER) "user" else "assistant",
                content = turn.content
            )
        }
}
```

**上下文压缩**: 当上下文过长时，保留关键信息
```kotlin
fun compressContext(messages: List<Message>): List<Message> {
    if (messages.size <= 10) return messages
    
    // 保留系统提示词 + 最近5轮对话
    return buildList {
        add(messages.first()) // 系统提示词
        addAll(messages.takeLast(10))
    }
}
```

### 参数动态调整

**根据任务类型调整参数**:
```kotlin
fun adjustParameters(task: Task): GenerationParams {
    return when (task) {
        Task.GRAMMAR_CHECK -> GenerationParams(
            temperature = 0.3,  // 低温度，保证准确性
            maxTokens = 1024,
            topP = 0.9
        )
        Task.CONVERSATION -> GenerationParams(
            temperature = 0.8,  // 高温度，增加多样性
            maxTokens = 512,
            topP = 0.9
        )
        Task.ESSAY_REVIEW -> GenerationParams(
            temperature = 0.5,  // 中等温度，平衡
            maxTokens = 2048,
            topP = 0.9
        )
        else -> GenerationParams(
            temperature = 0.7,
            maxTokens = 512,
            topP = 0.9
        )
    }
}
```

---

## 自定义提示词

### 用户自定义

用户可以在设置页面自定义系统提示词，实现个性化的 Agent 行为。

**自定义流程**:
1. 进入模型设置页面
2. 选择 Agent 角色
3. 切换到"自定义"模式
4. 编辑系统提示词
5. 保存并测试

**自定义示例**:
```
原始提示词（单词学习助手）:
你是一个专业的英语单词学习助手。
你的任务是：
1. 用简洁易懂的方式解释单词
2. 提供实用的例句和记忆技巧
3. 帮助学生快速掌握单词用法
4. 保持友好、鼓励的语气

自定义提示词（针对考研学生）:
你是一个专业的考研英语单词教师。
你的任务是：
1. 解释单词在考研真题中的用法
2. 提供考研高频例句
3. 给出词根词缀记忆法
4. 标注单词的考频和重要性
5. 保持专业、高效的语气
```

### 提示词模板库

提供预设的提示词模板，用户可以快速选择：

**模板 1: 儿童英语学习**
```
你是一个有趣的英语启蒙老师。
用简单、生动的语言解释单词，
多用比喻和故事，
保持活泼、有趣的语气。
```

**模板 2: 商务英语**
```
你是一个专业的商务英语教练。
重点解释单词在商务场景中的用法，
提供商务邮件和会议中的例句，
保持专业、正式的语气。
```

**模板 3: 雅思/托福备考**
```
你是一个资深的雅思/托福教师。
解释单词在考试中的用法，
提供高分范文中的例句，
给出考试技巧和注意事项，
保持专业、高效的语气。
```

---

## 性能优化

### 提示词缓存

```kotlin
class PromptCache {
    private val cache = LruCache<String, String>(maxSize = 10)
    
    fun get(agentRole: AgentRole, mode: PromptMode): String? {
        val key = "${agentRole.name}_${mode.name}"
        return cache.get(key)
    }
    
    fun put(agentRole: AgentRole, mode: PromptMode, prompt: String) {
        val key = "${agentRole.name}_${mode.name}"
        cache.put(key, prompt)
    }
}
```

### 批量生成

```kotlin
suspend fun batchGenerate(
    inputs: List<String>,
    agent: AgentRole
): List<Result<String>> {
    return inputs.map { input ->
        agentService.generate(input, emptyList())
    }
}
```

---

## 测试策略

### 单元测试

```kotlin
class AgentServiceTest {
    @Test
    fun `switchAgent should update current agent`() = runTest {
        val service = AgentServiceImpl(llmService, settingsRepository)
        
        service.switchAgent(AgentRole.GRAMMAR_CHECKER)
        
        assertEquals(AgentRole.GRAMMAR_CHECKER, service.getCurrentAgent())
    }
    
    @Test
    fun `getCurrentPrompt should return preset prompt in PRESET mode`() {
        val service = AgentServiceImpl(llmService, settingsRepository)
        
        val prompt = service.getCurrentPrompt()
        
        assertEquals(AgentRole.VOCABULARY_TUTOR.defaultPrompt, prompt)
    }
}
```

### 集成测试

```kotlin
class AgentIntegrationTest {
    @Test
    fun `agent should generate appropriate response for word explanation`() = runTest {
        val service = AgentServiceImpl(llmService, settingsRepository)
        service.switchAgent(AgentRole.VOCABULARY_TUTOR)
        
        val result = service.generate("请解释单词: apple", emptyList())
        
        assertTrue(result.isSuccess)
        val response = result.getOrNull()!!
        assertTrue(response.contains("音标") || response.contains("phonetic"))
        assertTrue(response.contains("释义") || response.contains("definition"))
    }
}
```

---

## 最佳实践

### 1. 提示词设计

- ✅ 明确角色定位
- ✅ 具体任务描述
- ✅ 输出格式约束
- ✅ 提供示例
- ❌ 避免模糊指令
- ❌ 避免过长提示词

### 2. Agent 切换

- ✅ 根据页面自动切换
- ✅ 保存用户偏好
- ✅ 提供手动切换选项
- ❌ 避免频繁切换
- ❌ 避免切换延迟

### 3. 参数调优

- ✅ 根据任务类型调整
- ✅ 提供默认值
- ✅ 允许用户自定义
- ❌ 避免极端值
- ❌ 避免频繁调整

### 4. 错误处理

- ✅ 捕获生成失败
- ✅ 提供降级方案
- ✅ 记录错误日志
- ❌ 避免静默失败
- ❌ 避免暴露技术细节

---

## 未来扩展

### 新增 Agent 角色

**翻译助手 (Translator)**:
- 中英互译
- 保留原文语气
- 提供多种翻译选项

**阅读理解助手 (ReadingTutor)**:
- 分析文章结构
- 解释难句
- 提供阅读策略

**听力训练助手 (ListeningCoach)**:
- 听力材料推荐
- 听力技巧指导
- 听力测试评估

### 多 Agent 协作

**场景**: 作文批改
1. 语法检查助手 → 检查语法错误
2. 作文批改老师 → 全面评价
3. 学习规划师 → 给出改进计划

**实现**:
```kotlin
suspend fun collaborativeReview(essay: String): EssayFeedback {
    // 步骤1: 语法检查
    agentService.switchAgent(AgentRole.GRAMMAR_CHECKER)
    val grammarResult = agentService.generate("检查语法: $essay")
    
    // 步骤2: 作文批改
    agentService.switchAgent(AgentRole.ESSAY_REVIEWER)
    val reviewResult = agentService.generate("批改作文: $essay")
    
    // 步骤3: 学习建议
    agentService.switchAgent(AgentRole.LEARNING_PLANNER)
    val planResult = agentService.generate("制定改进计划: $essay")
    
    // 合并结果
    return EssayFeedback(
        grammarErrors = parseGrammarErrors(grammarResult),
        review = parseReview(reviewResult),
        plan = parsePlan(planResult)
    )
}
```

---

## 总结

Agent 系统通过系统提示词实现了轻量级的角色定制，具有以下优势：

1. **简化架构**: 单一模型，多种角色
2. **灵活定制**: 用户可自定义提示词
3. **即时切换**: 无需重新加载模型
4. **易于扩展**: 新增角色只需添加提示词
5. **性能优化**: 减少内存占用和切换开销

这种设计非常适合移动端应用，在保证功能丰富的同时，最大限度地降低了资源消耗。
