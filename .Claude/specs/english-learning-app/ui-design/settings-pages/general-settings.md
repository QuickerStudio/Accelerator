# 通用设置页面 (General Settings)

## 设计思路

通用设置页面包含学习设置、权限管理、关于等设置选项。作为离线应用，重点关注本地功能设置，如学习提醒、权限管理等。采用分组卡片布局，清晰分类。

---

## 布局结构
```
┌─────────────────────────────────┐
│  顶部导航栏                       │
│  ←  设置                         │
├─────────────────────────────────┤
│                                 │
│  学习设置                        │
│  ┌─────────────────────────┐   │
│  │ ⏰ 学习提醒           [●] │   │
│  │ 🎯 每日学习目标       >  │   │
│  │ � 学习计划           >  │   │
│  │ � 学习统计显示       >  │   │
│  └─────────────────────────┘   │
│                                 │
│  自动朗读设置                    │
│  ┌─────────────────────────┐   │
│  │ 📖 每日自动朗读英语文本 [●] │   │
│  │ 📚 每日自动朗读英语单词 [●] │   │
│  │ 📝 每日自动阅读英语语法 [●] │   │
│  │                         │   │
│  │ 日期时间设置             │   │
│  │ 每周哪些天:              │   │
│  │ [周一][周二][周三][周四] │   │
│  │ [周五][周六][周日]       │   │
│  │                         │   │
│  │ 每日几点: [20:00] [设置] │   │
│  └─────────────────────────┘   │
│                                 │
│  权限管理                        │
│  ┌─────────────────────────┐   │
│  │ 🔔 通知权限           >  │   │
│  │ � 麦克风权限         >  │   │
│  │ 📷 相机权限           >  │   │
│  │ � 存储权限           >  │   │
│  └─────────────────────────┘   │
│                                 │
│  其他                           │
│  ┌─────────────────────────┐   │
│  │ ℹ️ 版本号      0.0.1  >  │   │
│  │ 🏢 公司信息 QuickerStudio >│   │
│  │ 📄 用户协议           >  │   │
│  │ 🔄 检查更新           >  │   │
│  └─────────────────────────┘   │
│                                 │
│  数据管理                 [▼]   │
│  ┌─────────────────────────┐   │
│  │ 🗑️ 清除缓存           >  │   │
│  │ 📥 导入数据  📤 导出数据 │   │
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
  - **返回按钮** (←): 48.dp × 48.dp, 图标24.dp白色
  - **标题**: "设置", HeadlineMedium (20.sp), 白色

### 分组标题
- 文本: HeadlineSmall (16.sp), 白色, 粗体
- 内边距: 水平16.dp, 顶部24.dp, 底部12.dp
- 分组: 学习设置、自动朗读设置、权限管理、其他、数据管理

### 可折叠分组标题（数据管理）
- 文本: HeadlineSmall (16.sp), 白色, 粗体
- 内边距: 水平16.dp, 顶部24.dp, 底部12.dp
- 右侧图标: 
  - 展开状态: ▼ (向下箭头)
  - 折叠状态: ▶ (向右箭头)
  - 尺寸: 20.dp, 灰色 (#94A3B8)
- 点击: 切换展开/折叠状态
- 动画: 旋转动画，时长200ms

### 设置卡片
- 宽度: match_parent - 32.dp
- 高度: wrap_content
- 圆角: CornerRadius.large (16.dp)
- 背景: 深色卡片 (#2A2A2A)
- 边距: 水平16.dp, 底部12.dp

### 设置项
- 高度: 56.dp
- 内边距: 水平16.dp
- 布局: 水平排列
- 分隔线: 0.5.dp, 深灰 (#3A3A3A), 除最后一项
- 点击效果: 背景变为深灰 (#3A3A3A)
- 组件:
  - **图标**: 24.dp, 主题色, 左侧
  - **标题**: BodyLarge (16.sp), 白色, 图标右侧12.dp
  - **开关/箭头**: 右侧
    - 开关: 48.dp × 28.dp, 主题色
    - 箭头: 20.dp, 灰色 (#94A3B8)

### 开关样式
- 宽度: 48.dp
- 高度: 28.dp
- 圆角: CornerRadius.full
- 背景: 关闭时灰色 (#475569), 开启时主题色
- 滑块: 24.dp圆形, 白色
- 动画: 滑动动画，时长200ms

### 日期选择按钮（自动朗读设置）
- 宽度: 48.dp
- 高度: 36.dp
- 圆角: 8.dp
- 间距: 8.dp
- 未选中状态:
  - 背景: 透明
  - 边框: 1.dp, 灰色 (#475569)
  - 文字: 灰色 (#94A3B8), BodyMedium (14.sp)
- 选中状态:
  - 背景: 主题色
  - 边框: 无
  - 文字: 白色, BodyMedium (14.sp)
- 点击效果: 缩放动画 (0.95x)

### 时间输入框（自动朗读设置）
- 宽度: 120.dp
- 高度: 48.dp
- 圆角: 8.dp
- 背景: 深色卡片 (#2A2A2A)
- 边框: 1.dp, 灰色 (#475569)
- 文字: 白色, BodyLarge (16.sp), 居中
- 内边距: 水平16.dp
- 点击: 打开时间选择器对话框

### 设置按钮（自动朗读设置）
- 宽度: 80.dp
- 高度: 48.dp
- 圆角: 8.dp
- 背景: 主题色
- 文字: "设置", 白色, LabelLarge (14.sp)
- 点击效果: 背景变暗 (80% 透明度)

---

## 设置项详细说明

### 学习设置

#### 1. ⏰ 学习提醒 - 开关
- 功能: 开启/关闭学习提醒通知
- 开启后: 在设定的时间发送本地通知提醒用户学习
- 默认: 开启

#### 2. 🎯 每日学习目标 - 箭头
- 功能: 设置每日学习目标
- 点击进入: 目标设置页面
- 可设置项:
  - 每日学习单词数（默认: 20个）
  - 每日学习时长（默认: 30分钟）
  - 每日写作篇数（默认: 1篇）
  - 每日对话次数（默认: 3次）

#### 3. 📅 学习计划 - 箭头
- 功能: 设置学习计划和提醒时间
- 点击进入: 学习计划页面
- 可设置项:
  - 学习提醒时间（默认: 20:00）
  - 学习日期（周一至周日选择）
  - 提醒方式（通知、声音、震动）
  - 提醒内容自定义

#### 4. 📊 学习统计显示 - 箭头
- 功能: 设置学习统计的显示方式
- 点击进入: 统计显示设置页面
- 可设置项:
  - 显示周期（日/周/月）
  - 显示内容（单词/写作/口语/语法）
  - 图表类型（折线图/柱状图/饼图）

---

### 自动朗读设置

#### 1. 📖 每日自动朗读英语文本 - 开关
- 功能: 开启/关闭每日自动朗读英语文本功能
- 开启后: 在设定的时间自动朗读英语文本内容
- 默认: 开启

#### 2. 📚 每日自动朗读英语单词 - 开关
- 功能: 开启/关闭每日自动朗读英语单词功能
- 开启后: 在设定的时间自动朗读今日学习的单词
- 默认: 开启

#### 3. 📝 每日自动阅读英语语法 - 开关
- 功能: 开启/关闭每日自动阅读英语语法功能
- 开启后: 在设定的时间自动朗读语法知识点
- 默认: 开启

#### 4. 日期时间设置

##### 每周哪些天
- 功能: 选择每周哪些天进行自动朗读
- 显示: 7个按钮（周一至周日）
- 交互: 多选，点击切换选中/未选中状态
- 按钮样式:
  - 未选中: 边框1.dp灰色，背景透明，文字灰色
  - 选中: 背景主题色，文字白色
  - 尺寸: 48.dp × 36.dp
  - 圆角: 8.dp
  - 间距: 8.dp
- 默认: 周一至周五选中

##### 每日几点
- 功能: 设置每日自动朗读的时间
- 显示: 时间输入框 + 设置按钮
- 时间输入框:
  - 宽度: 120.dp
  - 高度: 48.dp
  - 圆角: 8.dp
  - 背景: 深色 (#2A2A2A)
  - 文字: 白色，BodyLarge (16.sp)
  - 默认值: "20:00"
  - 点击: 打开时间选择器
- 设置按钮:
  - 宽度: 80.dp
  - 高度: 48.dp
  - 圆角: 8.dp
  - 背景: 主题色
  - 文字: "设置"，白色，LabelLarge
  - 点击: 保存时间设置

---

### 权限管理

#### 1. 🔔 通知权限 - 箭头
- 功能: 管理应用通知权限
- 点击进入: 系统通知权限设置
- 说明: 用于学习提醒功能
- 状态显示: 已授权/未授权

#### 2. 🎤 麦克风权限 - 箭头
- 功能: 管理麦克风权限
- 点击进入: 系统麦克风权限设置
- 说明: 用于口语练习录音功能
- 状态显示: 已授权/未授权

#### 3. 📷 相机权限 - 箭头
- 功能: 管理相机权限
- 点击进入: 系统相机权限设置
- 说明: 用于拍照识别单词功能
- 状态显示: 已授权/未授权

#### 4. 📁 存储权限 - 箭头
- 功能: 管理存储权限
- 点击进入: 系统存储权限设置
- 说明: 用于保存学习数据和导出功能
- 状态显示: 已授权/未授权

---

### 其他

#### 1. ℹ️ 版本号 - 箭头
- 功能: 查看应用版本信息
- 点击进入: 版本信息页面
- 显示内容:
  - 应用名称: "英语学习助手"
  - 版本号: "v0.0.1"
  - 版本代码: "Build 1"
  - 更新日期: "2026-03-01"
  - 更新日志

#### 2. 🏢 公司信息 - 箭头
- 功能: 查看公司/开发者信息
- 点击进入: 公司信息页面
- 显示内容:
  - 公司名称: "QuickerStudio"
  - 公司简介
  - 联系方式
  - 官方网站
  - 社交媒体

#### 3. 📄 用户协议 - 箭头
- 功能: 查看用户协议和隐私政策
- 点击进入: 协议查看页面
- 内容: 用户协议、隐私政策、免责声明

#### 4. 🔄 检查更新 - 箭头
- 功能: 检查应用更新
- 点击: 检查是否有新版本
- 有更新: 显示更新内容和下载按钮
- 无更新: 提示"已是最新版本"

---

### 数据管理（可折叠）

#### 1. 🗑️ 清除缓存 - 箭头
- 功能: 清除应用缓存数据
- 点击: 显示确认对话框
- 确认后: 清除缓存，显示清除的数据大小
- 说明: 不会删除学习数据，仅清除临时文件

#### 2. 导入/导出数据 - 并排按钮
- 布局: 水平排列，两个按钮并排显示
- 间距: 12.dp

##### 📥 导入数据按钮
- 宽度: (卡片宽度 - 32.dp - 12.dp) / 2
- 高度: 56.dp
- 圆角: 8.dp
- 背景: 深色 (#2A2A2A)
- 边框: 1.dp, 主题色
- 图标: 📥, 24.dp, 主题色, 左侧
- 文字: "导入数据", BodyMedium (14.sp), 白色
- 点击: 进入数据导入页面
- 功能:
  - 从文件中导入学习数据
  - 支持格式: JSON/CSV/TXT
  - 可导入: 单词列表、学习记录、设置配置

##### 📤 导出数据按钮
- 宽度: (卡片宽度 - 32.dp - 12.dp) / 2
- 高度: 56.dp
- 圆角: 8.dp
- 背景: 深色 (#2A2A2A)
- 边框: 1.dp, 主题色
- 图标: 📤, 24.dp, 主题色, 左侧
- 文字: "导出数据", BodyMedium (14.sp), 白色
- 点击: 进入数据导出页面
- 功能:
  - 导出学习数据到文件
  - 支持格式: JSON/CSV/TXT
  - 可导出: 单词列表、写作文章、对话记录、学习统计

---

## 交互流程

### 切换学习提醒
1. 用户点击"学习提醒"开关
2. 开关状态切换（开/关）
3. 开启时: 请求通知权限（如未授权）
4. 关闭时: 取消所有学习提醒
5. 自动保存设置

### 设置每日学习目标
1. 用户点击"每日学习目标"
2. 进入目标设置页面
3. 调整各项学习目标数值
4. 点击保存或自动保存
5. 返回设置页面

### 设置学习计划
1. 用户点击"学习计划"
2. 进入学习计划页面
3. 设置提醒时间（时间选择器）
4. 选择学习日期（周一至周日）
5. 设置提醒方式（通知、声音、震动）
6. 自定义提醒内容
7. 保存设置

### 设置自动朗读
1. 用户切换自动朗读开关（文本/单词/语法）
2. 开关状态切换（开/关）
3. 自动保存设置
4. 用户点击日期按钮选择每周哪些天
5. 按钮状态切换（选中/未选中）
6. 用户点击时间输入框
7. 打开时间选择器
8. 用户选择时间
9. 用户点击"设置"按钮
10. 保存时间设置
11. 显示保存成功提示

### 管理权限
1. 用户点击权限项
2. 跳转到系统权限设置页面
3. 用户授权或拒绝权限
4. 返回应用，更新权限状态显示

### 折叠/展开数据管理
1. 用户点击"数据管理"标题
2. 箭头图标旋转动画（▼ ↔ ▶）
3. 数据管理卡片展开/折叠动画
4. 保存折叠状态到本地

### 清除缓存
1. 用户点击"清除缓存"
2. 显示确认对话框: "确定要清除缓存吗？这不会删除您的学习数据。"
3. 用户确认后:
   - 清除缓存文件
   - 显示清除的数据大小
   - 提示"缓存已清除"
4. 用户取消: 关闭对话框

### 导出数据
1. 用户点击"导出数据"
2. 进入数据导出页面
3. 选择要导出的内容（多选）
4. 选择导出格式
5. 选择保存位置
6. 点击"导出"按钮
7. 显示导出进度
8. 导出完成，提示保存路径

### 导入数据
1. 用户点击"导入数据"
2. 进入数据导入页面
3. 点击"选择文件"按钮
4. 从文件管理器选择文件
5. 预览导入内容
6. 点击"导入"按钮
7. 显示导入进度
8. 导入完成，提示导入的数据数量

---

## 动画规范

### 页面进入动画
```kotlin
val offsetX by animateFloatAsState(
    targetValue = 0f,
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
)
```

### 开关切换动画
```kotlin
val offsetX by animateFloatAsState(
    targetValue = if (isChecked) 20.dp.toPx() else 0f,
    animationSpec = tween(
        durationMillis = 200,
        easing = FastOutSlowInEasing
    )
)

