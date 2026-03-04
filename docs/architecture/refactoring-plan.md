# 架构重组计划

## 目标
将数据层和算法层进行清晰的原子化拆分，消除重复代码，建立清晰的职责边界。

## 当前问题

### 1. 重复的功能
- `ReviewManager` (data) 和 `ReviewAlgorithm` (algorithm) - 功能重复
- `LearningProgressManager` (data) 既管理数据又包含算法逻辑 - 职责混乱

### 2. 职责不清
- 数据层包含算法逻辑
- 算法层直接操作 SharedPreferences

## 重组方案

### data 文件夹（纯数据层）
**职责：** 只负责数据的存储、读取和持久化

**保留的文件：**
- `Word.kt` - 单词数据模型
- `WordStatus.kt` - 单词状态枚举（已移动）
- `WordPoolType.kt` - 单词池类型枚举（已移动）
- `WordLearningManager.kt` - 单词学习记录的存储和读取
- `WordRepository.kt` - 词典数据访问
- `BookmarkManager.kt` - 收藏数据管理
- `Note.kt`, `Essay.kt`, `Conversation.kt` - 其他数据模型
- `EssayCollectionManager.kt` - 作文收藏数据管理
- `LearningProgressData.kt` - 学习进度数据存储（新建，从 LearningProgressManager 拆分）

**需要删除的文件：**
- `ReviewManager.kt` - 功能已被 `ReviewAlgorithm` 替代
- `StreamingWordLoader.kt` - 功能已被 `WordPoolManager` 替代
- `JsonWordLoader.kt` - 如果不再使用

**需要重构的文件：**
- `LearningProgressManager.kt` → 拆分为：
  - `LearningProgressData.kt` (data) - 只负责数据存储
  - 算法逻辑移到 `WordPoolManager` (algorithm)

### algorithm 文件夹（纯算法层）
**职责：** 只负责算法逻辑，通过数据层获取数据

**保留的文件：**
- `ReviewAlgorithm.kt` - 复习算法
- `DefaultPlanAlgorithm.kt` - 默认计划算法
- `DailyPlanAlgorithm.kt` - 每日计划算法
- `WordPoolManager.kt` - 核心调度器

**文件结构：**
```
algorithm/
├── README.md
├── ReviewAlgorithm.kt
├── DefaultPlanAlgorithm.kt
├── DailyPlanAlgorithm.kt
└── WordPoolManager.kt
```

## 实施步骤

### 步骤 1: 创建 LearningProgressData.kt
从 `LearningProgressManager.kt` 中提取纯数据存储逻辑：
- 学习池数据（poolSize, learningPool, nextWordId）
- 每日统计数据（todayDate, todayMemorizedCount）
- 旧版进度数据（currentPageIndex, currentIndexInPage）

### 步骤 2: 更新 WordPoolManager
将 `LearningProgressManager` 中的算法逻辑移到 `WordPoolManager`：
- 学习池初始化逻辑
- 学习池调整逻辑
- 单词补充逻辑

### 步骤 3: 删除重复文件
- 删除 `ReviewManager.kt`
- 删除 `StreamingWordLoader.kt`
- 更新所有引用这些文件的代码

### 步骤 4: 更新导入引用
- 更新所有使用 `ReviewManager` 的地方改为使用 `ReviewAlgorithm`
- 更新所有使用 `StreamingWordLoader` 的地方改为使用 `WordPoolManager`

### 步骤 5: 清理 LearningProgressManager
- 移除算法逻辑
- 只保留数据存储功能
- 或者完全删除，功能合并到 `LearningProgressData`

## 最终架构

### data/ - 数据层
```
data/
├── models/                    # 数据模型
│   ├── Word.kt
│   ├── WordStatus.kt
│   ├── WordPoolType.kt
│   ├── Note.kt
│   ├── Essay.kt
│   └── Conversation.kt
├── managers/                  # 数据管理器
│   ├── WordLearningManager.kt
│   ├── WordRepository.kt
│   ├── BookmarkManager.kt
│   ├── EssayCollectionManager.kt
│   └── LearningProgressData.kt
```

### algorithm/ - 算法层
```
algorithm/
├── README.md
├── ReviewAlgorithm.kt
├── DefaultPlanAlgorithm.kt
├── DailyPlanAlgorithm.kt
└── WordPoolManager.kt
```

## 依赖关系
- UI层 → algorithm层 → data层
- algorithm层通过data层获取数据
- algorithm层不直接操作SharedPreferences
- data层不包含算法逻辑

## 优势
1. **职责清晰**：数据层只管数据，算法层只管算法
2. **易于测试**：算法可以独立测试，不依赖持久化
3. **易于维护**：修改算法不影响数据层，修改数据存储不影响算法
4. **易于扩展**：添加新算法不需要修改数据层
