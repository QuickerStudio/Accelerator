# 单词学习系统设计文档

## 1. 现有系统分析

### 1.1 词典信息
- **总单词数**: 4998个
- **数据源**: `app/src/main/res/raw/ecdict_words.json`
- **单词结构**: ID (1-4998), word, phonetic, translation, example, frequency, level, pos

### 1.2 现有复习算法 (ReviewManager)
- **算法名称**: 洗牌式混合 (Shuffle Algorithm)
- **核心逻辑**:
  - 每加载一页新单词（50个），从未记住的单词池中随机选择复习单词
  - 按比例插入：默认每8个新单词插入1个复习单词
  - 复习单词随机插入到新单词中（避免插入到最开始）
- **核心方法**: `ReviewManager.shuffleWithReviewWords(newWords: List<Word>): List<Word>`
- **数据分类**:
  - **已记住**: `WordLearningManager` 中 `isMemorized = true` 的单词
  - **未记住**: `WordLearningManager` 中 `isMemorized = false` 的单词

### 1.3 单词卡片显示
- **每页显示**: 50个单词一组
- **刷新时机**: 在第49个单词时刷新，随机插入下一组

## 2. 新系统设计方案

### 2.1 两种学习模式

#### 模式1: 默认模式 (poolSize = 0)
- **适用场景**: 用户不设置每日学习单词数，或设置为0
- **推送逻辑**:
  - 使用现有的 ReviewManager 算法
  - 按词典顺序推送所有4998个单词
  - 不限制学习池大小
  - 复习单词按比例随机插入到新单词中

#### 模式2: 学习池模式 (poolSize > 0)
- **适用场景**: 用户设置每日学习单词数（如100个）
- **推送逻辑**:
  - 维护固定大小的学习池（如100个单词）
  - 单词被标记为"已记住"时，从池中移除
  - 从词典后续索引补充新单词到学习池
  - 学习池动态更新，不按天重置
  - 复习单词仍然按比例随机插入

### 2.2 学习池模式详细设计

#### 数据结构
```kotlin
// 学习池中的单词ID列表
private const val KEY_LEARNING_POOL = "learning_pool" // List<Int>

// 下一个要补充的单词ID（词典索引位置）
private const val KEY_NEXT_WORD_ID = "next_word_id" // Int

// 学习池大小
private const val KEY_POOL_SIZE = "pool_size" // Int
```

#### 核心流程
1. **初始化学习池**:
   - 用户设置学习池大小（如100个）
   - 从词典ID=1开始，填充100个单词到学习池
   - 设置 nextWordId = 101

2. **单词被标记为已记住**:
   - 从学习池中移除该单词ID
   - 从词典索引 nextWordId 补充1个新单词
   - nextWordId++
   - 保持学习池大小不变

3. **单词卡片显示**:
   - 从学习池中获取单词
   - 使用 ReviewManager.shuffleWithReviewWords() 混合复习单词
   - 每50个单词一组显示

#### 统计维度
- **已记住**: 总共记住的单词数（累计，从 WordLearningManager 获取）
- **学习中**: 当前学习池中的单词数（poolSize）
- **超额完成**: 今天记住的单词数 - 学习池大小（如果为正）
- **复习次数**: 复习旧单词的次数

### 2.3 推送算法整合

#### 问题点
1. **如何整合两种模式？**
   - 默认模式：直接使用现有的 ReviewManager 算法
   - 学习池模式：需要从学习池中获取单词，然后使用 ReviewManager 混合复习单词

2. **复习单词的来源？**
   - 复习单词来自 WordLearningManager 中 `isMemorized = false` 的单词
   - 这些单词可能在学习池中，也可能不在学习池中
   - **需要明确**: 学习池模式下，复习单词是否只从学习池中选择？

3. **单词卡片如何获取单词？**
   - 当前单词卡片从 WordRepository 按页加载
   - 需要修改为从学习池或词典加载
   - 需要与 ReviewManager 集成

## 3. 待讨论的问题

