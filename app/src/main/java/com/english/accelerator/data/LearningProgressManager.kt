package com.english.accelerator.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

/**
 * 学习进度管理器
 *
 * 两种学习模式：
 * 1. 学习池模式（每日学习单词数 > 0）：
 *    - 维护固定大小的学习池
 *    - 单词被标记为"已记住"时从池中移除，从词典后续索引补充新单词
 *    - 学习池动态更新，不按天重置
 *
 * 2. 默认模式（每日学习单词数 = 0）：
 *    - 使用现有推送算法
 *    - 按词典顺序推送所有单词（4998个）
 *    - 不限制学习池大小
 */
object LearningProgressManager {
    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()

    private const val PREFS_NAME = "learning_progress_prefs"
    private const val KEY_CURRENT_PAGE_INDEX = "current_page_index"
    private const val KEY_CURRENT_INDEX_IN_PAGE = "current_index_in_page"

    // 学习池模式相关
    private const val KEY_POOL_SIZE = "pool_size" // 学习池大小（0表示默认模式）
    private const val KEY_LEARNING_POOL = "learning_pool" // 当前学习池中的单词ID列表
    private const val KEY_NEXT_WORD_ID = "next_word_id" // 下一个要补充的单词ID

    // 每日统计相关
    private const val KEY_TODAY_DATE = "today_date"
    private const val KEY_TODAY_MEMORIZED_COUNT = "today_memorized_count" // 今天记住的单词数

    private const val TOTAL_WORDS = 4998 // 词典总单词数

    /**
     * 初始化
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        checkAndResetDailyStats()
    }

    /**
     * 保存学习进度（旧版兼容）
     */
    fun saveProgress(pageIndex: Int, indexInPage: Int) {
        sharedPreferences?.edit()?.apply {
            putInt(KEY_CURRENT_PAGE_INDEX, pageIndex)
            putInt(KEY_CURRENT_INDEX_IN_PAGE, indexInPage)
            apply()
        }
    }

    /**
     * 获取当前页索引（旧版兼容）
     */
    fun getCurrentPageIndex(): Int {
        return sharedPreferences?.getInt(KEY_CURRENT_PAGE_INDEX, 0) ?: 0
    }

    /**
     * 获取当前页内索引（旧版兼容）
     */
    fun getCurrentIndexInPage(): Int {
        return sharedPreferences?.getInt(KEY_CURRENT_INDEX_IN_PAGE, 0) ?: 0
    }

    /**
     * 重置进度
     */
    fun resetProgress() {
        sharedPreferences?.edit()?.apply {
            putInt(KEY_CURRENT_PAGE_INDEX, 0)
            putInt(KEY_CURRENT_INDEX_IN_PAGE, 0)
            apply()
        }
    }

    // ========== 学习池模式管理 ==========

    /**
     * 设置学习池大小（0表示默认模式）
     */
    fun setPoolSize(size: Int) {
        val currentSize = getPoolSize()
        sharedPreferences?.edit()?.putInt(KEY_POOL_SIZE, size)?.apply()

        // 如果从默认模式切换到学习池模式，初始化学习池
        if (currentSize == 0 && size > 0) {
            initializeLearningPool(size)
        }
        // 如果学习池大小改变，调整学习池
        else if (size > 0 && size != currentSize) {
            adjustLearningPool(size)
        }
    }

    /**
     * 获取学习池大小
     */
    fun getPoolSize(): Int {
        return sharedPreferences?.getInt(KEY_POOL_SIZE, 0) ?: 0
    }

    /**
     * 初始化学习池
     */
    private fun initializeLearningPool(size: Int) {
        val pool = mutableListOf<Int>()
        val startId = 1

        // 填充学习池
        for (i in 0 until size.coerceAtMost(TOTAL_WORDS)) {
            pool.add(startId + i)
        }

        saveLearningPool(pool)
        setNextWordId(startId + size)
    }

