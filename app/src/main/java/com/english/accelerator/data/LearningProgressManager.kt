package com.english.accelerator.data

import android.content.Context
import android.content.SharedPreferences

/**
 * 学习进度管理器
 * 负责保存和恢复用户的学习进度（当前页和当前索引）
 */
object LearningProgressManager {
    private var sharedPreferences: SharedPreferences? = null
    private const val PREFS_NAME = "learning_progress_prefs"
    private const val KEY_CURRENT_PAGE_INDEX = "current_page_index"
    private const val KEY_CURRENT_INDEX_IN_PAGE = "current_index_in_page"

    /**
     * 初始化
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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
}
