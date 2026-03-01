# 单词学习算法设计文档

## 1. 核心概念

### 1.1 分组学习机制
- 单词按组管理，每组 50 个单词
- 用户需完成当前组才能进入下一组
- 支持多组并行学习（复习旧组 + 学习新组）

### 1.2 单词难度分级
```
EASY (初级)    - 3-6个字母，常用词，如 cat, run, good
MEDIUM (中级)  - 7-10个字母，中等难度，如 beautiful, important
HARD (高级)    - 11+个字母，复杂词汇，如 extraordinary, responsibility
```

### 1.3 单词状态
```
NEW (新词) → LEARNING (学习中) → MASTERED (已掌握)
```

## 2. 间隔重复算法 (Modified Leitner System)

### 2.1 算法原理
基于 Leitner 系统的改进版本，结合了以下特点：
- 简单易懂的盒子系统
- 适合滑卡片的交互方式
- 针对短期记忆优化

### 2.2 盒子系统
```
Box 0: 新词 (立即出现)
Box 1: 第一次标记"已记住" (3分钟后复习)
Box 2: 第二次标记"已记住" (30分钟后复习)
Box 3: 第三次标记"已记住" (12小时后复习)
Box 4: 第四次标记"已记住" (2天后复习)
Box 5: 已掌握 (5天后复习)
```

### 2.3 难度渐进式推流规则

#### 组内单词难度配比 (50词/组)
```
EASY (初级):   20词 (40%) - 前15个位置
MEDIUM (中级): 20词 (40%) - 中间20个位置
HARD (高级):   10词 (20%) - 最后15个位置
```

#### 推流策略 (由易到难)
1. **热身阶段 (前10个单词)**:
   - 100% EASY 新词
   - 目标: 建立信心，快速进入状态

2. **渐进阶段 (11-30个单词)**:
   - 60% EASY/MEDIUM 新词
   - 30% 到期复习词 (Box 1-2)
   - 10% 未记住的词 (Box 0)

3. **挑战阶段 (31-50个单词)**:
   - 40% MEDIUM/HARD 新词
   - 40% 到期复习词
   - 20% 未记住的词

#### 滑动反馈处理
- **向右滑 (已记住)**:
  - 单词晋级到下一个 Box
  - 设置下次复习时间
  - 达到 Box 5 标记为 MASTERED

- **向左滑 (未记住)**:
  - 单词降级到 Box 0
  - 立即重新加入学习队列
  - 插入位置: 在接下来的 3-5 个单词后出现 (避免连续打击信心)

#### 智能插入策略
```
当前队列 = [
  新词 (按难度排序: EASY → MEDIUM → HARD),
  到期复习词 (优先 EASY),
  未记住的词 (间隔插入，避免连续出现)
]
```

**关键规则**:
- 未记住的词不会立即出现，而是间隔 3-5 个其他单词
- 连续出现 2 个 HARD 词后，必须插入 1 个 EASY 词作为缓冲
- 用户连续标记 3 个"未记住"后，自动降低难度，优先推送 EASY 词

### 2.4 完成条件
一组被认为"完成"需满足：
- 所有 50 个单词至少被标记"已记住" 1 次
- 至少 70% 的单词达到 Box 2 或更高
- EASY 词: 90% 达到 Box 3
- MEDIUM 词: 70% 达到 Box 2
- HARD 词: 50% 达到 Box 2

## 3. 数据结构设计

### 3.1 数据库 Schema (Room)

