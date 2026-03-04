# 模型设置页面 (Model Settings)

## 设计思路

用户可以配置 AI 模型的参数和系统提示词。页面采用卡片式布局，包含当前模型信息、系统提示词编辑（5个角色选项卡）、模型参数调节等功能。

**核心特性**：
- Agent 角色根据底部导航栏自动切换（单词页→单词学习，口语页→口语陪练等）
- 用户可以在此页面查看和编辑每个 Agent 角色的系统提示词
- 通过选项卡切换不同角色的提示词进行编辑
- 支持保存自定义提示词和重置为预设
- 模型参数可独立调整（温度、最大长度、Top P）

---

## 布局结构
```
┌─────────────────────────────────┐
│  顶部导航栏                       │
│  ←  模型设置                     │
├─────────────────────────────────┤
│                                 │
│  当前模型                        │
│  ┌─────────────────────────┐   │
│  │ 🤖 Qwen2.5-3B           │   │
│  │ 本地模型，完全离线        │   │
│  │         [✓]             │   │
│  └─────────────────────────┘   │
│                                 │
│  系统提示词                      │
│  ┌─────────────────────────┐   │
│  │ 📚单词 🔍语法 ✍️作文     │   │
│  │ �口语 📊规划            │   │
│  │ (横向滚动选项卡)         │   │
│  ├─────────────────────────┤   │
│  │                         │   │
│  │ 你是一个专业的英语单词... │   │
│  │ (可编辑文本区域)         │   │
│  │                         │   │
│  │ [保存] [重置为预设]      │   │
│  └─────────────────────────┘   │
│                                 │
│  模型参数                        │
│  ┌─────────────────────────┐   │
│  │ 温度 (Temperature)       │   │
│  │ 创造性 ━━━●━━━━━━ 精确性 │   │
│  │        0.7              │   │
│  └─────────────────────────┘   │
│                                 │
│  ┌─────────────────────────┐   │
│  │ 最大长度 (Max Tokens)    │   │
│  │ 短 ━━━━━━━●━━━━━━━ 长   │   │
│  │        512              │   │
│  └─────────────────────────┘   │
│                                 │
│  ┌─────────────────────────┐   │
│  │ Top P                   │   │
│  │ 保守 ━━━━━●━━━━━━ 多样  │   │
│  │        0.9              │   │
│  └─────────────────────────┘   │
│                                 │
│  [恢复默认设置]                  │
│                                 │
└─────────────────────────────────┘
```

---

## 设计细节

