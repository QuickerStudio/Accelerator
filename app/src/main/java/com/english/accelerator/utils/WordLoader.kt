package com.english.accelerator.utils

import android.content.Context
import com.english.accelerator.algorithm.WordPoolManager
import com.english.accelerator.data.Word

/**
 * 单词加载器 - 核心逻辑和界面的中间件
 *
 * 职责：
 * - 作为 UI 层和算法层之间的桥梁
 * - 提供简化的 API 供 UI 层调用
 * - 管理单词加载的生命周期
 * - 处理单词状态的更新
 *
 * 设计原则：
 * - UI 层只需要知道如何获取单词和标记单词状态
 * - 算法层的复杂性对 UI 层透明
 * - 所有单词推送逻辑由算法层处理
 */
object WordLoader {
    private lateinit var wordPoolManager: WordPoolManager
    private var isInitialized = false

    /**
     * 初始化
     */
    fun init(context: Context) {
        if (!isInitialized) {
            wordPoolManager = WordPoolManager.getInstance()
            wordPoolManager.init(context)
            isInitialized = true
        }
    }

    /**
     * 获取下一批单词
     *
     * @param count 获取的单词数量（默认50个）
     * @param includeReview 是否包含复习单词（默认true）
     * @return 单词列表
     */
    fun getNextBatch(
        count: Int = 50,
        includeReview: Boolean = true
    ): List<Word> {
        checkInitialized()
        return wordPoolManager.getNextBatch(count, includeReview)
    }

    /**
     * 标记单词为已记住
     *
     * @param wordId 单词ID
     */
    fun markAsMemorized(wordId: Int) {
        checkInitialized()
        wordPoolManager.markAsMemorized(wordId)
    }

    /**
     * 标记单词为未记住
     *
     * @param wordId 单词ID
     */
    fun markAsUnmemorized(wordId: Int) {
        checkInitialized()
        wordPoolManager.markAsUnmemorized(wordId)
    }

    /**
     * 检查是否还有更多单词
     *
     * @return 是否还有更多单词
     */
    fun hasMoreWords(): Boolean {
        checkInitialized()
        return wordPoolManager.hasMoreWords()
    }

    /**
     * 设置学习池大小
     *
     * @param size 学习池大小（0表示默认模式，>0表示每日计划模式）
     */
    fun setPoolSize(size: Int) {
        checkInitialized()
        wordPoolManager.setPoolSize(size)
    }

    /**
     * 获取当前学习池大小
     *
     * @return 学习池大小
     */
    fun getPoolSize(): Int {
        checkInitialized()
        return wordPoolManager.getPoolSize()
    }

    /**
     * 获取统计信息
     *
     * @return 单词池统计信息
     */
    fun getStatistics(): com.english.accelerator.algorithm.types.WordPoolStatistics {
        checkInitialized()
        return wordPoolManager.getStatistics()
    }

    /**
     * 检查是否已初始化
     */
    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("WordLoader has not been initialized. Call init() first.")
        }
    }
}
