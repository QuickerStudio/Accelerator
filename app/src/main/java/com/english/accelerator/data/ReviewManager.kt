package com.english.accelerator.data

import android.content.Context
import android.content.SharedPreferences
import kotlin.random.Random

/**
 * 单词复习管理器
 * 负责管理未记住单词的复习逻辑
 *
 * 算法：洗牌式混合
 * 1. 加载新单词页面时，获取未记住的单词池
 * 2. 按比例（如每 8 个新单词插入 1 个复习单词）将复习单词随机插入
 * 3. 如果未记住的单词被标记为"已记住"，从池中移除
 */
object ReviewManager {
    private const val PREFS_NAME = "review_prefs"
    private const val KEY_REVIEW_RATIO = "review_ratio"

    // 默认每 8 个新单词插入 1 个复习单词
    private const val DEFAULT_REVIEW_RATIO = 8

    private var sharedPreferences: SharedPreferences? = null
    private var reviewRatio = DEFAULT_REVIEW_RATIO

    /**
     * 初始化
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        reviewRatio = sharedPreferences?.getInt(KEY_REVIEW_RATIO, DEFAULT_REVIEW_RATIO) ?: DEFAULT_REVIEW_RATIO
    }

    /**
     * 混合新单词和复习单词（洗牌算法）
     * @param newWords 新单词列表
     * @return 混合后的单词列表
     */
    fun shuffleWithReviewWords(newWords: List<Word>): List<Word> {
        val unmemorizedWords = getUnmemorizedWords()
        if (unmemorizedWords.isEmpty() || newWords.isEmpty()) {
            return newWords
        }

        // 计算需要插入多少个复习单词
        val reviewCount = (newWords.size / reviewRatio).coerceAtMost(unmemorizedWords.size)
        if (reviewCount == 0) {
            return newWords
        }

        // 随机选择复习单词
        val selectedReviewWords = unmemorizedWords.shuffled().take(reviewCount)

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
     */
    private fun getUnmemorizedWords(): List<Word> {
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
     * 设置复习比例（每 N 个新单词插入 1 个复习单词）
     */
    fun setReviewRatio(ratio: Int) {
        reviewRatio = ratio.coerceIn(3, 20) // 限制在 3-20 之间
        sharedPreferences?.edit()?.putInt(KEY_REVIEW_RATIO, reviewRatio)?.apply()
    }

    /**
     * 获取当前复习比例
     */
    fun getReviewRatio(): Int {
        return reviewRatio
    }
}