val backgroundColor by animateColorAsState(
    targetValue = if (isChecked) Primary else Gray,
    animationSpec = tween(200)
)
```

### 设置项点击动画
```kotlin
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.98f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy
    )
)
```

### 日期按钮点击动画
```kotlin
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.95f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
    )
)

val backgroundColor by animateColorAsState(
    targetValue = if (isSelected) Primary else Color.Transparent,
    animationSpec = tween(200)
)

val borderColor by animateColorAsState(
    targetValue = if (isSelected) Color.Transparent else Gray,
    animationSpec = tween(200)
)
```

### 数据管理折叠动画
```kotlin
// 箭头旋转动画
val rotation by animateFloatAsState(
    targetValue = if (isExpanded) 0f else -90f,
    animationSpec = tween(
        durationMillis = 200,
        easing = FastOutSlowInEasing
    )
)

// 内容展开/折叠动画
AnimatedVisibility(
    visible = isExpanded,
    enter = expandVertically(
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeIn(
        animationSpec = tween(200)
    ),
    exit = shrinkVertically(
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeOut(
        animationSpec = tween(200)
    )
) {
    // 数据管理卡片内容
}
```

---

## 状态管理

### 通用设置状态
```kotlin
data class GeneralSettings(
    // 学习设置
    val learningReminderEnabled: Boolean = true,
    val dailyWordGoal: Int = 20,
    val dailyStudyMinutes: Int = 30,
    val dailyWritingGoal: Int = 1,
    val dailySpeakingGoal: Int = 3,
    val reminderTime: String = "20:00",
    val reminderDays: Set<DayOfWeek> = setOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    ),
    val reminderSound: Boolean = true,
    val reminderVibration: Boolean = true,
    
    // 自动朗读设置
    val autoReadTextEnabled: Boolean = true,
    val autoReadWordsEnabled: Boolean = true,
    val autoReadGrammarEnabled: Boolean = true,
    val autoReadDays: Set<DayOfWeek> = setOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    ),
    val autoReadTime: String = "20:00",
    
    // 权限状态
    val notificationPermission: PermissionStatus = PermissionStatus.GRANTED,
    val microphonePermission: PermissionStatus = PermissionStatus.GRANTED,
    val cameraPermission: PermissionStatus = PermissionStatus.NOT_GRANTED,
    val storagePermission: PermissionStatus = PermissionStatus.GRANTED,
    
    // 应用信息
    val appVersion: String = "0.0.1",
    val buildNumber: Int = 1,
    val updateDate: String = "2026-03-01",
    val companyName: String = "QuickerStudio",
    
    // UI状态
    val dataManagementExpanded: Boolean = false
)

