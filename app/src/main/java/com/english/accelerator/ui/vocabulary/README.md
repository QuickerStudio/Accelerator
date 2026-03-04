# Vocabulary Module

单词学习模块 - 插件式节点架构

## 架构

```
vocabulary/
├── VocabularyScreen.kt    # 节点管理器（根组件）
└── nodes/                 # 可替换节点
    ├── CardStack.kt       # 卡片堆栈
    ├── WordCardNode.kt    # 单词卡片
    └── BookmarkNode.kt    # 收藏列表
```

## 职责

- **VocabularyScreen.kt**: 节点管理器，管理所有节点的注册、生命周期、状态和数据流
- **nodes/**: 所有 UI 节点，独立、可替换

## 特点

- 插件式架构，节点可随时替换
- 集成 ViewModel 和 NodeManager
- 统一的节点接口
