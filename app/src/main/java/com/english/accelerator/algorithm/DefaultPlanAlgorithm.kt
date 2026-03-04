/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.algorithm

import com.english.accelerator.data.Word
import com.english.accelerator.data.WordRepository

/**
 * 默认计划算法
 *
 * 触发条件：每日学习单词数设置为 0
 *
 * 算法逻辑：
 * 1. 按词典顺序推送所有单词（1-4998）
 * 2. 使用 ReviewAlgorithm 混合复习单词
 * 3. 每50个单词一组
 * 4. 在第49个单词时刷新，随机插入下一组
 */
object DefaultPlanAlgorithm {
    /**
     * 词典总单词数
     */
    private const val TOTAL_WORDS = 4998

    /**
     * 每页单词数
     */
    private const val PAGE_SIZE = 50

    /**
     * 获取下一批单词（默认计划模式）
     *
     * @param startIndex 起始索引（从0开始）
     * @param count 获取的单词数量
     * @param includeReview 是否包含复习单词
     * @return 单词列表（可能包含复习单词）
     */
    fun getNextBatch(
        startIndex: Int,
        count: Int = PAGE_SIZE,
        includeReview: Boolean = true
    ): List<Word> {
        // 计算起始单词ID（词典ID从1开始）
        val startWordId = startIndex + 1

        // 获取新单词
        val newWords = mutableListOf<Word>()
        for (i in 0 until count) {
            val wordId = startWordId + i
            if (wordId > TOTAL_WORDS) {
                break // 已经到达词典末尾
            }

            val word = WordRepository.getWordById(wordId)
            if (word != null) {
                newWords.add(word)
            }
        }

        // 如果需要包含复习单词，使用 ReviewAlgorithm 混合
        return if (includeReview && newWords.isNotEmpty()) {
            ReviewAlgorithm.shuffleWithReviewWords(newWords)
        } else {
            newWords
        }
    }

    /**
     * 获取剩余单词数
     *
     * @param currentIndex 当前索引
     * @return 剩余单词数
     */
    fun getRemainingCount(currentIndex: Int): Int {
        return (TOTAL_WORDS - currentIndex).coerceAtLeast(0)
    }

    /**
     * 检查是否还有更多单词
     *
     * @param currentIndex 当前索引
     * @return 是否还有更多单词
     */
    fun hasMoreWords(currentIndex: Int): Boolean {
        return currentIndex < TOTAL_WORDS
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