enum class PermissionStatus {
    GRANTED,        // 已授权
    NOT_GRANTED,    // 未授权
    DENIED          // 已拒绝
}

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
```

### ViewModel
```kotlin
class GeneralSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val permissionManager: PermissionManager,
    private val notificationManager: NotificationManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GeneralSettings())
    val uiState: StateFlow<GeneralSettings> = _uiState.asStateFlow()
    
    fun toggleLearningReminder(enabled: Boolean) {
        _uiState.update { it.copy(learningReminderEnabled = enabled) }
        if (enabled) {
            scheduleReminders()
        } else {
            cancelReminders()
        }
        saveSettings()
    }
    
    fun updateDailyGoal(wordGoal: Int, studyMinutes: Int, writingGoal: Int, speakingGoal: Int) {
        _uiState.update {
            it.copy(
                dailyWordGoal = wordGoal,
                dailyStudyMinutes = studyMinutes,
                dailyWritingGoal = writingGoal,
                dailySpeakingGoal = speakingGoal
            )
        }
        saveSettings()
    }
    
    fun toggleAutoReadText(enabled: Boolean) {
        _uiState.update { it.copy(autoReadTextEnabled = enabled) }
        if (enabled) {
            scheduleAutoRead()
        } else {
            cancelAutoRead()
        }
        saveSettings()
    }
    
    fun toggleAutoReadWords(enabled: Boolean) {
        _uiState.update { it.copy(autoReadWordsEnabled = enabled) }
        if (enabled) {
            scheduleAutoRead()
        } else {
            cancelAutoRead()
        }
        saveSettings()
    }
    
    fun toggleAutoReadGrammar(enabled: Boolean) {
        _uiState.update { it.copy(autoReadGrammarEnabled = enabled) }
        if (enabled) {
            scheduleAutoRead()
        } else {
            cancelAutoRead()
        }
        saveSettings()
    }
    
    fun updateAutoReadDays(days: Set<DayOfWeek>) {
        _uiState.update { it.copy(autoReadDays = days) }
        scheduleAutoRead()
        saveSettings()
    }
    
    fun updateAutoReadTime(time: String) {
        _uiState.update { it.copy(autoReadTime = time) }
        scheduleAutoRead()
        saveSettings()
    }
    
    private fun scheduleAutoRead() {
        viewModelScope.launch {
            // 根据设置安排自动朗读任务
            notificationManager.scheduleAutoRead(
                _uiState.value.autoReadDays,
                _uiState.value.autoReadTime,
                _uiState.value.autoReadTextEnabled,
                _uiState.value.autoReadWordsEnabled,
                _uiState.value.autoReadGrammarEnabled
            )
        }
    }
    
    private fun cancelAutoRead() {
        viewModelScope.launch {
            notificationManager.cancelAutoRead()
        }
    }
    
    fun checkPermissions() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    notificationPermission = permissionManager.checkNotificationPermission(),
                    microphonePermission = permissionManager.checkMicrophonePermission(),
                    cameraPermission = permissionManager.checkCameraPermission(),
                    storagePermission = permissionManager.checkStoragePermission()
                )
            }
        }
    }
    
    fun clearCache() {
        viewModelScope.launch {
            val cacheSize = settingsRepository.clearCache()
            // 显示清除成功提示
        }
    }
    
    fun exportData(dataTypes: Set<DataType>, format: ExportFormat) {
        viewModelScope.launch {
            settingsRepository.exportData(dataTypes, format)
        }
    }
    
    fun importData(filePath: String) {
        viewModelScope.launch {
            settingsRepository.importData(filePath)
        }
    }
    
    fun toggleDataManagementExpanded() {
        _uiState.update { it.copy(dataManagementExpanded = !it.dataManagementExpanded) }
        saveSettings()
    }
    
    private fun saveSettings() {
        viewModelScope.launch {
            settingsRepository.saveGeneralSettings(_uiState.value)
        }
    }
}
```

---

## 数据持久化

### 使用 DataStore 存储
```kotlin
suspend fun saveGeneralSettings(settings: GeneralSettings) {
    dataStore.edit { preferences ->
        preferences[LEARNING_REMINDER_KEY] = settings.learningReminderEnabled
        preferences[DAILY_WORD_GOAL_KEY] = settings.dailyWordGoal
        preferences[DAILY_STUDY_MINUTES_KEY] = settings.dailyStudyMinutes
        preferences[DAILY_WRITING_GOAL_KEY] = settings.dailyWritingGoal
        preferences[DAILY_SPEAKING_GOAL_KEY] = settings.dailySpeakingGoal
        preferences[REMINDER_TIME_KEY] = settings.reminderTime
        preferences[REMINDER_DAYS_KEY] = settings.reminderDays.joinToString(",")
        preferences[REMINDER_SOUND_KEY] = settings.reminderSound
        preferences[REMINDER_VIBRATION_KEY] = settings.reminderVibration
        preferences[AUTO_READ_TEXT_KEY] = settings.autoReadTextEnabled
        preferences[AUTO_READ_WORDS_KEY] = settings.autoReadWordsEnabled
        preferences[AUTO_READ_GRAMMAR_KEY] = settings.autoReadGrammarEnabled
        preferences[AUTO_READ_DAYS_KEY] = settings.autoReadDays.joinToString(",")
        preferences[AUTO_READ_TIME_KEY] = settings.autoReadTime
    }
}
```

---

## 无障碍支持

### 语义化标签
```kotlin
// 开关
Switch(
    modifier = Modifier.semantics {
        contentDescription = "学习提醒，当前${if (checked) "开启" else "关闭"}"
        role = Role.Switch
    }
)

