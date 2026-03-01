# 后端设计文档导航

本目录包含英语学习应用的完整后端设计规范，按技术架构分页组织。

## 文档结构

### 📋 [01-overview.md](01-overview.md)
项目概览与设计目标
- 项目背景与目标
- 核心功能概述（单词、口语、写作、笔记、设置）
- 技术栈选择（统一 LLM 架构）
- AI 模型配置（Qwen2.5-3B + Whisper Tiny + Android TTS）
- 架构设计原则
- 系统要求（12GB+ RAM 推荐）
- 存储空间分配（~2.6GB）

### 📁 [02-project-structure.md](02-project-structure.md)
项目文件目录结构
- 完整目录树结构
- UI Layer 组织
- Domain Layer 组织
- Data Layer 组织
- AI Services Layer 组织
- 依赖注入配置
- 测试目录结构
- 命名规范与代码组织原则

### 🏗️ [03-architecture.md](03-architecture.md)
系统架构设计
- Clean Architecture 分层架构
- MVVM 模式应用
- 依赖注入（Hilt）
- 模块化设计
- 数据流向
- 组件通信模式

### 🔄 [04-workflows.md](04-workflows.md)
业务流程与用例
- 单词学习流程
- AI 口语训练流程
- 写作练习流程
- 笔记管理流程
- 设置管理流程
- 数据导入导出流程
- 自动朗读定时任务流程

### 💾 [05-data-models.md](05-data-models.md)
数据模型设计
- Word（单词）
- Conversation & ConversationTurn（对话）
- Essay（作文）
- Note & NoteGroup（笔记）
- UserSettings（用户设置）
- UserProgress（用户进度）
- WordLearningLog（学习日志）
- 数据模型关系图
- 数据持久化策略（Room、DataStore、文件存储）
- 数据库迁移策略

### � [06-components-interfaces.md](06-components-interfaces.md)
组件与接口设计
- UI Layer（Jetpack Compose Screens）
- ViewModel Layer（状态管理）
- Domain Layer（Use Cases）
- Data Layer（Repositories）
- AI Model Services（LLM、TTS、ASR、语法检查）
- 新增组件（设置页面、侧边栏、主题选择）
- 组件依赖关系图
- 接口设计原则

### �🧮 [07-algorithms.md](07-algorithms.md)
核心算法设计
- 单词学习算法（间隔重复算法）
- 语法检查算法
- 作文评分算法
- AI 对话生成算法
- 发音评估算法
- 学习进度计算算法
- 算法性能分析

### ✅ [08-correctness-properties.md](08-correctness-properties.md)
正确性与验证
- 数据验证规则
- 业务逻辑约束
- 状态一致性保证
- 并发控制策略
- 事务管理
- 数据完整性检查

### ⚠️ [09-error-handling.md](09-error-handling.md)
错误处理策略
- 错误分类（网络错误、数据错误、AI 模型错误）
- 异常处理机制
- 用户友好的错误提示
- 错误日志记录
- 降级策略
- 重试机制

### 🧪 [10-testing-strategy.md](10-testing-strategy.md)
测试策略
- 单元测试（ViewModel、Use Cases、Repository）
- 集成测试（数据库、AI 服务）
- UI 测试（Compose UI 测试）
- 端到端测试
- 测试覆盖率目标
- Mock 策略
- 测试工具与框架

### ⚡ [11-performance-security.md](11-performance-security.md)
性能优化与安全设计
- 性能优化策略（内存管理、数据库优化、UI 渲染优化）
- AI 模型推理优化
- 缓存策略
- 安全设计（数据加密、权限管理、输入验证）
- 隐私保护
- 离线功能设计

### 📦 [12-dependencies-structure.md](12-dependencies-structure.md)
依赖管理与构建配置
- Gradle 依赖配置
- 版本管理策略
- 模块依赖关系
- 第三方库选择
- 构建配置

### 🤖 [13-agent-system.md](13-agent-system.md)
Agent 系统与系统提示词
- Agent 架构设计理念
- 5 个 Agent 角色定义（单词助手、语法检查、作文批改、口语陪练、学习规划）
- 系统提示词工程
- Agent 服务实现
- 模式约束与编排
- 自定义提示词
- 性能优化与测试策略

---

## 使用指南