```kotlin
@Entity(tableName = "word_groups")
data class WordGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,                    // 组名，如 "CET-4 Group 1"
    val source: String,                  // 来源: "local_cet4", "custom", etc.
    val totalWords: Int = 50,
    val createdAt: Long,
    val status: GroupStatus              // LOCKED, ACTIVE, COMPLETED
)

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val phonetic: String,
    val translation: String,
    val example: String,
    val exampleTranslation: String,
    val audioUrl: String? = null,
    val source: String,                  // 词库来源
    val difficulty: WordDifficulty,      // EASY, MEDIUM, HARD
    val wordLength: Int                  // 单词字符长度
)

enum class WordDifficulty {
    EASY,      // 3-6个字母
    MEDIUM,    // 7-10个字母
    HARD       // 11+个字母
}

@Entity(
    tableName = "user_word_progress",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WordGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("wordId"), Index("groupId")]
)
data class UserWordProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long,
    val groupId: Long,
    val box: Int = 0,                    // 0-5 盒子编号
    val status: WordStatus,              // NEW, LEARNING, MASTERED, STRUGGLING
    val correctCount: Int = 0,           // 标记"已记住"次数
    val wrongCount: Int = 0,             // 标记"未记住"次数
    val lastReviewedAt: Long? = null,    // 上次复习时间
    val nextReviewAt: Long? = null,      // 下次复习时间
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "review_history")
data class ReviewHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long,
    val groupId: Long,
    val isCorrect: Boolean,              // true=已记住, false=未记住
    val reviewedAt: Long,
    val timeTaken: Long                  // 用户思考时间(毫秒)
)

enum class GroupStatus {
    LOCKED,      // 未解锁
    ACTIVE,      // 学习中
    COMPLETED    // 已完成
}

enum class WordStatus {
    NEW,         // 新词
    LEARNING,    // 学习中
    MASTERED     // 已掌握
}
```

### 3.2 词库来源配置

```kotlin
data class VocabularySource(
    val id: String,              // "cet4", "cet6", "toefl", "custom"
    val name: String,            // "大学英语四级"
    val totalWords: Int,
    val isBuiltIn: Boolean,      // 是否内置词库
    val filePath: String? = null // 自定义词库文件路径
)

// 内置词库
val BUILT_IN_SOURCES = listOf(
    VocabularySource("cet4", "大学英语四级", 4500, true),
    VocabularySource("cet6", "大学英语六级", 5500, true),
    VocabularySource("toefl", "托福核心词汇", 3000, true),
    VocabularySource("ielts", "雅思核心词汇", 3500, true)
)
```

## 4. 推流算法实现

### 4.1 核心算法类

