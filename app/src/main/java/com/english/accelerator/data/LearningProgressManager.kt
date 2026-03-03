package com.english.accelerator.data

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate

/**
 * 学习进度管理器
 * 负责保存和恢复用户的学习进度（当前页和当前索引）
 *
 * 单词推送逻辑：
 * - 每日学习单词数：用户设置的每日目标（默认100个）
 * - 学习中：当天推送但还没标记为"已记住"的单词数
 * - 已记住：已经掌握的单词总数
 * - 超额完成：超过每日目标的单词数
 * - 复习次数：复习旧单词的次数
 */
object LearningProgressManager {
    private var sharedPreferences: SharedPreferences? = null
    private const val PREFS_NAME = "learning_progress_prefs"
    private const val KEY_CURRENT_PAGE_INDEX = "current_page_index"
    private const val KEY_CURRENT_INDEX_IN_PAGE = "current_index_in_page"

    // 新增：每日学习单词数设置
    private const val KEY_DAILY_WORD_GOAL = "daily_word_goal"
    private const val DEFAULT_DAILY_WORD_GOAL = 100

    // 新增：当天学习进度
    private const val KEY_TODAY_DATE = "today_date"
    private const val KEY_TODAY_STUDIED_COUNT = "today_studied_count"
    private const val KEY_TODAY_START_ID = "today_start_id"

    /**
     * 初始化
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        checkAndResetDailyProgress()
    }

    /**
     * 保存学习进度
     */
    fun saveProgress(pageIndex: Int, indexInPage: Int) {
        sharedPreferences?.edit()?.apply {
            putInt(KEY_CURRENT_PAGE_INDEX, pageIndex)
            putInt(KEY_CURRENT_INDEX_IN_PAGE, indexInPage)
            apply()
        }
    }

    /**
     * 获取当前页索引
     */
    fun getCurrentPageIndex(): Int {
        return sharedPreferences?.getInt(KEY_CURRENT_PAGE_INDEX, 0) ?: 0
    }

    /**
     * 获取当前页内索引
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

    // ========== 新增：每日学习单词数管理 ==========

    /**
     * 检查并重置每日进度（如果日期变了）
     */
    private fun checkAndResetDailyProgress() {
        val today = LocalDate.now().toString()
        val savedDate = sharedPreferences?.getString(KEY_TODAY_DATE, "") ?: ""

        if (today != savedDate) {
            // 新的一天，重置每日进度
            sharedPreferences?.edit()?.apply {
                putString(KEY_TODAY_DATE, today)
                putInt(KEY_TODAY_STUDIED_COUNT, 0)
                // 计算今天的起始ID
                val lastStartId = getInt(KEY_TODAY_START_ID, 1)
                val dailyGoal = getDailyWordGoal()
                putInt(KEY_TODAY_START_ID, lastStartId + dailyGoal)
                apply()
            }
        }
    }

    /**
     * 设置每日学习单词数
     */
    fun setDailyWordGoal(count: Int) {
        sharedPreferences?.edit()?.putInt(KEY_DAILY_WORD_GOAL, count)?.apply()
    }

    /**
     * 获取每日学习单词数
     */
    fun getDailyWordGoal(): Int {
        return sharedPreferences?.getInt(KEY_DAILY_WORD_GOAL, DEFAULT_DAILY_WORD_GOAL) ?: DEFAULT_DAILY_WORD_GOAL
    }

    /**
     * 获取今天的起始单词ID
     */
    fun getTodayStartWordId(): Int {
        checkAndResetDailyProgress()
        return sharedPreferences?.getInt(KEY_TODAY_START_ID, 1) ?: 1
    }

    /**
     * 获取今天已学习的单词数
     */
    fun getTodayStudiedCount(): Int {
        checkAndResetDailyProgress()
        return sharedPreferences?.getInt(KEY_TODAY_STUDIED_COUNT, 0) ?: 0
    }

    /**
     * 增加今天已学习的单词数
     */
    fun incrementTodayStudiedCount() {
        checkAndResetDailyProgress()
        val current = getTodayStudiedCount()
        sharedPreferences?.edit()?.putInt(KEY_TODAY_STUDIED_COUNT, current + 1)?.apply()
    }

    /**
     * 获取今天应该推送的单词ID范围
     * @return Pair(startId, endId)
     */
    fun getTodayWordRange(): Pair<Int, Int> {
        val startId = getTodayStartWordId()
        val goal = getDailyWordGoal()
        val endId = (startId + goal - 1).coerceAtMost(4998) // 词典最大ID是4998
        return Pair(startId, endId)
    }

    /**
     * 获取超额完成的单词数
     */
    fun getExtraWordsCount(): Int {
        val studied = getTodayStudiedCount()
        val goal = getDailyWordGoal()
        return (studied - goal).coerceAtLeast(0)
    }

    /**
     * 获取学习中的单词数（推送了但还没记住的）
     */
    fun getStudyingWordsCount(): Int {
        val studied = getTodayStudiedCount()
        val goal = getDailyWordGoal()
        // 学习中 = min(已学习数, 目标数)
        return studied.coerceAtMost(goal)
    }
}
