package com.english.accelerator.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random

/**
 * 单词复习管理器
 * 负责管理未记住单词的复习逻辑
 *
 * 算法：
 * 1. 收集所有标记为"未记住"的单词
 * 2. 每滑动 N 次（可配置），从未记住池中随机抽取一个单词插入
 * 3. 如果未记住的单词被标记为"已记住"，从池中移除
 */
object ReviewManager {
    private const val PREFS_NAME = "review_prefs"
    private const val KEY_SWIPE_COUNT = "swipe_count"
    private const val KEY_REVIEW_INTERVAL = "review_interval"

    // 默认每 8 次滑动插入一个复习单词
    private const val DEFAULT_REVIEW_INTERVAL = 8

    private var sharedPreferences: SharedPreferences? = null
    private var swipeCount = 0
    private var reviewInterval = DEFAULT_REVIEW_INTERVAL

    /**
     * 初始化
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        swipeCount = sharedPreferences?.getInt(KEY_SWIPE_COUNT, 0) ?: 0
        reviewInterval = sharedPreferences?.getInt(KEY_REVIEW_INTERVAL, DEFAULT_REVIEW_INTERVAL) ?: DEFAULT_REVIEW_INTERVAL
    }

    /**
     * 记录一次滑动
     * @return 是否应该插入复习单词
     */
    fun recordSwipe(): Boolean {
        swipeCount++
        saveSwipeCount()

        // 检查是否达到复习间隔
        if (swipeCount >= reviewInterval) {
            swipeCount = 0
            saveSwipeCount()
            return true
        }
        return false
    }

    /**
     * 获取一个需要复习的单词
     * 从未记住的单词中随机选择
     */
    fun getReviewWord(context: Context): Word? {
        val unmemorizedWords = getUnmemorizedWords()
        if (unmemorizedWords.isEmpty()) {
            return null
        }

        // 随机选择一个未记住的单词
        val randomIndex = Random.nextInt(unmemorizedWords.size)
        val wordId = unmemorizedWords[randomIndex]

        // 从词库中获取完整的单词数据
        return WordRepository.getWordById(wordId)
    }

    /**
     * 获取所有未记住的单词 ID 列表
     */
    private fun getUnmemorizedWords(): List<Int> {
        val allRecords = WordLearningManager.getAllRecords()
        return allRecords
            .filter { !it.isMemorized }
            .map { it.wordId }
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
     * 设置复习间隔
     */
    fun setReviewInterval(interval: Int) {
        reviewInterval = interval.coerceIn(3, 20) // 限制在 3-20 之间
        sharedPreferences?.edit()?.putInt(KEY_REVIEW_INTERVAL, reviewInterval)?.apply()
    }

    /**
     * 获取当前复习间隔
     */
    fun getReviewInterval(): Int {
        return reviewInterval
    }

    /**
     * 重置滑动计数
     */
    fun resetSwipeCount() {
        swipeCount = 0
        saveSwipeCount()
    }

    /**
     * 保存滑动计数
     */
    private fun saveSwipeCount() {
        sharedPreferences?.edit()?.putInt(KEY_SWIPE_COUNT, swipeCount)?.apply()
    }

    /**
     * 获取当前滑动计数
     */
    fun getSwipeCount(): Int {
        return swipeCount
    }
}
