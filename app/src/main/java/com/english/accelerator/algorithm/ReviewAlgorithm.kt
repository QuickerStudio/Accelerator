package com.english.accelerator.algorithm

import com.english.accelerator.data.Word
import com.english.accelerator.data.WordLearningManager
import com.english.accelerator.data.WordRepository
import kotlin.random.Random

/**
 * 复习算法（洗牌混合）
 *
 * 功能：将未记住的单词随机插入到新单词中
 *
 * 算法逻辑：
 * 1. 从未记住的单词池中随机选择复习单词
 * 2. 按比例插入：默认每8个新单词插入1个复习单词
 * 3. 复习单词随机插入到新单词中（避免插入到最开始）
 * 4. 在第49个单词时刷新，准备下一组
 */
object ReviewAlgorithm {
    /**
     * 默认复习比例：每8个新单词插入1个复习单词
     */
    private const val DEFAULT_REVIEW_RATIO = 8

    /**
     * 混合新单词和复习单词（洗牌算法）
     *
     * @param newWords 新单词列表
     * @param reviewRatio 复习比例（每N个新单词插入1个复习单词）
     * @param unmemorizedWords 未记住的单词列表（可选，如果不提供则自动获取）
     * @return 混合后的单词列表
     */
    fun shuffleWithReviewWords(
        newWords: List<Word>,
        reviewRatio: Int = DEFAULT_REVIEW_RATIO,
        unmemorizedWords: List<Word>? = null
    ): List<Word> {
        if (newWords.isEmpty()) {
            return newWords
        }

        // 获取未记住的单词
        val reviewPool = unmemorizedWords ?: getUnmemorizedWords()
        if (reviewPool.isEmpty()) {
            return newWords
        }

        // 计算需要插入多少个复习单词
        val reviewCount = (newWords.size / reviewRatio).coerceAtMost(reviewPool.size)
        if (reviewCount == 0) {
            return newWords
        }

        // 随机选择复习单词
        val selectedReviewWords = reviewPool.shuffled().take(reviewCount)

        // 创建混合列表
        val mixedWords = newWords.toMutableList()

        // 将复习单词随机插入到新单词中
        selectedReviewWords.forEach { reviewWord ->
            // 随机选择插入位置（避免插入到最开始，至少在第2个位置之后）
            val insertPosition = if (mixedWords.size > 2) {
                Random.nextInt(2, mixedWords.size)
            } else {
                mixedWords.size
            }
            mixedWords.add(insertPosition, reviewWord)
        }

        return mixedWords
    }

    /**
     * 获取所有未记住的单词
     *
     * @return 未记住的单词列表
     */
    fun getUnmemorizedWords(): List<Word> {
        val allRecords = WordLearningManager.getAllRecords()
        val unmemorizedIds = allRecords
            .filter { !it.isMemorized }
            .map { it.wordId }

        // 从词库中获取完整的单词数据
        return unmemorizedIds.mapNotNull { id ->
            WordRepository.getWordById(id)
        }
    }

    /**
     * 从指定的单词池中获取未记住的单词
     *
     * @param poolWordIds 单词池中的单词ID列表
     * @return 未记住的单词列表
     */
    fun getUnmemorizedWordsFromPool(poolWordIds: List<Int>): List<Word> {
        val allRecords = WordLearningManager.getAllRecords()
        val unmemorizedIds = allRecords
            .filter { !it.isMemorized && poolWordIds.contains(it.wordId) }
            .map { it.wordId }

        // 从词库中获取完整的单词数据
        return unmemorizedIds.mapNotNull { id ->
            WordRepository.getWordById(id)
        }
    }

    /**
     * 检查是否有需要复习的单词
     *
     * @return 是否有未记住的单词
     */
    fun hasReviewWords(): Boolean {
        return getUnmemorizedWords().isNotEmpty()
    }

    /**
     * 获取未记住单词的数量
     *
     * @return 未记住单词的数量
     */
    fun getUnmemorizedCount(): Int {
        return getUnmemorizedWords().size
    }

    /**
     * 获取默认复习比例
     *
     * @return 默认复习比例
     */
    fun getDefaultReviewRatio(): Int {
        return DEFAULT_REVIEW_RATIO
    }
}