### 3.1 学习池模式下的复习逻辑
**问题**: 学习池模式下，复习单词应该从哪里选择？

**方案A**: 复习单词只从学习池中选择
- 优点: 逻辑简单，用户只需关注学习池中的单词
- 缺点: 如果学习池中没有未记住的单词，就没有复习单词了

**方案B**: 复习单词从所有未记住的单词中选择（包括学习池外的）
- 优点: 可以复习更多单词
- 缺点: 可能会出现学习池外的单词，用户可能会困惑

**建议**: 方案A，复习单词只从学习池中选择

### 3.2 单词卡片的加载逻辑
**问题**: 单词卡片应该如何加载单词？

**当前逻辑**:
```kotlin
// VocabularyScreen 中
val words = WordRepository.getWordsPage(pageIndex, pageSize)
val mixedWords = ReviewManager.shuffleWithReviewWords(words)
```

**新逻辑（学习池模式）**:
```kotlin
// 从学习池获取单词
val poolWordIds = LearningProgressManager.getLearningPool()
val words = poolWordIds.mapNotNull { WordRepository.getWordById(it) }
val mixedWords = ReviewManager.shuffleWithReviewWords(words)
```

### 3.3 学习池大小调整
**问题**: 用户调整学习池大小时，如何处理？

**场景1**: 从100个增加到150个
- 补充50个新单词到学习池

**场景2**: 从100个减少到50个
- 从学习池末尾移除50个单词

**场景3**: 从0（默认模式）切换到100（学习池模式）
- 初始化学习池，填充100个单词

**场景4**: 从100（学习池模式）切换到0（默认模式）
- 清空学习池，使用默认算法

## 4. 实现计划

### 4.1 阶段1: 完善 LearningProgressManager
- [x] 实现学习池管理逻辑
- [x] 实现 markWordAsMemorized() 方法
- [ ] 添加学习池模式下的复习单词过滤逻辑

### 4.2 阶段2: 修改单词卡片加载逻辑
- [ ] 修改 VocabularyScreen 的单词加载逻辑
- [ ] 根据学习池模式选择不同的加载方式
- [ ] 集成 ReviewManager 的复习算法

### 4.3 阶段3: 更新UI
- [ ] 将学习计划页面的"每日学习单词数"改为文本框（默认0）
- [ ] 更新学习统计页面显示新的统计维度
- [ ] 添加学习池大小调整的UI

### 4.4 阶段4: 测试
- [ ] 编写单元测试验证推送算法
- [ ] 测试学习池模式的单词补充逻辑
- [ ] 测试复习单词的随机插入逻辑
- [ ] 测试学习池大小调整逻辑

## 5. 技术细节

### 5.1 数据持久化
使用 SharedPreferences 存储：
- 学习池大小 (KEY_POOL_SIZE)
- 学习池中的单词ID列表 (KEY_LEARNING_POOL)
- 下一个要补充的单词ID (KEY_NEXT_WORD_ID)
- 今天的日期 (KEY_TODAY_DATE)
- 今天记住的单词数 (KEY_TODAY_MEMORIZED_COUNT)

### 5.2 性能优化
- 学习池使用 List<Int> 存储单词ID，而不是完整的 Word 对象
- 需要时从 WordRepository 获取完整的 Word 对象
- 使用 Gson 序列化/反序列化学习池数据

### 5.3 边界情况处理
- 学习池大小超过词典总数（4998个）
- 词典单词用完了（nextWordId > 4998）
- 学习池为空
- 没有未记住的单词可以复习

## 6. 下一步行动

1. **讨论并确定**: 学习池模式下的复习逻辑（方案A或方案B）
2. **封装复习逻辑**: 将 ReviewManager 的复习逻辑封装成独立的函数模块
3. **修改单词卡片**: 修改 VocabularyScreen 的单词加载逻辑
4. **编写测试**: 编写单元测试验证推送算法
5. **更新UI**: 更新学习计划和统计页面

---

**文档版本**: v1.0
**创建日期**: 2026-03-04
**最后更新**: 2026-03-04
