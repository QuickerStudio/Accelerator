package com.english.accelerator.algorithm

import com.english.accelerator.data.Word
import com.english.accelerator.data.WordRepository

/**
 * 每日计划算法
 *
 * 触发条件：每日学习单词数设置 > 0（如100个）
 *
 * 算法逻辑：
 * 1. 维护固定大小的学习池
 * 2. 单词被标记为"已记住"时，从池中移除
 * 3. 从词典后续索引补充新单词
 * 4. 使用 ReviewAlgorithm 混合复习单词
 * 5. 学习池动态更新，不按天重置
 */
object DailyPlanAlgorithm {
    /**
     * 词典总单词数
     */
    private const val TOTAL_WORDS = 4998

    /**
     * 每页单词数
     */
    private const val PAGE_SIZE = 50

    /**
     * 从学习池获取下一批单词
     *
     * @param poolWordIds 学习池中的单词ID列表
     * @param count 获取的单词数量
     * @param includeReview 是否包含复习单词
     * @return 单词列表（可能包含复习单词）
     */
    fun getNextBatch(
        poolWordIds: List<Int>,
        count: Int = PAGE_SIZE,
        includeReview: Boolean = true
    ): List<Word> {
        if (poolWordIds.isEmpty()) {
            return emptyList()
        }

        // 从学习池中获取单词
        val poolWords = poolWordIds.take(count).mapNotNull { id ->
            WordRepository.getWordById(id)
        }

        // 如果需要包含复习单词，使用 ReviewAlgorithm 混合
        // 注意：只从学习池中选择复习单词
        return if (includeReview && poolWords.isNotEmpty()) {
            val unmemorizedInPool = ReviewAlgorithm.getUnmemorizedWordsFromPool(poolWordIds)
            ReviewAlgorithm.shuffleWithReviewWords(
                newWords = poolWords,
                unmemorizedWords = unmemorizedInPool
            )
        } else {
            poolWords
        }
    }

    /**
     * 从词典获取新单词来补充学习池
     *
     * @param nextWordId 下一个要补充的单词ID
     * @param count 需要补充的单词数量
     * @return 新单词ID列表
     */
    fun getNewWordsForPool(
        nextWordId: Int,
        count: Int
    ): List<Int> {
        val newWordIds = mutableListOf<Int>()
        var currentId = nextWordId

        for (i in 0 until count) {
            if (currentId > TOTAL_WORDS) {
                break // 已经到达词典末尾
            }
            newWordIds.add(currentId)
            currentId++
        }

        return newWordIds
    }

    /**
     * 计算下一个要补充的单词ID
     *
     * @param currentNextWordId 当前的下一个单词ID
     * @param addedCount 已添加的单词数量
     * @return 新的下一个单词ID
     */
    fun calculateNextWordId(
        currentNextWordId: Int,
        addedCount: Int
    ): Int {
        return (currentNextWordId + addedCount).coerceAtMost(TOTAL_WORDS + 1)
    }

    /**
     * 检查是否还有更多单词可以补充
     *
     * @param nextWordId 下一个要补充的单词ID
     * @return 是否还有更多单词
     */
    fun hasMoreWords(nextWordId: Int): Boolean {
        return nextWordId <= TOTAL_WORDS
    }

    /**
     * 获取剩余可补充的单词数
     *
     * @param nextWordId 下一个要补充的单词ID
     * @return 剩余单词数
     */
    fun getRemainingCount(nextWordId: Int): Int {
        return (TOTAL_WORDS - nextWordId + 1).coerceAtLeast(0)
    }

    /**
     * 获取词典总单词数
     *
     * @return 词典总单词数
     */
    fun getTotalWords(): Int {
        return TOTAL_WORDS
    }

    /**
     * 获取每页单词数
     *
     * @return 每页单词数
     */
    fun getPageSize(): Int {
        return PAGE_SIZE
    }
}