### 顶部导航栏
- 高度: 64.dp
- 背景: 深色 (#1E1E1E)
- 内边距: 水平16.dp
- 布局: 水平排列
- 组件:
  - **返回按钮** (←): 48.dp × 48.dp, 图标24.dp白色
  - **标题**: "模型设置", HeadlineMedium (20.sp), 白色

### 当前模型卡片
- 宽度: match_parent - 32.dp
- 高度: wrap_content
- 圆角: CornerRadius.large (16.dp)
- 背景: 主题色渐变 (#6366F1 → #8B5CF6)
- 内边距: 20.dp
- 边距: 水平16.dp, 顶部16.dp
- 阴影: elevation = 4.dp

- 组件:
  - **模型图标**: 🤖, 32.dp, 白色
  - **模型名称**: "Qwen2.5-3B", HeadlineMedium (18.sp), 白色, 粗体
  - **模型描述**: "本地模型，完全离线", BodyMedium (14.sp), 白色透明80%
  - **选中标记**: ✓, 24.dp, 白色, 右上角

### 系统提示词区域
- 标题: "系统提示词", HeadlineSmall (16.sp), 白色, 粗体
- 内边距: 水平16.dp, 顶部24.dp, 底部12.dp
- 说明: "为不同场景定制 AI 的行为和风格", BodySmall (12.sp), 灰色

### 角色选项卡
- 布局: 水平滚动行（ScrollableTabRow）
- 高度: 48.dp
- 背景: 深色卡片 (#2A2A2A)
- 圆角: 顶部 CornerRadius.large (16.dp)
- 边距: 水平16.dp, 顶部12.dp

**选项卡按钮**:
- 宽度: wrap_content, 最小宽度 80.dp
- 高度: 48.dp
- 内边距: 水平16.dp
- 未选中: 背景透明，文本灰色
- 选中: 背景主题色，文本白色，圆角12.dp
- 动画: 300ms 平滑过渡

**5 个选项卡**:
1. **📚 单词学习** (VocabularyTutor)
   - 默认温度: 0.7, Max Tokens: 512
   
2. **🔍 语法检查** (GrammarChecker)
   - 默认温度: 0.3, Max Tokens: 1024
   
3. **✍️ 作文批改** (EssayReviewer)
   - 默认温度: 0.5, Max Tokens: 2048
   
4. **💬 口语陪练** (SpeakingPartner)
   - 默认温度: 0.8, Max Tokens: 512
   
5. **📊 学习规划** (LearningPlanner)
   - 默认温度: 0.5, Max Tokens: 1024

**注意**: Agent 角色会根据用户所在页面自动切换（单词页→单词学习，口语页→口语陪练等），此处仅用于查看和编辑各角色的系统提示词。

### 提示词编辑框
- 宽度: match_parent - 32.dp
- 最小高度: 200.dp
- 最大高度: 400.dp
- 圆角: 底部 CornerRadius.large (16.dp)
- 背景: 深色卡片 (#2A2A2A)
- 内边距: 16.dp
- 边距: 水平16.dp
- 字体: BodyMedium (14.sp), 白色
- 行高: 22.sp
- 可滚动: 垂直滚动
- 可编辑: 是
- 实时显示字符数
- 最大长度: 2000 字符
- 占位符: "在此输入系统提示词..."

**显示内容**:
- 根据选中的选项卡显示对应角色的提示词
- 如果用户修改过，显示自定义内容
- 如果未修改，显示预设内容（可编辑）

### 提示词操作按钮
- 布局: 水平排列，两个按钮
- 边距: 水平16.dp, 顶部12.dp

**保存按钮**:
- 宽度: 占50% - 6.dp
- 高度: 48.dp
- 圆角: CornerRadius.medium (12.dp)
- 背景: 主题色
- 文本: "保存", LabelLarge, 白色
- 功能: 保存当前选项卡对应角色的提示词

**重置为预设按钮**:
- 宽度: 占50% - 6.dp
- 高度: 48.dp
- 圆角: CornerRadius.medium (12.dp)
- 背景: 透明，边框1.dp灰色
- 文本: "重置为预设", LabelLarge, 灰色
- 功能: 将当前选项卡对应角色的提示词恢复为系统预设

### 模型参数区域
- 标题: "模型参数", HeadlineSmall (16.sp), 白色, 粗体
- 内边距: 水平16.dp, 顶部24.dp, 底部12.dp

### 参数调节卡片
- 宽度: match_parent - 32.dp
- 高度: wrap_content
- 圆角: CornerRadius.large (16.dp)
- 背景: 深色卡片 (#2A2A2A)
- 内边距: 20.dp
- 边距: 水平16.dp, 底部12.dp
- 组件:
  - **参数名称**: BodyLarge (16.sp), 白色, 粗体
  - **参数说明**: BodySmall (12.sp), 灰色, 底部间距8.dp
  - **滑块**: 颜色主题色, 标签左右两侧, 当前值居中HeadlineMedium

### 参数说明

**温度 (Temperature)**:
- 范围: 0.0 - 2.0
- 默认值: 根据 Agent 角色自动调整
- 说明: 控制输出的随机性
  - 0.0-0.5: 精确、一致（适合语法检查）
  - 0.5-1.0: 平衡（适合作文批改）
  - 1.0-2.0: 创造性、多样（适合对话）

**最大长度 (Max Tokens)**:
- 范围: 256 - 4096
- 默认值: 根据 Agent 角色自动调整
- 说明: 控制生成文本的最大长度
  - 256-512: 短回复（单词解释、对话）
  - 512-1024: 中等长度（语法检查）
  - 1024-2048: 长文本（作文批改、学习规划）

**Top P (核采样)**:
- 范围: 0.0 - 1.0
- 默认值: 0.9
- 说明: 控制输出的多样性
  - 0.5-0.7: 保守、稳定
  - 0.8-0.9: 平衡（推荐）
  - 0.9-1.0: 多样、创新

### 恢复默认按钮
- 宽度: match_parent - 32.dp
- 高度: 48.dp
- 圆角: CornerRadius.medium (12.dp)
- 背景: 透明，边框1.dp主题色
- 文本: "恢复默认设置", LabelLarge, 主题色
- 边距: 水平16.dp, 顶部24.dp, 底部16.dp
- 功能: 恢复当前 Agent 角色的默认参数

---

## 状态管理

```kotlin
data class ModelSettingsUiState(
    val selectedTabIndex: Int = 0,  // 当前选中的选项卡索引
    val agentPrompts: Map<AgentRole, String> = emptyMap(),  // 每个角色的提示词
    val currentPrompt: String = "",  // 当前显示的提示词
    val temperature: Float = 0.7f,
    val maxTokens: Int = 512,
    val topP: Float = 0.9f,
    val isLoading: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)

enum class AgentRole(
    val displayName: String,
    val icon: String,
    val defaultPrompt: String,
    val defaultTemperature: Float,
    val defaultMaxTokens: Int
) {
    VOCABULARY_TUTOR(
        displayName = "📚 单词学习",
        icon = "📚",
        defaultPrompt = """你是一个专业的英语单词学习助手。
你的任务是：
1. 用简洁易懂的方式解释单词
2. 提供实用的例句和记忆技巧
3. 帮助学生快速掌握单词用法
4. 保持友好、鼓励的语气""",
        defaultTemperature = 0.7f,
        defaultMaxTokens = 512
    ),
    
    GRAMMAR_CHECKER(
        displayName = "🔍 语法检查",
        icon = "🔍",
        defaultPrompt = """你是一个专业的英语语法检查工具。
你的任务是：
1. 准确识别语法错误
2. 给出清晰的修改建议
3. 解释错误原因
4. 保持客观、专业的语气""",
        defaultTemperature = 0.3f,
        defaultMaxTokens = 1024
    ),
    
    ESSAY_REVIEWER(
        displayName = "✍️ 作文批改",
        icon = "✍️",
        defaultPrompt = """你是一个经验丰富的英语写作老师。
你的任务是：
1. 全面评价学生的作文
2. 指出优点和需要改进的地方
3. 给出具体的改进建议
4. 保持鼓励、建设性的语气""",
        defaultTemperature = 0.5f,
        defaultMaxTokens = 2048
    ),
    
    SPEAKING_PARTNER(
        displayName = "💬 口语陪练",
        icon = "💬",
        defaultPrompt = """你是一个友好的英语口语陪练。
你的任务是：
1. 用自然的英语进行对话
2. 委婉地纠正语法错误
3. 给出发音建议
4. 保持对话轻松、有趣""",
        defaultTemperature = 0.8f,
        defaultMaxTokens = 512
    ),
    
    LEARNING_PLANNER(
        displayName = "📊 学习规划",
        icon = "📊",
        defaultPrompt = """你是一个专业的英语学习规划师。
你的任务是：
1. 分析学生的学习状态
2. 制定个性化的学习计划
3. 给出实用的学习建议
4. 保持专业、激励的语气""",
        defaultTemperature = 0.5f,
        defaultMaxTokens = 1024
    )
}

sealed class ModelSettingsEvent {
    data class SelectTab(val index: Int) : ModelSettingsEvent()
    data class UpdatePrompt(val prompt: String) : ModelSettingsEvent()
    object SavePrompt : ModelSettingsEvent()
    object ResetToPreset : ModelSettingsEvent()
    data class UpdateTemperature(val value: Float) : ModelSettingsEvent()
    data class UpdateMaxTokens(val value: Int) : ModelSettingsEvent()
    data class UpdateTopP(val value: Float) : ModelSettingsEvent()
    object RestoreDefaults : ModelSettingsEvent()
}
```

---

## 后端代码

### AgentService 接口

```kotlin
/**
 * Agent 服务接口
 * 管理不同的 Agent 角色和系统提示词
 */
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

### AgentService 实现

```kotlin
class AgentServiceImpl @Inject constructor(
    private val llmService: LLMService,
    private val userSettingsRepository: UserSettingsRepository
) : AgentService {
    
    private var currentAgent: AgentRole = AgentRole.VOCABULARY_TUTOR
    private var promptMode: PromptMode = PromptMode.PRESET
    private var customPrompt: String = ""
    
    init {
        // 从设置中加载
        viewModelScope.launch {
            val settings = userSettingsRepository.getUserSettings().getOrNull()
            settings?.let {
                currentAgent = it.selectedAgent
                promptMode = it.promptMode
                customPrompt = it.customPrompt
            }
        }
    }
    
    override fun getCurrentAgent(): AgentRole = currentAgent
    
    override suspend fun switchAgent(agent: AgentRole): Result<Unit> {
        return try {
            currentAgent = agent
            // 保存到设置
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
    
    override suspend fun updateCustomPrompt(prompt: String): Result<Unit> {
        return try {
            customPrompt = prompt
            promptMode = PromptMode.CUSTOM
            userSettingsRepository.updateAgentSettings(
                agent = currentAgent,
                mode = PromptMode.CUSTOM,
                customPrompt = prompt
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun resetToPreset(): Result<Unit> {
        return try {
            promptMode = PromptMode.PRESET
            customPrompt = ""
            userSettingsRepository.updateAgentSettings(
                agent = currentAgent,
                mode = PromptMode.PRESET,
                customPrompt = ""
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
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

### ViewModel

```kotlin
class ModelSettingsViewModel @Inject constructor(
    private val agentService: AgentService,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ModelSettingsUiState())
    val uiState: StateFlow<ModelSettingsUiState> = _uiState.asStateFlow()
    
    private val agentRoles = AgentRole.values()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            val settings = userSettingsRepository.getUserSettings().getOrNull()
            settings?.let {
                // 加载所有角色的提示词
                val prompts = agentRoles.associate { role ->
                    role to (it.agentPrompts[role] ?: role.defaultPrompt)
                }
                
                _uiState.update { state ->
                    state.copy(
                        agentPrompts = prompts,
                        currentPrompt = prompts[agentRoles[0]] ?: "",
                        temperature = it.modelTemperature,
                        maxTokens = it.modelMaxTokens,
                        topP = it.modelTopP
                    )
                }
            }
        }
    }
    
    fun onEvent(event: ModelSettingsEvent) {
        when (event) {
            is ModelSettingsEvent.SelectTab -> selectTab(event.index)
            is ModelSettingsEvent.UpdatePrompt -> updatePrompt(event.prompt)
            is ModelSettingsEvent.SavePrompt -> savePrompt()
            is ModelSettingsEvent.ResetToPreset -> resetToPreset()
            is ModelSettingsEvent.UpdateTemperature -> updateTemperature(event.value)
            is ModelSettingsEvent.UpdateMaxTokens -> updateMaxTokens(event.value)
            is ModelSettingsEvent.UpdateTopP -> updateTopP(event.value)
            is ModelSettingsEvent.RestoreDefaults -> restoreDefaults()
        }
    }
    
    private fun selectTab(index: Int) {
        if (index !in agentRoles.indices) return
        
        val role = agentRoles[index]
        val prompt = _uiState.value.agentPrompts[role] ?: role.defaultPrompt
        
        _uiState.update { state ->
            state.copy(
                selectedTabIndex = index,
                currentPrompt = prompt,
                temperature = role.defaultTemperature,
                maxTokens = role.defaultMaxTokens,
                hasUnsavedChanges = false
            )
        }
    }
    
    private fun updatePrompt(prompt: String) {
        _uiState.update { state ->
            state.copy(
                currentPrompt = prompt,
                hasUnsavedChanges = true
            )
        }
    }
    
    private fun savePrompt() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val currentRole = agentRoles[_uiState.value.selectedTabIndex]
            val updatedPrompts = _uiState.value.agentPrompts.toMutableMap()
            updatedPrompts[currentRole] = _uiState.value.currentPrompt
            
            // 保存到设置
            userSettingsRepository.updateAgentPrompts(updatedPrompts)
            
            // 如果当前页面使用的是这个角色，更新 AgentService
            if (agentService.getCurrentAgent() == currentRole) {
                agentService.updateCustomPrompt(_uiState.value.currentPrompt)
            }
            
            _uiState.update { state ->
                state.copy(
                    agentPrompts = updatedPrompts,
                    isLoading = false,
                    hasUnsavedChanges = false
                )
            }
        }
    }
    
    private fun resetToPreset() {
        val currentRole = agentRoles[_uiState.value.selectedTabIndex]
        val presetPrompt = currentRole.defaultPrompt
        
        _uiState.update { state ->
            state.copy(
                currentPrompt = presetPrompt,
                hasUnsavedChanges = true
            )
        }
    }
    
    private fun updateTemperature(value: Float) {
        _uiState.update { it.copy(temperature = value) }
        saveModelSettings()
    }
    
    private fun updateMaxTokens(value: Int) {
        _uiState.update { it.copy(maxTokens = value) }
        saveModelSettings()
    }
    
    private fun updateTopP(value: Float) {
        _uiState.update { it.copy(topP = value) }
        saveModelSettings()
    }
    
    private fun restoreDefaults() {
        val currentRole = agentRoles[_uiState.value.selectedTabIndex]
        _uiState.update { state ->
            state.copy(
                temperature = currentRole.defaultTemperature,
                maxTokens = currentRole.defaultMaxTokens,
                topP = 0.9f
            )
        }
        saveModelSettings()
    }
    
    private fun saveModelSettings() {
        viewModelScope.launch {
            userSettingsRepository.updateModelSettings(
                temperature = _uiState.value.temperature,
                maxTokens = _uiState.value.maxTokens,
                topP = _uiState.value.topP
            )
        }
    }
}
```
    val uiState: StateFlow<ModelSettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            val settings = userSettingsRepository.getUserSettings().getOrNull()
            settings?.let {
                _uiState.update { state ->
                    state.copy(
                        selectedAgent = it.selectedAgent,
                        promptMode = it.promptMode,
                        customPrompt = it.customPrompt,
                        temperature = it.modelTemperature,
                        maxTokens = it.modelMaxTokens,
                        topP = it.modelTopP
                    )
                }
            }
        }
    }
    
    fun onEvent(event: ModelSettingsEvent) {
        when (event) {
            is ModelSettingsEvent.SelectAgent -> selectAgent(event.agent)
            is ModelSettingsEvent.SwitchPromptMode -> switchPromptMode(event.mode)
            is ModelSettingsEvent.UpdateCustomPrompt -> updateCustomPrompt(event.prompt)
            is ModelSettingsEvent.SaveCustomPrompt -> saveCustomPrompt()
            is ModelSettingsEvent.ResetToPreset -> resetToPreset()
            is ModelSettingsEvent.UpdateTemperature -> updateTemperature(event.value)
            is ModelSettingsEvent.UpdateMaxTokens -> updateMaxTokens(event.value)
            is ModelSettingsEvent.UpdateTopP -> updateTopP(event.value)
            is ModelSettingsEvent.RestoreDefaults -> restoreDefaults()
        }
    }
    
    private fun selectAgent(agent: AgentRole) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            agentService.switchAgent(agent)
            
            _uiState.update { state ->
                state.copy(
                    selectedAgent = agent,
                    temperature = agent.defaultTemperature,
                    maxTokens = agent.defaultMaxTokens,
                    isLoading = false
                )
            }
        }
    }
    
    private fun switchPromptMode(mode: PromptMode) {
        _uiState.update { it.copy(promptMode = mode) }
    }
    
    private fun updateCustomPrompt(prompt: String) {
        _uiState.update { it.copy(customPrompt = prompt) }
    }
    
    private fun saveCustomPrompt() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            agentService.updateCustomPrompt(_uiState.value.customPrompt)
            
            _uiState.update { it.copy(isLoading = false) }
        }
    }
    
    private fun resetToPreset() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            agentService.resetToPreset()
            
            _uiState.update { state ->
                state.copy(
                    promptMode = PromptMode.PRESET,
                    customPrompt = "",
                    isLoading = false
                )
            }
        }
    }
    
    private fun updateTemperature(value: Float) {
        _uiState.update { it.copy(temperature = value) }
        saveModelSettings()
    }
    
    private fun updateMaxTokens(value: Int) {
        _uiState.update { it.copy(maxTokens = value) }
        saveModelSettings()
    }
    
    private fun updateTopP(value: Float) {
        _uiState.update { it.copy(topP = value) }
        saveModelSettings()
    }
    
    private fun restoreDefaults() {
        val agent = _uiState.value.selectedAgent
        _uiState.update { state ->
            state.copy(
                temperature = agent.defaultTemperature,
                maxTokens = agent.defaultMaxTokens,
                topP = 0.9f
            )
        }
        saveModelSettings()
    }
    
    private fun saveModelSettings() {
        viewModelScope.launch {
            userSettingsRepository.updateModelSettings(
                temperature = _uiState.value.temperature,
                maxTokens = _uiState.value.maxTokens,
                topP = _uiState.value.topP
            )
        }
    }
}
```

---

## 数据持久化

### UserSettings 扩展

```kotlin
data class UserSettings(
    // ... 其他设置 ...
    
    // Agent 提示词（每个角色独立保存）
    val agentPrompts: Map<AgentRole, String> = emptyMap(),
    
    // 模型参数
    val modelTemperature: Float = 0.7f,
    val modelMaxTokens: Int = 512,
    val modelTopP: Float = 0.9f
)
```

### Repository 方法

```kotlin
interface UserSettingsRepository {
    // ... 其他方法 ...
    
    suspend fun updateAgentPrompts(
        prompts: Map<AgentRole, String>
    ): Result<Unit>
    
    suspend fun updateModelSettings(
        temperature: Float,
        maxTokens: Int,
        topP: Float
    ): Result<Unit>
}
```

---

## 使用示例

### Agent 自动切换机制

Agent 角色会根据用户所在的页面自动切换，无需手动选择：

```kotlin
// 在 MainScreen 或 NavGraph 中实现自动切换
@Composable
fun MainScreen(navController: NavHostController) {
    val currentRoute by navController.currentBackStackEntryAsState()
    val agentService: AgentService = hiltViewModel()
    
    // 根据当前路由自动切换 Agent
    LaunchedEffect(currentRoute?.destination?.route) {
        val agent = when (currentRoute?.destination?.route) {
            Screen.Vocabulary.route -> AgentRole.VOCABULARY_TUTOR
            Screen.Speaking.route -> AgentRole.SPEAKING_PARTNER
            Screen.Writing.route -> AgentRole.ESSAY_REVIEWER
            Screen.Settings.route -> AgentRole.LEARNING_PLANNER
            else -> AgentRole.VOCABULARY_TUTOR
        }
        agentService.switchAgent(agent)
    }
    
    // ... 其他 UI 代码
}
```

### 在单词学习中使用

```kotlin
class VocabularyViewModel @Inject constructor(
    private val agentService: AgentService
) : ViewModel() {
    
    fun explainWord(word: String) {
        viewModelScope.launch {
            // Agent 已自动切换为 VOCABULARY_TUTOR
            // 直接使用当前 Agent 生成解释
            val result = agentService.generate(
                userInput = "请解释单词: $word",
                context = emptyList()
            )
            
            // 处理结果...
        }
    }
}
```

### 在口语训练中使用

```kotlin
class SpeakingViewModel @Inject constructor(
    private val agentService: AgentService
) : ViewModel() {
    
    fun startConversation() {
        viewModelScope.launch {
            // Agent 已自动切换为 SPEAKING_PARTNER
            // 生成开场白
            val result = agentService.generate(
                userInput = "Let's start a conversation",
                context = emptyList()
            )
            
            // 处理结果...
        }
    }
}
```

### 在写作练习中使用

```kotlin
class WritingViewModel @Inject constructor(
    private val agentService: AgentService
) : ViewModel() {
    
    fun checkGrammar(text: String) {
        viewModelScope.launch {
            // 临时切换到语法检查助手
            val previousAgent = agentService.getCurrentAgent()
            agentService.switchAgent(AgentRole.GRAMMAR_CHECKER)
            
            // 检查语法
            val result = agentService.generate(
                userInput = "请检查语法: $text",
                context = emptyList()
            )
            
            // 恢复之前的 Agent
            agentService.switchAgent(previousAgent)
            
            // 处理结果...
        }
    }
    
    fun reviewEssay(essay: String) {
        viewModelScope.launch {
            // Agent 已自动切换为 ESSAY_REVIEWER
            // 批改作文
            val result = agentService.generate(
                userInput = "请批改作文: $essay",
                context = emptyList()
            )
            
            // 处理结果...
        }
    }
}
```

### 在模型设置页面中

用户可以查看和编辑每个 Agent 角色的系统提示词：

```kotlin
@Composable
fun ModelSettingsScreen(
    viewModel: ModelSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column {
        // 角色选项卡
        ScrollableTabRow(
            selectedTabIndex = uiState.selectedTabIndex,
            onTabSelected = { index ->
                viewModel.onEvent(ModelSettingsEvent.SelectTab(index))
            }
        )
        
        // 提示词编辑框
        TextField(
            value = uiState.currentPrompt,
            onValueChange = { prompt ->
                viewModel.onEvent(ModelSettingsEvent.UpdatePrompt(prompt))
            },
            modifier = Modifier.fillMaxWidth().height(300.dp)
        )
        
        // 操作按钮
        Row {
            Button(
                onClick = { viewModel.onEvent(ModelSettingsEvent.SavePrompt) },
                enabled = uiState.hasUnsavedChanges
            ) {
                Text("保存")
            }
            
            Button(
                onClick = { viewModel.onEvent(ModelSettingsEvent.ResetToPreset) }
            ) {
                Text("重置为预设")
            }
        }
    }
}
```