1. **开始开发前**：先阅读 `01-overview.md` 和 `03-architecture.md` 了解整体架构
2. **创建项目结构时**：参考 `02-project-structure.md` 了解完整的目录组织
3. **实现数据层时**：参考 `05-data-models.md` 了解数据模型设计
4. **实现业务逻辑时**：查看 `04-workflows.md` 和 `06-components-interfaces.md`
5. **优化性能时**：参考 `07-algorithms.md` 和 `11-performance-security.md`
6. **编写测试时**：查看 `10-testing-strategy.md` 确保测试覆盖

## 设计原则

- **Clean Architecture**：清晰的分层架构，依赖倒置
- **单一职责**：每个组件只负责一个功能
- **依赖注入**：使用 Hilt 管理依赖
- **响应式编程**：使用 Kotlin Flow 和 Coroutines
- **类型安全**：使用 Kotlin 类型系统和 sealed class
- **测试驱动**：高测试覆盖率，可测试的代码设计

## 技术栈概览

### 核心框架
- **Kotlin**：主要开发语言
- **Jetpack Compose**：现代化 UI 框架
- **Coroutines & Flow**：异步编程和响应式数据流

### 架构组件
- **ViewModel**：UI 状态管理
- **Room**：本地数据库
- **DataStore**：设置持久化（替代 SharedPreferences）
- **Hilt**：依赖注入
- **Navigation Compose**：页面导航
- **WorkManager**：后台定时任务

### AI 功能
- **LLM**：本地大语言模型（Qwen2.5-3B，GGUF 格式）
- **TTS**：文本转语音（Android 系统 TTS）
- **ASR**：语音识别（Whisper Tiny）

### 测试框架
- **JUnit**：单元测试
- **Mockito/MockK**：Mock 框架
- **Compose UI Test**：UI 测试
- **Truth**：断言库

---

## 与 UI 设计文档的对应关系

| UI 设计文档 | 后端设计文档 | 说明 |
|------------|-------------|------|
| 01-design-system.md | 03-architecture.md | UI 设计系统 ↔ 架构设计 |
| 02-vocabulary-screen.md | 04-workflows.md, 06-components-interfaces.md | 单词页面 ↔ 单词学习流程与组件 |
| 03-speaking-screen.md | 04-workflows.md, 06-components-interfaces.md | 口语页面 ↔ 对话流程与组件 |
| 04-writing-screen.md | 04-workflows.md, 06-components-interfaces.md | 写作页面 ↔ 写作流程与组件 |
| 08-settings-screen.md | 05-data-models.md, 06-components-interfaces.md | 设置页面 ↔ 设置数据模型与组件 |
| 09-sidebar.md | 05-data-models.md, 06-components-interfaces.md | 侧边栏 ↔ 笔记数据模型与组件 |
| 10-theme-styles.md | 05-data-models.md | 主题设计 ↔ 主题数据模型 |
| settings-pages/*.md | 05-data-models.md, 06-components-interfaces.md | 设置子页面 ↔ 设置数据与组件 |

---

## 开发流程建议

### 阶段 1：基础架构（任务 1-5）
1. 搭建项目结构和依赖配置
2. 实现数据模型和 Room 数据库
3. 实现 Repository 层
4. 实现基础 Use Cases
5. 集成 AI 模型服务

### 阶段 2：核心功能（任务 6-13）
6. 实现单词学习功能
7. 实现 AI 口语训练功能
8. 实现写作练习功能
9. 实现用户进度追踪
10. 实现 TTS 和语音识别
11. 实现语法检查
12. 实现底部导航
13. 实现基础 UI 测试

### 阶段 3：扩展功能（任务 14-23）
14. 实现主题系统
15. 实现设置页面
16. 实现设置子页面
17. 实现笔记管理功能
18. 实现单词卡片滑动交互
19. 实现底部输入区域
20. 实现自动朗读功能
21. 实现数据导入导出
22. 更新导航和主界面
23. 最终检查点

---

## 文档维护

- **版本**：0.0.1
- **最后更新**：2026-03-01
- **维护者**：QuickerStudio
- **更新频率**：随功能迭代更新

## 相关文档

- [根目录设计文档](../design.md)：简化版设计概览
- [需求文档](../requirements.md)：功能需求列表
- [任务文档](../tasks.md)：开发任务清单
- [UI 设计文档](../ui-design/README.md)：前端 UI 设计规范
- [更新总结](../../UPDATE_SUMMARY.md)：最近的设计变更记录

