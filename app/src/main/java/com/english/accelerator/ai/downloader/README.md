# Model Downloader

模型下载管理模块，负责从远程服务器下载 LLM 模型文件。

## 核心组件

### DManager.kt
模型下载管理器，提供下载、暂停、恢复、取消等功能。

**主要功能：**
- 多源下载（支持多个镜像源）
- 断点续传
- 下载进度跟踪
- 速度计算
- 自动重试

### DEngine.kt
下载引擎，处理实际的 HTTP 下载逻辑。

### DConfig.kt
下载配置管理，包括模型信息、下载源等。

### DState.kt
下载状态管理，跟踪下载进度和状态。

### DPing.kt
网络延迟检测，用于选择最快的下载源。

## 使用示例

```kotlin
val dManager = DManager(context)

// 开始下载
dManager.startDownload()

// 监听下载状态
dManager.downloadState.collect { state ->
    when (state) {
        is DownloadState.Downloading -> {
            println("Progress: ${state.progress}")
            println("Speed: ${state.speed}")
        }
        is DownloadState.Completed -> {
            println("Download completed!")
        }
        is DownloadState.Error -> {
            println("Error: ${state.message}")
        }
    }
}

// 暂停下载
dManager.pauseDownload()

// 恢复下载
dManager.resumeDownload()

// 取消下载
dManager.cancelDownload()
```

## 配置

模型配置在 `config.json` 中定义：

```json
{
  "model": {
    "name": "Gemma 3N E2B IT INT4",
    "fileName": "gemma-3n-e2b-it-int4.litertlm",
    "size": 3600000000
  },
  "sources": [
    {
      "name": "ModelScope",
      "url": "https://www.modelscope.cn/..."
    },
    {
      "name": "Hugging Face",
      "url": "https://huggingface.co/..."
    }
  ]
}
```

## 特性

- **多源下载** - 支持多个镜像源，自动选择最快的
- **断点续传** - 支持暂停和恢复下载
- **进度跟踪** - 实时显示下载进度和速度
- **自动重试** - 下载失败自动重试
- **网络检测** - 自动检测网络延迟，选择最优源
