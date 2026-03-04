# Sidebar Module

侧边栏模块 - 插件式节点架构

## 架构

```
sidebar/
├── Sidebar.kt          # 节点管理器（根组件）
└── nodes/              # 可替换节点
    ├── DragDrop.kt     # 拖拽功能
    └── Editor.kt       # 编辑器
```

## 职责

- **Sidebar.kt**: 节点管理器，管理所有节点的注册、生命周期、状态和数据流
- **nodes/**: 所有 UI 节点，独立、可替换

## 特点

- 插件式架构，节点可随时替换
- 集成 ViewModel 和 NodeManager
- 统一的节点接口
