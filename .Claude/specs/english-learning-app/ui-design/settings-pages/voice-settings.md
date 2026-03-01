# 朗读音色设置页面 (Voice Settings)

## 设计思路

用户可以选择不同的TTS（文本转语音）朗读音色，每种音色都可以试听。支持男声、女声、不同口音和语速调节。页面采用卡片式布局，当前使用的音色以渐变卡片突出显示，其他音色以列表形式展示。

---

## 布局结构
```
┌─────────────────────────────────┐
│  顶部导航栏                       │
│  ←  朗读音色                     │
├─────────────────────────────────┤
│                                 │
│  当前音色                        │
│  ┌─────────────────────────┐   │
│  │ 🔊 美式女声 (Emma)       │   │
│  │ 清晰自然，适合学习        │   │
│  │         [试听] [✓]      │   │
│  └─────────────────────────┘   │
│                                 │
│  推荐音色                        │
│  ┌─────────────────────────┐   │
│  │ 🔊 美式男声 (James)      │   │
│  │ 沉稳专业，发音标准        │   │
│  │         [试听]          │   │
│  └─────────────────────────┘   │
│                                 │
│  ┌─────────────────────────┐   │
│  │ 🔊 英式女声 (Sophie)     │   │
│  │ 优雅动听，英伦口音        │   │
│  │         [试听]          │   │
│  └─────────────────────────┘   │
│                                 │
│  ┌─────────────────────────┐   │
│  │ 🔊 英式男声 (Oliver)     │   │
│  │ 磁性低沉，标准英音        │   │
│  │         [试听]          │   │
│  └─────────────────────────┘   │
│                                 │
│  语速调节                        │
│  ┌─────────────────────────┐   │
│  │ 慢速 ●━━━━━━━━━━ 快速    │   │
│  │        1.0x             │   │
│  └─────────────────────────┘   │
│                                 │
│  音量调节                        │
│  ┌─────────────────────────┐   │
│  │ 小声 ━━━━━━━●━━━ 大声    │   │
│  │        80%              │   │
│  └─────────────────────────┘   │
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
  - **返回按钮** (←):
    - 尺寸: 48.dp × 48.dp
    - 图标: 24.dp, 白色
    - 点击: 返回上一页
  - **标题**:
    - 文本: "朗读音色", HeadlineMedium (20.sp), 白色
    - 位置: 返回按钮右侧

### 当前音色卡片
- 宽度: match_parent - 32.dp
- 高度: wrap_content
- 圆角: CornerRadius.large (16.dp)
- 背景: 主题色渐变 (#6366F1 → #8B5CF6)
- 内边距: 20.dp
- 边距: 水平16.dp, 顶部16.dp
- 阴影: elevation = 4.dp
- 组件:
  - **音色图标**: 🔊, 32.dp, 白色, 左上角
  - **音色名称**: HeadlineMedium (18.sp), 白色, 粗体
  - **音色描述**: BodyMedium (14.sp), 白色透明80%
  - **试听按钮**:
    - 宽度: 80.dp, 高度: 36.dp
    - 圆角: CornerRadius.full
    - 背景: 白色透明20%
    - 文本: "试听", LabelMedium, 白色
    - 位置: 右下角
  - **选中标记**: ✓, 24.dp, 白色, 右上角

### 推荐音色区域
- 标题: "推荐音色", HeadlineSmall (16.sp), 白色, 粗体
- 内边距: 水平16.dp, 顶部24.dp, 底部12.dp

### 音色卡片
- 宽度: match_parent - 32.dp
- 高度: wrap_content
- 圆角: CornerRadius.large (16.dp)
- 背景: 深色卡片 (#2A2A2A)
- 内边距: 16.dp
- 边距: 水平16.dp, 底部12.dp
- 布局: 水平排列
- 点击效果: 背景变为深灰 (#3A3A3A)
- 组件:
  - **音色图标**: 🔊, 24.dp, 主题色, 左侧
  - **音色信息**:
    - 音色名称: BodyLarge (16.sp), 白色
    - 音色描述: BodySmall (12.sp), 灰色 (#94A3B8)
    - 垂直排列，间距4.dp
  - **试听按钮**:
    - 宽度: 64.dp, 高度: 32.dp
    - 圆角: CornerRadius.medium (12.dp)
    - 背景: 主题色
    - 文本: "试听", LabelSmall, 白色
    - 位置: 右侧

### 可用音色列表
1. **美式女声 (Emma)**
   - 描述: 清晰自然，适合学习
   - 特点: 标准美式发音，语调柔和
   
2. **美式男声 (James)**
   - 描述: 沉稳专业，发音标准
   - 特点: 低沉有力，适合正式场合
   
3. **英式女声 (Sophie)**
   - 描述: 优雅动听，英伦口音
   - 特点: 标准英式发音，优雅大方
   
4. **英式男声 (Oliver)**
   - 描述: 磁性低沉，标准英音
   - 特点: 绅士风范，发音纯正

### 语速调节区域
- 标题: "语速调节", HeadlineSmall (16.sp), 白色, 粗体
- 内边距: 水平16.dp, 顶部24.dp, 底部12.dp
- 滑块卡片:
  - 宽度: match_parent - 32.dp
  - 高度: wrap_content
  - 圆角: CornerRadius.large (16.dp)
  - 背景: 深色卡片 (#2A2A2A)
  - 内边距: 20.dp
  - 边距: 水平16.dp
  - 滑块:
    - 范围: 0.5x - 2.0x
    - 默认: 1.0x
    - 步进: 0.1x
    - 颜色: 主题色
    - 轨道高度: 4.dp
    - 滑块尺寸: 20.dp圆形
    - 标签: 左侧"慢速"，右侧"快速"
    - 当前值: 居中显示，HeadlineMedium (20.sp), 白色

### 音量调节区域
- 标题: "音量调节", HeadlineSmall (16.sp), 白色, 粗体
- 内边距: 水平16.dp, 顶部24.dp, 底部12.dp
- 滑块卡片: 样式同语速调节
  - 范围: 0% - 100%
  - 默认: 80%
  - 步进: 5%
  - 标签: 左侧"小声"，右侧"大声"
  - 当前值: 居中显示，HeadlineMedium (20.sp), 白色

---

## 交互流程

### 选择音色
1. 用户进入朗读音色设置页面
2. 查看当前使用的音色（渐变卡片显示）
3. 浏览推荐音色列表
4. 点击音色卡片选择新音色
5. 音色立即切换，当前音色卡片更新
6. 自动保存设置

### 试听音色
1. 用户点击"试听"按钮
2. 播放该音色的示例语音
3. 试听按钮显示播放动画（脉冲效果）
4. 播放完成后动画停止
5. 可以随时点击停止播放

### 调节语速
1. 用户拖动语速滑块
2. 实时显示当前语速值
3. 松手后自动保存设置
4. 下次朗读时应用新语速

### 调节音量
1. 用户拖动音量滑块
2. 实时显示当前音量值
3. 松手后自动保存设置
4. 下次朗读时应用新音量

---

## 动画规范

### 页面进入动画
```kotlin
// 从右侧滑入
val offsetX by animateFloatAsState(
    targetValue = 0f,
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
)
```

### 试听按钮动画
```kotlin
// 播放时脉冲动画
val scale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.1f,
    animationSpec = infiniteRepeatable(
        animation = tween(500, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )
)
```

### 音色卡片点击动画
```kotlin
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.98f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy
    )
)