```kotlin
class WordStreamAlgorithm(
    private val groupId: Long,
    private val progressDao: UserWordProgressDao,
    private val historyDao: ReviewHistoryDao,
    private val wordDao: WordDao
) {
    companion object {
        // 盒子间隔时间 (毫秒)
        val BOX_INTERVALS = longArrayOf(
            0L,                          // Box 0: 立即
            3 * 60 * 1000L,             // Box 1: 3分钟
            30 * 60 * 1000L,            // Box 2: 30分钟
            12 * 60 * 60 * 1000L,       // Box 3: 12小时
            2 * 24 * 60 * 60 * 1000L,   // Box 4: 2天
            5 * 24 * 60 * 60 * 1000L    // Box 5: 5天
        )

        const val GROUP_SIZE = 50
        const val CONFIDENCE_BUFFER = 3  // 连续失败3次后降低难度
    }

    private var recentWrongCount = 0  // 最近连续"未记住"次数
    private var lastHardWordCount = 0 // 连续出现的 HARD 词数量
    private val wrongWordsQueue = mutableListOf<Long>()  // 未记住的词队列

    /**
     * 获取下一个要展示的单词 (难度渐进式)
     */
    suspend fun getNextWord(): WordEntity? {
        val now = System.currentTimeMillis()
        val progress = progressDao.getGroupProgress(groupId)
        val learnedCount = progress.count { it.status != WordStatus.NEW }

        // 判断当前学习阶段
        val phase = when {
            learnedCount < 10 -> LearningPhase.WARMUP
            learnedCount < 30 -> LearningPhase.PROGRESSIVE
            else -> LearningPhase.CHALLENGE
        }

        // 1. 检查是否需要插入"未记住"的词 (间隔插入)
        if (wrongWordsQueue.isNotEmpty() && shouldInsertWrongWord()) {
            val wordId = wrongWordsQueue.removeFirst()
            return wordDao.getWordById(wordId)
        }

        // 2. 到期复习词 (优先 EASY)
        val dueWords = getDueWords(now)
        if (dueWords.isNotEmpty() && shouldReview(phase)) {
            val easyDueWords = dueWords.filter { it.difficulty == WordDifficulty.EASY }
            return (easyDueWords.ifEmpty { dueWords }).random()
        }

        // 3. 新词 (按难度和阶段选择)
        val newWords = getNewWordsByPhase(phase)
        if (newWords.isNotEmpty()) {
            // 连续出现2个HARD词后，强制插入EASY词
            if (lastHardWordCount >= 2) {
                val easyWords = newWords.filter { it.difficulty == WordDifficulty.EASY }
                if (easyWords.isNotEmpty()) {
                    lastHardWordCount = 0
                    return easyWords.first()
                }
            }

            // 连续失败3次后，降低难度
            if (recentWrongCount >= CONFIDENCE_BUFFER) {
                val easyWords = newWords.filter { it.difficulty == WordDifficulty.EASY }
                if (easyWords.isNotEmpty()) {
                    recentWrongCount = 0
                    return easyWords.first()
                }
            }

            val word = newWords.first()
            if (word.difficulty == WordDifficulty.HARD) {
                lastHardWordCount++
            } else {
                lastHardWordCount = 0
            }
            return word
        }

        return null
    }

    private fun shouldInsertWrongWord(): Boolean {
        // 每隔3-5个单词插入一个"未记住"的词
        return Random.nextInt(3, 6) == 3
    }

    private fun shouldReview(phase: LearningPhase): Boolean {
        return when (phase) {
            LearningPhase.WARMUP -> false
            LearningPhase.PROGRESSIVE -> Random.nextFloat() < 0.3f
            LearningPhase.CHALLENGE -> Random.nextFloat() < 0.4f
        }
    }

    private suspend fun getNewWordsByPhase(phase: LearningPhase): List<WordEntity> {
        val allNewWords = progressDao.getWordsByStatus(groupId, WordStatus.NEW)

        return when (phase) {
            LearningPhase.WARMUP -> {
                // 只返回 EASY 词
                allNewWords.filter { it.difficulty == WordDifficulty.EASY }
            }
            LearningPhase.PROGRESSIVE -> {
                // EASY 和 MEDIUM 词
                allNewWords.filter {
                    it.difficulty == WordDifficulty.EASY ||
                    it.difficulty == WordDifficulty.MEDIUM
                }
            }
            LearningPhase.CHALLENGE -> {
                // 所有难度，但按 EASY → MEDIUM → HARD 排序
                allNewWords.sortedBy {
                    when (it.difficulty) {
                        WordDifficulty.EASY -> 0
                        WordDifficulty.MEDIUM -> 1
                        WordDifficulty.HARD -> 2
                    }
                }
            }
        }
    }

    enum class LearningPhase {
        WARMUP,      // 热身阶段
        PROGRESSIVE, // 渐进阶段
        CHALLENGE    // 挑战阶段
    }

    /**
     * 处理用户滑动反馈
     */
    suspend fun handleSwipe(wordId: Long, isCorrect: Boolean) {
        val progress = progressDao.getProgress(wordId, groupId) ?: return
        val now = System.currentTimeMillis()

        // 记录历史
        historyDao.insert(ReviewHistory(
            wordId = wordId,
            groupId = groupId,
            isCorrect = isCorrect,
            reviewedAt = now,
            timeTaken = 0 // TODO: 实际计算
        ))

        if (isCorrect) {
            // 向右滑: 晋级
            recentWrongCount = 0  // 重置连续失败计数
            val newBox = minOf(progress.box + 1, 5)
            val nextReviewAt = now + BOX_INTERVALS[newBox]

            progressDao.update(progress.copy(
                box = newBox,
                status = if (newBox >= 5) WordStatus.MASTERED else WordStatus.LEARNING,
                correctCount = progress.correctCount + 1,
                lastReviewedAt = now,
                nextReviewAt = nextReviewAt,
                updatedAt = now
            ))
        } else {
            // 向左滑: 降级到 Box 0，加入队列等待重新出现
            recentWrongCount++
            wrongWordsQueue.add(wordId)  // 加入队列，稍后间隔插入

            progressDao.update(progress.copy(
                box = 0,
                status = WordStatus.LEARNING,
                wrongCount = progress.wrongCount + 1,
                lastReviewedAt = now,
                nextReviewAt = now,
                updatedAt = now
            ))
        }
    }

    /**
     * 检查当前组是否完成 (按难度分级检查)
     */
    suspend fun isGroupCompleted(): Boolean {
        val allProgress = progressDao.getGroupProgress(groupId)
        if (allProgress.size < GROUP_SIZE) return false

        val easyWords = allProgress.filter { it.word.difficulty == WordDifficulty.EASY }
        val mediumWords = allProgress.filter { it.word.difficulty == WordDifficulty.MEDIUM }
        val hardWords = allProgress.filter { it.word.difficulty == WordDifficulty.HARD }

        // EASY: 90% 达到 Box 3
        val easyRate = easyWords.count { it.box >= 3 }.toFloat() / easyWords.size

        // MEDIUM: 70% 达到 Box 2
        val mediumRate = mediumWords.count { it.box >= 2 }.toFloat() / mediumWords.size

        // HARD: 50% 达到 Box 2
        val hardRate = hardWords.count { it.box >= 2 }.toFloat() / hardWords.size

        return easyRate >= 0.9f && mediumRate >= 0.7f && hardRate >= 0.5f
    }

    // 辅助方法
    private suspend fun getDueWords(now: Long): List<WordEntity> =
        progressDao.getDueWords(groupId, now)
}
```

