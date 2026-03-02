# 已弃用文档 - DEPRECATED

本文件夹包含已过时的设计文档。这些文档描述的是原始的复杂架构设计，但实际代码采用了更简化的方案。

**重要**: 这些文档仅供参考，不代表当前项目状态。请参考 [CURRENT_STATUS.md](../CURRENT_STATUS.md) 了解实际情况。

---

## 为什么这些文档过时了？

### 架构差异
- **文档**: 描述完整的 Clean Architecture（Domain/UseCase/Repository 层）
- **实际**: 简化的 Manager 模式（无 DI 框架）

### 模型差异
- **文档**: Qwen2.5-3B + Whisper + 多个模型
- **实际**: 单一模型 gemma-3n-E2B-it-litert-lm

### 依赖差异
- **文档**: Hilt + Room + DataStore + WorkManager
- **实际**: SharedPreferences + Gson（简化方案）

### 项目结构差异
- **文档**: 100+ 个文件，完整的分层结构
- **实际**: ~40 个文件，简化的结构

---

## 已弃用的文档

| 文档 | 原因 | 优先级 |
|------|------|--------|
| 03-architecture.md | 架构模式不符 | CRITICAL |
| 12-dependencies-structure.md | 依赖和模型不符 | CRITICAL |
| 02-project-structure.md | 项目结构不符 | CRITICAL |
| 05-data-models.md | 数据模型过于复杂 | HIGH |
| 01-overview.md | 模型和技术栈不符 | HIGH |
| 06-components-interfaces.md | UseCase/Repository 未实现 | MEDIUM |
| 13-agent-system.md | Agent 系统未实现 | MEDIUM |
| 04-workflows.md | 假设了未实现的架构 | MEDIUM |
| 07-algorithms.md | 算法未实现（未来功能） | LOW |
| 10-testing-strategy.md | 测试策略未实现 | LOW |
| 08-correctness-properties.md | 属性未实现 | LOW |
| 09-error-handling.md | 错误处理策略未实现 | LOW |
| 11-performance-security.md | 性能/安全策略未实现 | LOW |

---

## 如何使用这些文档

### 参考价值
- 了解原始设计意图
- 理解某些功能的规划思路
- 作为未来实现的参考

### 不应该使用
- 作为当前项目状态的参考
- 作为开发指南
- 作为架构决策的依据

---

## 当前文档

请参考以下文档了解当前项目状态：

- [CURRENT_STATUS.md](../CURRENT_STATUS.md) - 当前项目状态
- [BUILD_ROADMAP.md](../BUILD_ROADMAP.md) - 构建路线图
- [README.md](../../README.md) - 项目概述
- [CHANGELOG.md](../../CHANGELOG.md) - 版本历史

---

## 未来计划

当项目进展到以下阶段时，这些文档中的某些内容可能会被重新激活：

- **阶段 2**: 实现 Room 数据库时，参考 05-data-models.md
- **阶段 3**: 实现 Agent 系统时，参考 13-agent-system.md
- **阶段 4**: 优化性能时，参考 11-performance-security.md
