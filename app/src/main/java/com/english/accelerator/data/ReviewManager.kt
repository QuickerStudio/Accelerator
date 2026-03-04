package com.english.accelerator.data

import android.content.Context
import android.content.SharedPreferences
import kotlin.math.min

/**
 * 复习管理器
 * 负责将未记住的单词混入新单词列表中进行复习
 */
object ReviewManager {
    private var sharedPreferences: SharedPreferences? = null
    private const val PREFS_NAME = "review_manager_prefs"
    private const val KEY_REVIEW_RATIO = "review_ratio"
    private const val DEFAULT_RATIO = 8
    private const val MIN_RATIO = 3
    private const val MAX_RATIO = 20

    private var reviewRatio = DEFAULT_RATIO

    /**
     * 初始化
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        reviewRatio = sharedPreferences?.getInt(KEY_REVIEW_RATIO, DEFAULT_RATIO) ?: DEFAULT_RATIO
    }

    /**
     * 将复习单词混入新单词列表
     * @param newWords 新单词列表
     * @return 混合后的单词列表
     */
    fun shuffleWithReviewWords(newWords: List<Word>): List<Word> {
        if (newWords.isEmpty()) return emptyList()

        // 获取所有未记住的单词
        val unmemorizedWords = getUnmemorizedWords()
        if (unmemorizedWords.isEmpty()) return newWords

        // 计算需要插入的复习单词数量
        val reviewCount = min(newWords.size / reviewRatio, unmemorizedWords.size)
        if (reviewCount == 0) return newWords

        // 随机选择复习单词
        val reviewWords = unmemorizedWords.shuffled().take(reviewCount)

        // 将复习单词插入到新单词列表中
        val result = mutableListOf<Word>()
        result.addAll(newWords)

        // 在随机位置插入复习单词（避免前2个位置）
        reviewWords.forEach { reviewWord ->
            val insertPosition = (2 until result.size).random()
            result.add(insertPosition, reviewWord)
        }

        return result
    }

    /**
     * 检查是否有需要复习的单词
     */
    fun hasReviewWords(): Boolean {
        return getUnmemorizedWords().isNotEmpty()
    }

    /**
     * 获取未记住单词的数量
     */
    fun getUnmemorizedCount(): Int {
        return getUnmemorizedWords().size
    }

    /**
     * 设置复习比例
     * @param ratio 新单词:复习单词的比例（例如 8 表示 8:1）
     */
    fun setReviewRatio(ratio: Int) {
        reviewRatio = ratio.coerceIn(MIN_RATIO, MAX_RATIO)
        sharedPreferences?.edit()?.putInt(KEY_REVIEW_RATIO, reviewRatio)?.apply()
    }

    /**
     * 获取当前复习比例
     */
    fun getReviewRatio(): Int {
        return reviewRatio
    }

    /**
     * 获取所有未记住的单词
     */
    private fun getUnmemorizedWords(): List<Word> {
        val allRecords = WordLearningManager.getAllRecords()
        return allRecords
            .filter { !it.isMemorized }
            .mapNotNull { WordRepository.getWordById(it.wordId) }
    }
}
