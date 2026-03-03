# 下载器模块架构文档

## 架构概览

```
                    DManager.kt
                  (核心协调者)
                        │
        ┌───────────────┼───────────────┬───────────────┐
        │               │               │               │
        ▼               ▼               ▼               ▼
    DConfig.kt      DState.kt       DPing.kt       DEngine.kt
   (配置管理)      (状态监控)      (网络检查)      (下载执行)
        │               │               │               │
        ▼               ▼               ▼               ▼
   config.json     文件系统         服务器          HTTP下载
```

## 模块职责

### DManager.kt - 核心协调者
**职责：**
- 编排整个下载流程
- 协调各个模块的调用
- 做出下载决策
- 提供对外接口给 UI 层

**依赖：**
- `configManager: DConfig` - 配置管理（单一实例）
- `stateMonitor: DStateMonitor` - 状态监控（接收 configManager）
- `networkPing: DPing` - 网络检查
- `downloadEngine: DEngine` - 下载引擎

### DConfig.kt - 配置管理器
**职责：**
- 读写 config.json 文件
- 持久化下载状态
- 管理下载线路配置
- 记录各类日志

**特点：**
- 单一真相来源
- 所有配置的持久化都通过它

### DState.kt - 状态监控器
**职责：**
- 监控文件系统状态
- 检查文件完整性
- 提供文件信息
- 验证文件大小

**依赖：**
- 接收 DManager 传递的 `DConfig` 实例
- 不自己创建配置实例

**特点：**
- 直接与文件系统交互
- 只读取配置，不修改配置

### DPing.kt - 网络检查器
**职责：**
- 检查网络连接状态
- 检测服务器是否支持 Range 请求
- 生成下载配置建议
- 计算推荐的分块大小

**特点：**
- 独立模块，无依赖
- 只负责检测，不修改状态

### DEngine.kt - 下载引擎
**职责：**
- 执行实际的 HTTP 下载
- 处理断点续传逻辑
- 管理暂停/恢复/取消
- 检测服务器 Range 支持

**特点：**
- 独立模块，无依赖
- 只负责下载，不管理状态

## 下载流程

### 完整下载流程

```
1. DManager.downloadModel()
   ↓
2. DState.isFileComplete() - 检查文件是否已完整
   ↓
3. DPing.checkServer(url) - 检查网络和 Range 支持
   ↓
4. DConfig.updateRouteRangeSupport() - 保存检测结果
   ↓
5. DPing.generateDownloadConfig() - 生成下载配置
   ↓
6. DState.getFileSize() - 获取已下载大小
   ↓
7. DConfig.updateDownloadState() - 更新下载状态
   ↓
8. DEngine.download() - 执行下载
   ↓
9. DState.validateFile() - 验证文件完整性
   ↓
10. DConfig.updateDownloadState() - 保存最终状态
```

### 切换线路流程

```
1. DManager.switchRoute()
   ↓
2. DState.fileExists() - 检查文件是否存在
   ↓
3. DState.isFileComplete() - 检查是否完整
   ↓
4. DState.deleteFile() - 删除不完整的文件
   ↓
5. DConfig.clearDownloadState() - 清除下载状态
   ↓
6. DConfig.addDownloadLog() - 记录日志
```

## 断点续传机制

### 检测方式
1. **DPing 预检测**（下载前）
   - 发送 HEAD 请求 + Range 头
   - 检查响应码（206 vs 200）
   - 检查 Accept-Ranges 响应头
   - 检查 Content-Range 响应头

2. **DEngine 实际检测**（下载时）
   - 发送 GET 请求 + Range 头
   - 根据响应码决定下载模式
   - 206 → 追加模式继续下载
   - 200 → 删除旧文件重新下载

### Range 支持状态持久化
```json
{
  "download": {
    "routes": [
      {
        "name": "MODELSCOPE",
        "url": "https://...",
        "supportsRange": true,
        "rangeChecked": true
      }
    ]
  }
}
```

## 核心设计原则

1. **单一职责** - 每个模块只负责一件事
2. **依赖注入** - DStateMonitor 接收 DConfig 实例
3. **单一真相来源** - 只有一个 DConfig 实例
4. **协调者模式** - DManager 协调所有模块
5. **模块独立** - 工具模块无依赖，可独立测试

## 文件说明

- `DManager.kt` - 核心协调者，对外接口
- `DConfig.kt` - 配置管理，读写 config.json
- `DState.kt` - 状态监控，文件系统交互
- `DPing.kt` - 网络检查，服务器能力检测
- `DEngine.kt` - 下载引擎，HTTP 下载执行

## 配置文件

**位置：** `context.filesDir/download_states/Config.json`

**主要字段：**
- `model` - 模型信息（名称、大小、容差）
- `download` - 下载配置（分块大小、重试次数、线路列表）
- `state` - 当前下载状态
- `logs` - 各类日志记录

## 使用示例

```kotlin
// 创建下载管理器
val downloadManager = DManager(context)

// 开始下载
downloadManager.downloadModel { downloaded, total, speed ->
    // 更新 UI 进度
    val progress = (downloaded.toFloat() / total.toFloat() * 100).toInt()
    updateProgress(progress, speed)
}

// 暂停下载
downloadManager.pauseDownload()

// 恢复下载
downloadManager.resumeDownload()

// 切换线路
downloadManager.switchRoute()

// 检查下载状态
val status = downloadManager.getDStatus()
val isComplete = downloadManager.isModelComplete()
```

## 注意事项

1. DStateMonitor 必须接收 DConfig 实例，不能自己创建
2. 切换线路时会自动删除不完整的文件
3. 服务器不支持 Range 时会自动删除旧文件重新下载
4. 所有状态变更都会记录到 config.json
5. DManager 是唯一的对外接口，UI 层只与它交互