## 5. 用户设置项

### 5.1 设置界面配置

```kotlin
data class VocabularySettings(
    // 词库选择
    val selectedSource: String = "cet4",
    val customSources: List<String> = emptyList(),

    // 学习参数
    val groupSize: Int = 50,
    val dailyGoal: Int = 100,           // 每日学习目标(单词数)

    // 难度配比 (总和必须为100)
    val easyRatio: Int = 40,            // EASY 词占比 40%
    val mediumRatio: Int = 40,          // MEDIUM 词占比 40%
    val hardRatio: Int = 20,            // HARD 词占比 20%

    // 算法调整
    val confidenceBuffer: Int = 3,      // 连续失败N次后降低难度
    val wrongWordInsertGap: IntRange = 3..5,  // 未记住的词间隔N个单词后重新出现

    // 完成条件 (按难度分级)
    val easyCompletionRate: Float = 0.9f,     // EASY: 90%达到Box 3
    val mediumCompletionRate: Float = 0.7f,   // MEDIUM: 70%达到Box 2
    val hardCompletionRate: Float = 0.5f,     // HARD: 50%达到Box 2

    // 其他
    val autoPlayAudio: Boolean = false,
    val showExampleSentence: Boolean = true
)
```

## 6. 实现优先级

### Phase 1: 核心功能 (MVP)
1. 数据库设计与实现 (Room)
2. 基础推流算法
3. 单词状态管理
4. 滑动反馈处理

### Phase 2: 词库管理
1. 内置词库导入
2. 自定义词库导入 (CSV/JSON)
3. 词库切换功能

### Phase 3: 算法优化
1. 间隔重复算法完善
2. 学习数据统计
3. 智能推荐

### Phase 4: 高级功能
1. 多组并行学习
2. 学习报告
3. 云同步

## 7. 性能优化建议

1. **数据库索引**: 在 `nextReviewAt`, `status`, `groupId` 上建立索引
2. **缓存策略**: 缓存当前组的单词列表，减少数据库查询
3. **预加载**: 提前加载下一个单词的数据和音频
4. **批量操作**: 使用 Room 的批量插入/更新功能

## 8. 测试策略

1. **单元测试**: 算法逻辑测试
2. **集成测试**: 数据库操作测试
3. **UI测试**: 滑动交互测试
4. **性能测试**: 大量单词下的性能表现

---

**文档版本**: 1.0
**创建日期**: 2026-03-02
**作者**: Claude (Opus 4)
