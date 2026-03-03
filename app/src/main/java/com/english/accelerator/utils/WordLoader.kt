package com.english.accelerator.utils

import android.content.Context
import com.english.accelerator.algorithm.WordPoolIndexer
import com.english.accelerator.data.Word
import com.english.accelerator.data.WordPoolStatistics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 单词加载器（加载层）
 *
 * 核心定位：
 * - 算法路由器：根据设置（poolSize）自动选择 DefaultPlanAlgorithm 或 DailyPlanAlgorithm
 * - 数据加载器：管理分块加载、缓存、预加载和内存优化
 * - UI 接口层：为 UI 层提供简化的 API，隔离底层复杂性
 *
 * 职责：
 * - 加载索引（通过 WordPoolIndexer）
 * - 管理分块加载
 * - 内存管理和缓存
 * - 预加载优化
 * - 作为 UI 层和索引层之间的桥梁
 * - 提供简化的 API 供 UI 层调用
 *
 * 算法路由逻辑：
 * - poolSize = 0 → 启用 DefaultPlanAlgorithm（默认模式，顺序推送所有单词）
 * - poolSize > 0 → 启用 DailyPlanAlgorithm（每日计划模式，固定大小学习池）
 *
 * 架构层次：
 * data (数据层) → algorithm (算法层) → indexer (索引层) → WordLoader (加载层) → UI (界面层)
 */
object WordLoader {
    private lateinit var wordPoolIndexer: WordPoolIndexer
    private var isInitialized = false

    // 缓存管理
    private var cachedBatch: List<Word>? = null
    private var cacheTimestamp: Long = 0
    private const val CACHE_VALIDITY_MS = 5000L // 缓存有效期 5 秒

    // 预加载管理
    private var isPreloading = false
    private var preloadedBatch: List<Word>? = null

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
     * 获取下一批单词（分块加载，带缓存）
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

        // 检查预加载的数据
        preloadedBatch?.let { preloaded ->
            preloadedBatch = null // 清空预加载缓存
            triggerPreload(count, includeReview) // 触发下一次预加载
            return preloaded
        }

        // 检查缓存
        val now = System.currentTimeMillis()
        if (cachedBatch != null && (now - cacheTimestamp) < CACHE_VALIDITY_MS) {
            return cachedBatch!!
        }

        // 从索引器加载
        val batch = wordPoolIndexer.getNextBatch(count, includeReview)

        // 更新缓存
        cachedBatch = batch
        cacheTimestamp = now

        // 触发预加载
        triggerPreload(count, includeReview)

        return batch
    }

    /**
     * 触发预加载（异步）
     */
    private fun triggerPreload(count: Int, includeReview: Boolean) {
        if (isPreloading) return

        isPreloading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 预加载下一批数据
                preloadedBatch = wordPoolIndexer.getNextBatch(count, includeReview)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isPreloading = false
            }
        }
    }

    /**
     * 标记单词为已记住
     *
     * @param wordId 单词ID
     */
    fun markAsMemorized(wordId: Int) {
        checkInitialized()
        wordPoolIndexer.markAsMemorized(wordId)
        invalidateCache() // 标记后使缓存失效
    }

    /**
     * 标记单词为未记住
     *
     * @param wordId 单词ID
     */
    fun markAsUnmemorized(wordId: Int) {
        checkInitialized()
        wordPoolIndexer.markAsUnmemorized(wordId)
        invalidateCache() // 标记后使缓存失效
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
        invalidateCache() // 设置后使缓存失效
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
    fun getStatistics(): WordPoolStatistics {
        checkInitialized()
        return wordPoolIndexer.getStatistics()
    }

    /**
     * 使缓存失效
     */
    private fun invalidateCache() {
        cachedBatch = null
        preloadedBatch = null
        cacheTimestamp = 0
    }

    /**
     * 清空所有缓存
     */
    fun clearCache() {
        invalidateCache()
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
