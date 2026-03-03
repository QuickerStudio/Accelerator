package com.english.accelerator.utils

import android.content.Context
import com.english.accelerator.algorithm.WordPoolIndexer
import com.english.accelerator.data.Word

/**
 * 单词加载器 - 核心逻辑和界面的中间件
 *
 * 职责：
 * - 加载索引（通过 WordPoolIndexer）
 * - 管理分块加载
 * - 内存加载管理
 * - 作为 UI 层和算法层之间的桥梁
 * - 提供简化的 API 供 UI 层调用
 *
 * 架构层次：
 * data (数据层) → algorithm (算法层) → WordLoader (中间件层) → UI (界面层)
 */
object WordLoader {
    private lateinit var wordPoolIndexer: WordPoolIndexer
    private var isInitialized = false

    /**
     * 初始化
     */
    fun init(context: Context) {
        if (!isInitialized) {
            wordPoolIndexer = WordPoolIndexer.getInstance()
            wordPoolIndexer.init(context)
            isInitialized = true
        }
    }

    /**
     * 获取下一批单词（分块加载）
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
        return wordPoolIndexer.getNextBatch(count, includeReview)
    }

    /**
     * 标记单词为已记住
     *
     * @param wordId 单词ID
     */
    fun markAsMemorized(wordId: Int) {
        checkInitialized()
        wordPoolIndexer.markAsMemorized(wordId)
    }

    /**
     * 标记单词为未记住
     *
     * @param wordId 单词ID
     */
    fun markAsUnmemorized(wordId: Int) {
        checkInitialized()
        wordPoolIndexer.markAsUnmemorized(wordId)
    }

    /**
     * 检查是否还有更多单词
     *
     * @return 是否还有更多单词
     */
    fun hasMoreWords(): Boolean {
        checkInitialized()
        return wordPoolIndexer.hasMoreWords()
    }

    /**
     * 设置学习池大小
     *
     * @param size 学习池大小（0表示默认模式，>0表示每日计划模式）
     */
    fun setPoolSize(size: Int) {
        checkInitialized()
        wordPoolIndexer.setPoolSize(size)
    }

    /**
     * 获取当前学习池大小
     *
     * @return 学习池大小
     */
    fun getPoolSize(): Int {
        checkInitialized()
        return wordPoolIndexer.getPoolSize()
    }

    /**
     * 获取统计信息
     *
     * @return 单词池统计信息
     */
    fun getStatistics(): com.english.accelerator.data.WordPoolStatistics {
        checkInitialized()
        return wordPoolIndexer.getStatistics()
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
