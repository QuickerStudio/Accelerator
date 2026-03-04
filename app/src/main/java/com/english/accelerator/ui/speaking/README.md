# Speaking Module

对话模块 - 插件式节点架构

## 架构

```
speaking/
├── SpeakingScreen.kt    # 节点管理器（根组件）
└── nodes/               # 可替换节点
    ├── AgentBubble.kt   # AI 消息气泡
    ├── UserBubble.kt    # 用户消息气泡
    ├── ChatWindow.kt    # 对话窗口
    ├── InputBox.kt      # 输入框
    ├── NavBar.kt        # 导航栏
    └── History.kt       # 历史记录
```

## 职责

- **SpeakingScreen.kt**: 节点管理器，管理所有节点的注册、生命周期、状态和数据流
- **nodes/**: 所有 UI 节点，独立、可替换

## 特点

- 插件式架构，节点可随时替换
- 集成 ViewModel 和 NodeManager
- 统一的节点接口
