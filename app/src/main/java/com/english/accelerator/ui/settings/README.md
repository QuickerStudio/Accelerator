# Settings UI 架构规范

## 架构原则

Settings 模块采用**容器-组件**架构模式，遵循单一职责原则和组件化设计。

### 核心理念

```
SettingsScreen.kt (容器)
├── ModelDownloadCard.kt (原子组件)
├── LearningSettingsCard.kt (原子组件)
├── AboutCard.kt (原子组件)
└── ... (更多原子组件)
```

## 文件职责

### SettingsScreen.kt - 容器组件

**职责：**
- 作为设置页面的容器，负责布局和组织
- 向系统注册和管理所有子组件
- 提供通用的导航和回调接口
- **不包含**具体的业务逻辑

**应该做：**
- 定义页面整体布局结构
- 组织各个设置卡片的排列顺序
- 提供通用的导航回调（如 `onNavigateToSettings`）
- 管理全局的 UI 状态（如侧边栏显示/隐藏）

**不应该做：**
- 包含具体功能的业务逻辑
- 管理子组件的内部状态
- 处理子组件的具体操作逻辑

**示例代码：**
```kotlin
@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 标题
        Text(text = "设置")

        // AI 模型管理
        SettingsSection(title = "AI 模型管理") {
            ModelDownloadCard(
                onLoadModel = { /* 通用回调 */ }
            )
        }

        // 学习设置
        SettingsSection(title = "学习设置") {
            LearningSettingsCard()
        }

        // 更多设置卡片...
    }
}
```

### 原子组件 (如 ModelDownloadCard.kt)

**职责：**
- 独立的、自包含的功能模块
- 管理自己的内部状态和业务逻辑
- 提供清晰的对外接口（参数和回调）
- 完整实现特定功能的 UI 和交互

**应该做：**
- 内部管理所有相关状态（如下载状态、进度等）
- 实现完整的业务逻辑（如下载、暂停、继续）
- 提供必要的回调接口给父组件
- 保持组件的原子性和独立性

**不应该做：**
- 依赖父组件传递内部状态
- 将业务逻辑暴露给父组件
- 与其他原子组件直接耦合

**示例代码：**
```kotlin
@Composable
fun ModelDownloadCard(
    onLoadModel: () -> Unit,
    onOpenDirectory: () -> Unit
) {
    // 内部状态管理
    val context = LocalContext.current
    val dManager = remember { DManager(context) }
    var downloadStatus by remember { mutableStateOf(dManager.getDStatus()) }
    var downloadProgress by remember { mutableStateOf(0f) }

    // 内部业务逻辑
    fun handleDownloadClick() {
        when (downloadStatus) {
            DStatus.PARTIAL -> resumeDownload()
            DStatus.DOWNLOADING -> pauseDownload()
            else -> startDownload()
        }
    }

    // UI 渲染
    Column {
        Text(text = "智能老师")
        Button(onClick = ::handleDownloadClick) {
            Text(text = getButtonText())
        }
    }
}
```

## 组件通信

### 父组件 → 子组件
- 通过参数传递配置和回调
- 只传递必要的外部依赖

### 子组件 → 父组件
- 通过回调函数通知父组件
- 只暴露必要的事件（如导航、全局状态变更）

### 子组件 ↔ 子组件
- **禁止直接通信**
- 如需通信，通过父组件中转

## 状态管理原则

### 容器组件（SettingsScreen.kt）
- 只管理全局 UI 状态（如对话框显示/隐藏）
- 不管理子组件的业务状态

### 原子组件（如 ModelDownloadCard.kt）
- 使用 `remember` 管理内部状态
- 使用 `LaunchedEffect` 处理副作用
- 状态应该是自包含的，不依赖外部传入

## 添加新的设置卡片

1. **创建新的原子组件文件**
   ```
   app/src/main/java/com/english/accelerator/ui/settings/
   └── NewFeatureCard.kt
   ```

2. **实现自包含的组件**
   ```kotlin
   @Composable
   fun NewFeatureCard(
       onExternalAction: () -> Unit  // 只暴露必要的回调
   ) {
       // 内部状态管理
       var internalState by remember { mutableStateOf(...) }

       // 内部业务逻辑
       fun handleAction() { ... }

       // UI 渲染
       Card { ... }
   }
   ```

3. **在 SettingsScreen.kt 中注册**
   ```kotlin
   SettingsSection(title = "新功能") {
       NewFeatureCard(
           onExternalAction = { /* 处理外部动作 */ }
       )
   }
   ```

## 文件命名规范

- 容器组件：`SettingsScreen.kt`
- 原子组件：`[功能名]Card.kt`（如 `ModelDownloadCard.kt`）
- 工具组件：`[功能名]Dialog.kt`（如 `FileExplorerDialog.kt`）

## 最佳实践

### ✅ 推荐做法

1. **保持组件独立性**
   - 每个原子组件都应该能独立工作
   - 不依赖其他原子组件的状态

2. **单一职责**
   - 一个组件只负责一个功能领域
   - 容器只负责组织，不负责业务

3. **清晰的接口**
   - 参数和回调应该语义明确
   - 避免传递过多参数

4. **状态自包含**
   - 组件内部状态由组件自己管理
   - 只向外暴露必要的事件

### ❌ 避免做法

1. **不要在容器中写业务逻辑**
   ```kotlin
   // ❌ 错误示例
   fun SettingsScreen() {
       var downloadStatus by remember { ... }
       var downloadProgress by remember { ... }

       fun handleDownload() { ... }  // 业务逻辑不应该在这里
   }
   ```

2. **不要让组件依赖外部状态**
   ```kotlin
   // ❌ 错误示例
   @Composable
   fun ModelDownloadCard(
       downloadStatus: DStatus,  // 不应该由外部传入
       onStatusChange: (DStatus) -> Unit
   )
   ```

3. **不要在组件间直接通信**
   ```kotlin
   // ❌ 错误示例
   ModelDownloadCard(
       onComplete = { learningCard.refresh() }  // 不应该直接调用其他组件
   )
   ```

## 总结

这个架构的核心思想是：

- **SettingsScreen.kt** = 容器 = 组织者
- **XxxCard.kt** = 原子组件 = 功能实现者

容器负责"摆放"，组件负责"工作"。这样的设计让代码更清晰、更易维护、更容易扩展。
