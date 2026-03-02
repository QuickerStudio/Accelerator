# 当前项目状态 - 英语学习加速器

**最后更新**: 2026年3月2日
**版本**: v0.5.0
**项目阶段**: AI 开发阶段 - 模型下载和基础搭建

---

## 项目概述

英语学习加速器是一个基于 Kotlin + Jetpack Compose 开发的 Android 应用，专注于英语学习。项目采用简化的架构设计，目前处于早期开发阶段，重点是模型集成和基础功能实现。

---

## 核心技术栈

### 前端框架
- **UI框架**: Kotlin + Jetpack Compose + Material3
- **架构模式**: 简化的 Manager 模式（非 Clean Architecture）
- **异步处理**: Kotlin Coroutines + Flow
- **导航**: Bottom Navigation + Compose Navigation

### AI 模型（单一统一模型）
- **目标模型**: `gemma-3n-E2B-it-litert-lm`（多模态大模型）
  - 用途：所有文本任务（单词解释、语法检查、作文批改、对话生成）
  - 推理库：MediaPipe LLM Inference
  - 下载源：HuggingFace（主）/ ModelScope（备用）
  - 特点：多模态能力，统一处理所有 AI 任务

### 数据存储
- **本地存储**: SharedPreferences + Gson（简化方案）
- **数据模型**: 简单的 data class（无 Room 数据库）

### 依赖库
- Jetpack Compose (UI)
- Kotlin Coroutines (异步)
- MediaPipe Tasks GenAI (LLM 推理)
- Gson (JSON 序列化)

---

## 实际项目结构

```
app/src/main/java/com/english/accelerator/
├── ui/
│   ├── vocabulary/          # 单词学习屏幕
│   ├── speaking/            # 口语练习屏幕
│   ├── writing/             # 写作练习屏幕
│   ├── settings/            # 设置屏幕
│   ├── sidebar/             # 侧边栏（笔记管理）
│   ├── navigation/          # 导航配置
│   ├── theme/               # 主题系统
│   └── components/          # 通用组件
├── data/
│   ├── WordLearningManager.kt
│   ├── BookmarkManager.kt
│   ├── ReviewManager.kt
│   ├── EssayCollectionManager.kt
│   ├── NoteManager.kt
│   ├── models/              # 数据模型
│   │   ├── Word.kt
│   │   ├── Essay.kt
│   │   ├── Note.kt
│   │   └── ...
│   └── ...
├── ai/
│   ├── GemmaInferenceManager.kt      # LLM 推理管理
│   ├── ModelDownloadManager.kt       # 模型下载管理
│   ├── PromptTemplates.kt           # 提示词模板
│   ├── GrammarSuggestion.kt         # 语法建议数据类
│   └── ...
└── MainActivity.kt
```

**总文件数**: ~40 个 Kotlin 文件

---

## 已实现功能

### ✅ UI 屏幕
- **VocabularyScreen**: 单词卡片学习（卡片堆栈、滑动交互）
- **WritingScreen**: 写作练习（文本编辑、基础语法检查）
- **SpeakingScreen**: 口语练习（基础界面）
- **SettingsScreen**: 设置（主题、模型管理）
- **Sidebar**: 笔记管理（笔记编辑、分组）

### ✅ 数据管理
- 单词学习进度追踪
- 书签/收藏功能
- 作文集合管理
- 笔记管理和分组
- 复习管理

### ✅ AI 集成
- **模型下载**: 双路由下载（HuggingFace + ModelScope）
  - 自动路由选择（基于 ping 延迟）
  - 暂停/恢复下载支持
  - 断点续传
- **模型推理**: MediaPipe LLM Inference
  - 基础推理功能
  - 提示词模板系统
  - 语法建议解析

### ✅ UI/UX
- 4 种主题系统（浅色、深色、苹果绿、亮紫）
- 底部导航
- 拖放支持（笔记分组）
- Compose 动画基础

---

## 未实现功能

### ❌ 高优先级
- **语音识别**: Whisper 集成（计划中）
- **文本转语音**: TTS 服务（计划中）
- **数据持久化**: Room 数据库（计划中）
- **设置持久化**: DataStore（计划中）

### ❌ 中优先级
- **Agent 系统**: 多角色 AI 助手（计划中）
- **后台任务**: WorkManager 集成（计划中）
- **详细语法检查**: 错误标注和建议（计划中）
- **发音评估**: 发音评分系统（计划中）

### ❌ 低优先级
- **数据导入导出**: 备份功能
- **学习提醒**: 定时提醒
- **进度统计**: 详细学习数据
- **用户认证**: 账户系统

---

## 当前限制

1. **架构**: 简化的 Manager 模式，无 DI 框架
2. **数据库**: 使用 SharedPreferences + Gson，无 Room
3. **模型**: 仅支持 gemma-3n-E2B-it-litert-lm
4. **语音**: 无语音识别和 TTS
5. **测试**: 最小化测试基础设施

---

## 模型配置

### gemma-3n-E2B-it-litert-lm

**特点**:
- 多模态大模型
- 统一处理所有文本任务
- 通过 MediaPipe 推理

**下载配置**:
```kotlin
// 主源
https://huggingface.co/google/gemma-3n-E2B-it/resolve/main/gemma-3n-E2B-it.task

// 备用源（国内）
https://www.modelscope.cn/models/google/gemma-3n-E2B-it/resolve/master/gemma-3n-E2B-it.task
```

**推理参数**:
- maxTokens: 2048
- temperature: 0.3
- topK: 40

---

## 开发进度

| 功能 | 状态 | 备注 |
|------|------|------|
| 项目结构 | ✅ 完成 | 简化架构 |
| UI 屏幕 | ✅ 完成 | 基础功能 |
| 模型下载 | ✅ 完成 | 双路由支持 |
| 模型推理 | 🔄 进行中 | 基础推理工作 |
| 语音识别 | ⏳ 计划中 | 下一阶段 |
| TTS | ⏳ 计划中 | 下一阶段 |
| 数据库 | ⏳ 计划中 | 后续阶段 |
| Agent 系统 | ⏳ 计划中 | 后续阶段 |

---

## 系统要求

- **Android**: API 26+ (Android 8.0+)
- **RAM**: 6GB+ 推荐
- **存储**: 4GB+ 可用空间
- **处理器**: ARMv8-A 64位

---

## 快速开始

### 构建项目
```bash
./gradlew build
```

### 运行应用
```bash
./gradlew installDebug
```

### 模型下载
应用首次启动时会自动下载 gemma-3n-E2B-it-litert-lm 模型。

---

## 下一步

详见 [BUILD_ROADMAP.md](BUILD_ROADMAP.md)
