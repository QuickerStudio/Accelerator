# 架构分析报告

## 当前架构问题

### 1. 层次混乱

**期望的五层架构：**
```
data (数据层)
  ↓
algorithm (算法层)
  ↓
indexer (索引层)
  ↓
loader (加载层)
  ↓
UI (界面层)
```

**当前实际情况：**
```
data (数据层)
  ↓
algorithm (算法层 + 索引层混合)
  ↓
WordLoader (只是简单转发，没有真正的加载管理)
  ↓
UI (界面层)
```

### 2. 职责重叠问题

#### data 层
**应该包含：**
- 纯数据模型（Word, Note, Essay, Conversation）
- 数据类型定义（WordStatus, WordPoolType, WordPoolStatistics）
- 纯数据存储管理器（只负责 SharedPreferences 读写）

**当前问题：**
- ✅ `Word.kt`, `Note.kt`, `Essay.kt`, `Conversation.kt` - 正确
- ✅ `WordStatus.kt`, `WordPoolType.kt` - 正确（已移回）
- ❌ `ReviewManager.kt` - 重复，应删除（功能已被 ReviewAlgorithm 替代）
- ❌ `StreamingWordLoader.kt` - 重复，应删除（功能已被 WordLoader 替代）
- ❌ `LearningProgressManager.kt` - 职责混乱，既有数据存储又有算法逻辑
- ✅ `WordLearningManager.kt` - 正确（纯数据存储）
- ✅ `WordRepository.kt` - 正确（词典数据访问）
- ✅ `BookmarkManager.kt` - 正确（纯数据存储）
- ✅ `EssayCollectionManager.kt` - 正确（纯数据存储）

#### algorithm 层
**应该包含：**
- 纯算法逻辑
- 不直接操作 SharedPreferences
- 通过数据层获取数据

**当前情况：**
- ✅ `ReviewAlgorithm.kt` - 正确（纯算法）
- ✅ `DefaultPlanAlgorithm.kt` - 正确（纯算法）
- ✅ `DailyPlanAlgorithm.kt` - 正确（纯算法）
- ❌ `WordPoolIndexer.kt` - 职责混乱，既做索引又做算法协调，还直接操作数据层

#### indexer 层
**应该包含：**
- 建立索引
- 协调算法
- 管理学习池索引

**当前问题：**
- ❌ 没有独立的 indexer 层
- ❌ `WordPoolIndexer` 在 algorithm 文件夹中，职责不清

#### loader 层
**应该包含：**
- 加载索引
- 分块加载管理
- 内存加载管理
- 缓存管理

**当前问题：**
- ❌ `WordLoader` 只是简单转发，没有真正的加载逻辑
- ❌ 没有分块加载管理
- ❌ 没有内存管理
- ❌ 没有缓存管理

### 3. 代码质量问题

#### 重复代码
1. **ReviewManager vs ReviewAlgorithm**
   - 功能完全重复
   - ReviewManager 应该删除

2. **StreamingWordLoader vs WordLoader**
   - 功能重复
   - StreamingWordLoader 应该删除

#### 职责不清
1. **LearningProgressManager**
   - 既管理数据存储（SharedPreferences）
   - 又包含算法逻辑（学习池管理）
   - 应该拆分为：
     - `LearningProgressData` (data层) - 纯数据存储
     - 算法逻辑移到 indexer 层

2. **WordPoolIndexer**
   - 既建立索引
   - 又协调算法
   - 又直接调用数据层
   - 职责过多，应该拆分

3. **WordLoader**
   - 只是简单转发
   - 没有真正的加载管理逻辑
   - 需要增强功能

## 建议的重构方案

### 方案 A: 完整五层架构（推荐）

```
1. data/ (数据层)
   ├── models/           # 数据模型
   │   ├── Word.kt
   │   ├── WordStatus.kt
   │   ├── WordPoolType.kt
   │   └── ...
   └── storage/          # 数据存储
       ├── WordLearningData.kt
       ├── LearningProgressData.kt
       ├── WordRepository.kt
       └── ...

2. algorithm/ (算法层)
   ├── ReviewAlgorithm.kt
   ├── DefaultPlanAlgorithm.kt
   └── DailyPlanAlgorithm.kt

3. indexer/ (索引层) - 新建
   └── WordPoolIndexer.kt
       - 建立索引
       - 协调算法
       - 管理学习池索引

4. loader/ (加载层) - 新建
   └── WordLoader.kt
       - 加载索引
       - 分块加载
       - 内存管理
       - 缓存管理

5. ui/ (界面层)
   └── vocabulary/
       └── VocabularyScreen.kt
```

### 方案 B: 简化四层架构（务实）

如果五层太复杂，可以合并 indexer 和 loader：

```
1. data/ (数据层)
2. algorithm/ (算法层)
3. core/ (核心层 = indexer + loader)
   ├── WordPoolIndexer.kt  # 索引管理
   └── WordLoader.kt       # 加载管理
4. ui/ (界面层)
```

### 需要删除的文件
- `data/ReviewManager.kt` - 功能已被 ReviewAlgorithm 替代
- `data/StreamingWordLoader.kt` - 功能已被 WordLoader 替代
- `data/JsonWordLoader.kt` - 如果不再使用

### 需要重构的文件
1. **LearningProgressManager.kt**
   - 拆分为 `LearningProgressData.kt` (data层)
   - 算法逻辑移到 indexer 层

2. **WordPoolIndexer.kt**
   - 移到独立的 indexer 文件夹
   - 或者保留在 algorithm 文件夹但重命名为 `WordPoolCoordinator.kt`

3. **WordLoader.kt**
   - 增强功能：分块加载、内存管理、缓存管理
   - 或者移到独立的 loader 文件夹

## 代码质量评估

### 优点
✅ 算法层的三个算法文件职责清晰
✅ 数据模型定义清晰
✅ UI 层使用 WordLoader 作为中间件，解耦良好

### 缺点
❌ 层次混乱，没有清晰的五层架构
❌ 存在重复代码（ReviewManager, StreamingWordLoader）
❌ 职责不清（LearningProgressManager, WordPoolIndexer）
❌ WordLoader 功能不完整，只是简单转发

### 总体评分
- 架构清晰度：⭐⭐⭐ (3/5)
- 代码质量：⭐⭐⭐⭐ (4/5)
- 可维护性：⭐⭐⭐ (3/5)
- 可扩展性：⭐⭐⭐⭐ (4/5)

## 下一步行动建议

### 立即行动（高优先级）
1. 删除重复文件（ReviewManager, StreamingWordLoader）
2. 拆分 LearningProgressManager

### 短期行动（中优先级）
3. 增强 WordLoader 功能
4. 重新组织文件夹结构

### 长期行动（低优先级）
5. 建立完整的五层架构
6. 添加单元测试
7. 性能优化
