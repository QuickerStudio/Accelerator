# 单词推送算法模块
algorithm 文件夹应该只包含：

纯算法逻辑
不直接操作 SharedPreferences
通过数据层获取数据
本文件夹包含单词学习系统的核心推送算法。

## 文件结构

```
algorithm/
├── README.md                          # 本文件
├── WordPoolManager.kt                 # 单词池管理器（核心）
├── DefaultPlanAlgorithm.kt           # 默认计划算法
├── DailyPlanAlgorithm.kt             # 每日计划算法
└── ReviewAlgorithm.kt                # 复习算法（洗牌混合）
```

## 数据类型

### 单词状态 (WordStatus)
- **已记住** (Memorized): 用户已经掌握的单词
- **未记住** (Unmemorized): 用户学习过但还没掌握的单词
- **复习中** (Reviewing): 正在复习的单词

### 单词池类型 (WordPoolType)
- **默认计划池** (DefaultPlan): 包含所有剩余单词（从词典5000个统计）
- **每日计划池** (DailyPlan): 固定大小的学习池（如100个）

### 索引类型
- **默认计划索引**: 剩余单词索引（词典顺序）
- **每日计划索引**: 学习池中的单词索引

## 算法说明

### 1. 默认计划算法 (DefaultPlanAlgorithm)
**触发条件**: 每日学习单词数设置为 0

**逻辑**:
- 按词典顺序推送所有单词（1-4998）
- 使用 ReviewAlgorithm 混合复习单词
- 每50个单词一组
- 在第49个单词时刷新，随机插入下一组

### 2. 每日计划算法 (DailyPlanAlgorithm)
**触发条件**: 每日学习单词数设置 > 0（如100个）

**逻辑**:
- 维护固定大小的学习池
- 单词被标记为"已记住"时，从池中移除
- 从词典后续索引补充新单词
- 使用 ReviewAlgorithm 混合复习单词
- 学习池动态更新，不按天重置

### 3. 复习算法 (ReviewAlgorithm)
**功能**: 将未记住的单词随机插入到新单词中

**逻辑**:
- 从未记住的单词池中随机选择复习单词
- 按比例插入：默认每8个新单词插入1个复习单词
- 复习单词随机插入到新单词中（避免插入到最开始）
- 在第49个单词时刷新，准备下一组

## 使用示例

```kotlin
// 初始化单词池管理器
val poolManager = WordPoolManager.getInstance()

// 设置每日学习单词数（0表示默认模式，>0表示每日计划模式）
poolManager.setDailyWordGoal(100)

// 获取下一批单词（50个一组）
val words = poolManager.getNextBatch(50)

// 标记单词为已记住
poolManager.markAsMemorized(wordId)

// 获取统计信息
val stats = poolManager.getStatistics()
```

## 统计维度

- **已记住**: 总共记住的单词数（累计）
- **未记住**: 学习过但还没记住的单词数
- **学习中**: 当前学习池中的单词数
- **剩余单词**: 词典中还没学习的单词数
- **复习次数**: 复习旧单词的次数

## 设计原则

1. **原子化**: 每个算法独立封装，职责单一
2. **可扩展**: 易于添加新的推送算法
3. **可测试**: 每个算法都可以独立测试
4. **清晰**: 代码结构清晰，易于理解和维护