// 设置项
Row(
    modifier = Modifier.semantics(mergeDescendants = true) {
        contentDescription = "每日学习目标，点击设置"
        role = Role.Button
    }
)
```

---

## 实现优先级

### Phase 1 - 基础功能
1. 学习提醒开关
2. 基础设置项显示
3. 版本信息显示

### Phase 2 - 完整功能
1. 每日学习目标设置
2. 学习计划设置
3. 权限管理
4. 清除缓存功能

### Phase 3 - 高级功能
1. 数据导出功能
2. 数据导入功能
3. 检查更新功能
4. 完整的权限管理流程

- 组件:
  - **图标**: 24.dp, 主题色, 左侧
  - **标题**: BodyLarge (16.sp), 白色, 图标右侧12.dp
  - **开关/箭头**: 右侧
    - 开关: 48.dp × 28.dp, 主题色
    - 箭头: 20.dp, 灰色 (#94A3B8)

### 开关样式
- 宽度: 48.dp
- 高度: 28.dp
- 圆角: CornerRadius.full
- 背景: 关闭时灰色 (#475569), 开启时主题色
- 滑块: 24.dp圆形, 白色
- 动画: 滑动动画，时长200ms

### 设置项列表

**通知设置**:
1. 🔔 推送通知 - 开关
2. 📧 邮件通知 - 开关
3. 🔊 声音提醒 - 开关
4. 📳 震动反馈 - 开关

**学习设置**:
1. 🎯 每日学习目标 - 箭头（进入设置页面）
2. ⏰ 学习提醒时间 - 箭头（进入时间选择）
3. 📊 学习统计显示 - 箭头（进入统计设置）

**隐私与安全**:
1. 🔒 修改密码 - 箭头（进入密码修改页面）
2. 👤 隐私设置 - 箭头（进入隐私设置）
3. 🛡️ 数据安全 - 箭头（进入数据安全设置）

**账号管理**:
1. 📱 绑定手机号 - 箭头（进入手机号绑定）
2. 📧 绑定邮箱 - 箭头（进入邮箱绑定）
3. 🔗 第三方账号 - 箭头（进入第三方账号管理）

**其他**:
1. ℹ️ 关于我们 - 箭头（查看应用信息）
2. 📄 用户协议 - 箭头（查看用户协议）
3. 🔄 检查更新 - 箭头（检查应用更新）
4. 🗑️ 清除缓存 - 箭头（清除应用缓存）

### 退出登录按钮
- 宽度: match_parent - 32.dp
- 高度: 48.dp
- 圆角: CornerRadius.medium (12.dp)
- 背景: 透明，边框1.dp红色 (#EF4444)
- 文本: "退出登录", LabelLarge, 红色
- 边距: 水平16.dp, 顶部24.dp, 底部16.dp
- 点击: 显示确认对话框

---

## 交互流程

### 切换开关
1. 用户点击开关
2. 开关状态切换（开/关）
3. 滑块滑动动画
4. 背景颜色变化
5. 自动保存设置

### 进入子设置页面
1. 用户点击带箭头的设置项
2. 页面从右侧滑入
3. 显示详细设置内容
4. 修改后自动保存

### 退出登录
1. 用户点击"退出登录"按钮
2. 显示确认对话框: "确定要退出登录吗？"
3. 用户确认后:
   - 清除用户数据
   - 返回登录页面
4. 用户取消: 关闭对话框

---

## 状态管理

```kotlin
data class GeneralSettings(
    val pushNotification: Boolean = true,
    val emailNotification: Boolean = false,
    val soundAlert: Boolean = true,
    val vibration: Boolean = true,
    val dailyGoal: Int = 30,
    val reminderTime: String = "20:00"
)
```

---

## 数据持久化

```kotlin
suspend fun saveGeneralSettings(settings: GeneralSettings) {
    dataStore.edit { preferences ->
        preferences[PUSH_NOTIFICATION_KEY] = settings.pushNotification
        preferences[EMAIL_NOTIFICATION_KEY] = settings.emailNotification
        preferences[SOUND_ALERT_KEY] = settings.soundAlert
        preferences[VIBRATION_KEY] = settings.vibration
        preferences[DAILY_GOAL_KEY] = settings.dailyGoal
        preferences[REMINDER_TIME_KEY] = settings.reminderTime
    }
}
```

