package com.english.accelerator.algorithm

import android.content.Context
import com.english.accelerator.algorithm.types.WordPoolStatistics
import com.english.accelerator.algorithm.types.WordPoolType
import com.english.accelerator.data.LearningProgressManager
import com.english.accelerator.data.Word
import com.english.accelerator.data.WordLearningManager

/**
 * 单词池管理器（核心调度器）
 *
 * 功能：
 * 1. 根据用户设置选择合适的推送算法（默认计划 or 每日计划）
 * 2. 协调 DefaultPlanAlgorithm、DailyPlanAlgorithm 和 ReviewAlgorithm
 * 3. 管理学习池的生命周期
 * 4. 提供统一的 API 供 UI 层调用
 */
class WordPoolManager private constructor() {

    companion object {
        @Volatile
        private var instance: WordPoolManager? = null

        fun getInstance(): WordPoolManager {
            return instance ?: synchronized(this) {
                instance ?: WordPoolManager().also { instance = it }
            }
        }
    }

    /**
     * 初始化
     */
    fun init(context: Context) {
        // 初始化依赖的管理器
        LearningProgressManager.init(context)
        WordLearningManager.init(context)
    }

    /**
     * 获取下一批单词
     *
     * @param count 获取的单词数量（默认50个）
     * @param includeReview 是否包含复习单词（默认true）
     * @return 单词列表
     */
    fun getNextBatch(
        count: Int = 50,
        includeReview: Boolean = true
    ): List<Word> {
        val poolSize = LearningProgressManager.getPoolSize()

        return if (poolSize == 0) {
            // 默认计划模式
            val currentIndex = getCurrentIndex()
            DefaultPlanAlgorithm.getNextBatch(
                startIndex = currentIndex,
                count = count,
                includeReview = includeReview
            )
        } else {
            // 每日计划模式
            val poolWordIds = LearningProgressManager.getLearningPool()
            DailyPlanAlgorithm.getNextBatch(
                poolWordIds = poolWordIds,
                count = count,
                includeReview = includeReview
            )
        }
    }

    /**
     * 标记单词为已记住
     *
     * @param wordId 单词ID
     */
    fun markAsMemorized(wordId: Int) {
        // 更新 WordLearningManager
        val word = com.english.accelerator.data.WordRepository.getWordById(wordId)
        if (word != null) {
            WordLearningManager.recordWord(wordId, word.word, isMemorized = true)
        }

        // 如果是每日计划模式，更新学习池
        val poolSize = LearningProgressManager.getPoolSize()
        if (poolSize > 0) {
            LearningProgressManager.markWordAsMemorized(wordId)
        }
    }

    /**
     * 标记单词为未记住
     *
     * @param wordId 单词ID
     */
    fun markAsUnmemorized(wordId: Int) {
        val word = com.english.accelerator.data.WordRepository.getWordById(wordId)
        if (word != null) {
            WordLearningManager.recordWord(wordId, word.word, isMemorized = false)
        }
    }

    /**
     * 设置学习池大小（0表示默认模式，>0表示每日计划模式）
     *
     * @param size 学习池大小
     */
    fun setPoolSize(size: Int) {
        LearningProgressManager.setPoolSize(size)
    }

    /**
     * 获取当前学习池大小
     *
     * @return 学习池大小
     */
    fun getPoolSize(): Int {
        return LearningProgressManager.getPoolSize()
    }

    /**
     * 获取当前使用的单词池类型
     *
     * @return 单词池类型
     */
    fun getPoolType(): WordPoolType {
        return if (LearningProgressManager.getPoolSize() == 0) {
            WordPoolType.DEFAULT_PLAN
        } else {
            WordPoolType.DAILY_PLAN
        }
    }

    /**
     * 获取统计信息
     *
     * @return 单词池统计信息
     */
    fun getStatistics(): WordPoolStatistics {
        val poolSize = LearningProgressManager.getPoolSize()
        val poolType = getPoolType()

        val memorizedCount = WordLearningManager.getMemorizedWordsCount()
        val unmemorizedCount = ReviewAlgorithm.getUnmemorizedCount()
        val studyingCount = LearningProgressManager.getStudyingWordsCount()
        val remainingCount = if (poolType == WordPoolType.DEFAULT_PLAN) {
            DefaultPlanAlgorithm.getRemainingCount(getCurrentIndex())
        } else {
            DailyPlanAlgorithm.getRemainingCount(LearningProgressManager.getNextWordId())
        }

        return WordPoolStatistics(
            memorizedCount = memorizedCount,
            unmemorizedCount = unmemorizedCount,
            studyingCount = studyingCount,
            remainingCount = remainingCount,
            reviewCount = 0, // TODO: 实现复习次数统计
            poolType = poolType
        )
    }

    /**
     * 获取当前索引（默认计划模式使用）
     *
     * @return 当前索引
     */
    private fun getCurrentIndex(): Int {
        val pageIndex = LearningProgressManager.getCurrentPageIndex()
        val indexInPage = LearningProgressManager.getCurrentIndexInPage()
        return pageIndex * 50 + indexInPage
    }

    /**
     * 获取下一个要补充的单词ID（每日计划模式使用）
     *
     * @return 下一个单词ID
     */
    private fun getNextWordId(): Int {
        return LearningProgressManager.getNextWordId()
    }

    /**
     * 检查是否还有更多单词
     *
     * @return 是否还有更多单词
     */
    fun hasMoreWords(): Boolean {
        val poolType = getPoolType()
        return if (poolType == WordPoolType.DEFAULT_PLAN) {
            DefaultPlanAlgorithm.hasMoreWords(getCurrentIndex())
        } else {
            val poolWordIds = LearningProgressManager.getLearningPool()
            poolWordIds.isNotEmpty() || DailyPlanAlgorithm.hasMoreWords(getNextWordId())
        }
    }
}
