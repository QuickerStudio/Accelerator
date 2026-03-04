# Model Configuration

模型配置管理模块，负责管理 LLM 模型的配置和状态。

## 核心组件

### ModelConfig.kt
模型配置管理器，提供模型状态跟踪和配置持久化。

**主要功能：**
- 跟踪模型下载状态
- 跟踪模型初始化状态
- 保存模型配置
- 加载模型配置

## 使用示例

```kotlin
val modelConfig = ModelConfig.getInstance()

// 标记模型下载完成
modelConfig.markDownloadComplete()

// 标记模型初始化成功
modelConfig.markInitializationSuccess()

// 标记模型初始化失败
modelConfig.markInitializationFailed("Error message")

// 检查模型是否已下载
val isDownloaded = modelConfig.isModelDownloaded()

// 检查模型是否已初始化
val isInitialized = modelConfig.isModelInitialized()

// 获取初始化错误信息
val error = modelConfig.getInitializationError()
```

## 配置存储

模型配置以 SharedPreferences 形式存储：

```kotlin
{
  "model_downloaded": true,
  "model_initialized": true,
  "initialization_error": null,
  "model_path": "/data/user/0/.../models/gemma-3n-e2b-it-int4.litertlm",
  "last_check_time": 1234567890
}
```

## 状态流转

```
未下载 → 下载中 → 已下载 → 初始化中 → 已初始化
                                    ↓
                                初始化失败
```

## 与其他模块的关系

```
SettingsScreen (UI)
    ↓
ModelConfig (配置管理)
    ↓
InferenceEngine (推理引擎)
```

## 特性

- **状态持久化** - 模型状态永久保存
- **错误跟踪** - 记录初始化错误信息
- **单例模式** - 全局唯一配置实例