    /**
     * 调整学习池大小
     */
    private fun adjustLearningPool(newSize: Int) {
        val currentPool = getLearningPool().toMutableList()

        when {
            newSize > currentPool.size -> {
                // 扩大学习池，补充新单词
                val needCount = newSize - currentPool.size
                val newWords = getNewWords(needCount)
                currentPool.addAll(newWords)
            }
            newSize < currentPool.size -> {
                // 缩小学习池，移除多余的单词（从后面移除）
                while (currentPool.size > newSize) {
                    currentPool.removeAt(currentPool.size - 1)
                }
            }
        }

        saveLearningPool(currentPool)
    }

    /**
     * 获取学习池中的单词ID列表
     */
    fun getLearningPool(): List<Int> {
        val json = sharedPreferences?.getString(KEY_LEARNING_POOL, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * 保存学习池
     */
    private fun saveLearningPool(pool: List<Int>) {
        val json = gson.toJson(pool)
        sharedPreferences?.edit()?.putString(KEY_LEARNING_POOL, json)?.apply()
    }

    /**
     * 获取下一个要补充的单词ID
     */
    private fun getNextWordId(): Int {
        return sharedPreferences?.getInt(KEY_NEXT_WORD_ID, 1) ?: 1
    }

    /**
     * 设置下一个要补充的单词ID
     */
    private fun setNextWordId(id: Int) {
        sharedPreferences?.edit()?.putInt(KEY_NEXT_WORD_ID, id)?.apply()
    }

    /**
     * 从词典获取新单词
     */
    private fun getNewWords(count: Int): List<Int> {
        val newWords = mutableListOf<Int>()
        var nextId = getNextWordId()

        for (i in 0 until count) {
            if (nextId <= TOTAL_WORDS) {
                newWords.add(nextId)
                nextId++
            } else {
                break
            }
        }

        setNextWordId(nextId)
        return newWords
    }

    /**
     * 标记单词为已记住（从学习池中移除并补充新单词）
     */
    fun markWordAsMemorized(wordId: Int) {
        val poolSize = getPoolSize()

        // 如果是学习池模式
        if (poolSize > 0) {
            val pool = getLearningPool().toMutableList()

            // 从学习池中移除
            if (pool.remove(wordId)) {
                // 补充一个新单词
                val newWords = getNewWords(1)
                if (newWords.isNotEmpty()) {
                    pool.add(newWords[0])
                }

                saveLearningPool(pool)
            }
        }

        // 更新今日记住的单词数
        incrementTodayMemorizedCount()
    }

    // ========== 每日统计管理 ==========

    /**
     * 检查并重置每日统计（如果日期变了）
     */
    private fun checkAndResetDailyStats() {
        val today = LocalDate.now().toString()
        val savedDate = sharedPreferences?.getString(KEY_TODAY_DATE, "") ?: ""

        if (today != savedDate) {
            // 新的一天，重置每日统计
            sharedPreferences?.edit()?.apply {
                putString(KEY_TODAY_DATE, today)
                putInt(KEY_TODAY_MEMORIZED_COUNT, 0)
                apply()
            }
        }
    }

    /**
     * 获取今天记住的单词数
     */
    fun getTodayMemorizedCount(): Int {
        checkAndResetDailyStats()
        return sharedPreferences?.getInt(KEY_TODAY_MEMORIZED_COUNT, 0) ?: 0
    }

    /**
     * 增加今天记住的单词数
     */
    private fun incrementTodayMemorizedCount() {
        checkAndResetDailyStats()
        val current = getTodayMemorizedCount()
        sharedPreferences?.edit()?.putInt(KEY_TODAY_MEMORIZED_COUNT, current + 1)?.apply()
    }

    /**
     * 获取超额完成的单词数
     */
    fun getExtraWordsCount(): Int {
        val poolSize = getPoolSize()
        if (poolSize == 0) return 0 // 默认模式没有超额概念

        val memorized = getTodayMemorizedCount()
        return (memorized - poolSize).coerceAtLeast(0)
    }

    /**
     * 获取学习中的单词数（学习池中还没记住的）
     */
    fun getStudyingWordsCount(): Int {
        val poolSize = getPoolSize()
        if (poolSize == 0) {
            // 默认模式：返回词典总数减去已记住的
            val memorized = WordLearningManager.getMemorizedWordsCount()
            return (TOTAL_WORDS - memorized).coerceAtLeast(0)
        }

        // 学习池模式：返回学习池大小
        return getLearningPool().size
    }
}