val backgroundColor by animateColorAsState(
    targetValue = if (isPressed) Color(0xFF3A3A3A) else Color(0xFF2A2A2A),
    animationSpec = tween(200)
)
```

### 滑块拖动动画
```kotlin
val sliderValue by animateFloatAsState(
    targetValue = targetValue,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
)
```

---

## 状态管理

### 音色设置状态
```kotlin
data class VoiceSettings(
    val selectedVoice: Voice,
    val speed: Float = 1.0f,
    val volume: Float = 0.8f,
    val isPlaying: Boolean = false
)

data class Voice(
    val id: String,
    val name: String,
    val displayName: String,
    val description: String,
    val gender: Gender,
    val accent: Accent,
    val sampleUrl: String
)

enum class Gender {
    MALE,   // 男声
    FEMALE  // 女声
}

enum class Accent {
    US,  // 美式
    UK,  // 英式
    AU   // 澳式
}
```

### ViewModel
```kotlin
class VoiceSettingsViewModel @Inject constructor(
    private val ttsService: TTSService,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(VoiceSettings())
    val uiState: StateFlow<VoiceSettings> = _uiState.asStateFlow()
    
    fun selectVoice(voice: Voice) {
        _uiState.update { it.copy(selectedVoice = voice) }
        saveSettings()
    }
    
    fun playVoiceSample(voice: Voice) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPlaying = true) }
            ttsService.playSample(voice.sampleUrl)
            _uiState.update { it.copy(isPlaying = false) }
        }
    }
    
    fun updateSpeed(speed: Float) {
        _uiState.update { it.copy(speed = speed) }
        saveSettings()
    }
    
    fun updateVolume(volume: Float) {
        _uiState.update { it.copy(volume = volume) }
        saveSettings()
    }
    
    private fun saveSettings() {
        viewModelScope.launch {
            settingsRepository.saveVoiceSettings(_uiState.value)
        }
    }
}
```

---

## 数据持久化

### 使用 DataStore 存储
```kotlin
// 保存音色设置
suspend fun saveVoiceSettings(settings: VoiceSettings) {
    dataStore.edit { preferences ->
        preferences[VOICE_ID_KEY] = settings.selectedVoice.id
        preferences[VOICE_SPEED_KEY] = settings.speed
        preferences[VOICE_VOLUME_KEY] = settings.volume
    }
}

// 读取音色设置
val voiceSettingsFlow: Flow<VoiceSettings> = dataStore.data.map { preferences ->
    VoiceSettings(
        selectedVoice = getVoiceById(
            preferences[VOICE_ID_KEY] ?: "emma"
        ),
        speed = preferences[VOICE_SPEED_KEY] ?: 1.0f,
        volume = preferences[VOICE_VOLUME_KEY] ?: 0.8f
    )
}
```

---

## 无障碍支持

### 语义化标签
```kotlin
// 音色卡片
Card(
    modifier = Modifier.semantics(mergeDescendants = true) {
        contentDescription = "${voice.displayName}，${voice.description}"
        role = Role.Button
    }
)

// 试听按钮
Button(
    modifier = Modifier.semantics {
        contentDescription = "试听${voice.displayName}音色"
        role = Role.Button
    }
)

// 语速滑块
Slider(
    modifier = Modifier.semantics {
        contentDescription = "语速调节，当前${speed}倍速"
    }
)

// 音量滑块
Slider(
    modifier = Modifier.semantics {
        contentDescription = "音量调节，当前${volume}%"
    }
)
```

---

## 实现优先级

### Phase 1 - 基础功能
1. 音色列表显示
2. 音色选择功能
3. 设置保存和读取

### Phase 2 - 完整功能
1. 音色试听功能
2. 语速调节
3. 音量调节
4. 播放动画

### Phase 3 - 优化
1. 音色预加载
2. 性能优化
3. 无障碍支持
4. 错误处理
