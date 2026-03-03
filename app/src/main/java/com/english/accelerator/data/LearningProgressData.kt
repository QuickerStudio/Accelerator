package com.english.accelerator.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

/**
 * 学习进度数据管理器（数据层）
 *
 * 职责：
 * - 纯数据存储和访问
 * - SharedPreferences 操作
 * - 数据序列化/反序列化
 * - 不包含任何算法逻辑
 */
object LearningProgressData {
    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()

    private const val PREFS_NAME = "learning_progress_prefs"
    private const val KEY_CURRENT_PAGE_INDEX = "current_page_index"
    private const val KEY_CURRENT_INDEX_IN_PAGE = "current_index_in_page"
    private const val KEY_POOL_SIZE = "pool_size"
    private const val KEY_LEARNING_POOL = "learning_pool"
    private const val KEY_NEXT_WORD_ID = "next_word_id"
    private const val KEY_TODAY_DATE = "today_date"
    private const val KEY_TODAY_MEMORIZED_COUNT = "today_memorized_count"

    const val TOTAL_WORDS = 4998

    /**
     * 初始化
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        checkAndResetDailyStats()
    }

    // ========== 旧版进度数据 ==========

    fun saveProgress(pageIndex: Int, indexInPage: Int) {
        sharedPreferences?.edit()?.apply {
            putInt(KEY_CURRENT_PAGE_INDEX, pageIndex)
            putInt(KEY_CURRENT_INDEX_IN_PAGE, indexInPage)
            commit()
        }
    }

    fun getCurrentPageIndex(): Int {
        return sharedPreferences?.getInt(KEY_CURRENT_PAGE_INDEX, 0) ?: 0
    }

    fun getCurrentIndexInPage(): Int {
        return sharedPreferences?.getInt(KEY_CURRENT_INDEX_IN_PAGE, 0) ?: 0
    }

    fun resetProgress() {
        sharedPreferences?.edit()?.apply {
            putInt(KEY_CURRENT_PAGE_INDEX, 0)
            putInt(KEY_CURRENT_INDEX_IN_PAGE, 0)
            commit()
        }
    }

    // ========== 学习池数据 ==========

    fun setPoolSize(size: Int) {
        sharedPreferences?.edit()?.putInt(KEY_POOL_SIZE, size)?.commit()
    }

    fun getPoolSize(): Int {
        return sharedPreferences?.getInt(KEY_POOL_SIZE, 0) ?: 0
    }

    fun getLearningPool(): List<Int> {
        val json = sharedPreferences?.getString(KEY_LEARNING_POOL, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveLearningPool(pool: List<Int>) {
        val json = gson.toJson(pool)
        sharedPreferences?.edit()?.putString(KEY_LEARNING_POOL, json)?.commit()
    }

    fun getNextWordId(): Int {
        return sharedPreferences?.getInt(KEY_NEXT_WORD_ID, 1) ?: 1
    }

    fun setNextWordId(id: Int) {
        sharedPreferences?.edit()?.putInt(KEY_NEXT_WORD_ID, id)?.commit()
    }

    // ========== 每日统计数据 ==========

    private fun checkAndResetDailyStats() {
        val today = LocalDate.now().toString()
        val savedDate = sharedPreferences?.getString(KEY_TODAY_DATE, "") ?: ""

        if (today != savedDate) {
            sharedPreferences?.edit()?.apply {
                putString(KEY_TODAY_DATE, today)
                putInt(KEY_TODAY_MEMORIZED_COUNT, 0)
                commit()
            }
        }
    }

    fun getTodayMemorizedCount(): Int {
        checkAndResetDailyStats()
        return sharedPreferences?.getInt(KEY_TODAY_MEMORIZED_COUNT, 0) ?: 0
    }

    fun incrementTodayMemorizedCount() {
        checkAndResetDailyStats()
        val current = getTodayMemorizedCount()
        sharedPreferences?.edit()?.putInt(KEY_TODAY_MEMORIZED_COUNT, current + 1)?.commit()
    }
}
