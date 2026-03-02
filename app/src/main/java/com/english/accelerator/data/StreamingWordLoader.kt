package com.english.accelerator.data

import android.util.LruCache

/**
 * 流式单词加载器
 * 像视频播放一样，只加载当前需要的单词片段
 */
object StreamingWordLoader {
    // 配置
    private const val PAGE_SIZE = 50  // 每页 50 个单词
    private const val PRELOAD_THRESHOLD = 40  // 滑到第 40 个时预加载下一页
    private const val CACHE_SIZE = 2  // 最多缓存 2 页（当前页 + 预加载页）

    // 轻量级缓存：只缓存 2 页数据
    private val pageCache = LruCache<Int, List<Word>>(CACHE_SIZE)

    /**
     * 获取指定页的单词
     * @param pageIndex 页码（从 0 开始）
     */
    fun getPage(pageIndex: Int): List<Word> {
        // 先查缓存
        pageCache.get(pageIndex)?.let { return it }

        // 缓存未命中，从分块中加载
        val startId = pageIndex * PAGE_SIZE + 1
        val endId = minOf(startId + PAGE_SIZE - 1, 5000)

        val words = mutableListOf<Word>()
        for (id in startId..endId) {
            getWordFromChunk(id)?.let { words.add(it) }
        }

        // 放入缓存
        pageCache.put(pageIndex, words)
        return words
    }

    /**
     * 从分块中获取单个单词
     */
    private fun getWordFromChunk(wordId: Int): Word? {
        if (wordId < 1 || wordId > 5000) return null

        val chunkIndex = (wordId - 1) / 500
        val chunk = when (chunkIndex) {
            0 -> ecdictWordsChunk0
            1 -> ecdictWordsChunk1
            2 -> ecdictWordsChunk2
            3 -> ecdictWordsChunk3
            4 -> ecdictWordsChunk4
            5 -> ecdictWordsChunk5
            6 -> ecdictWordsChunk6
            7 -> ecdictWordsChunk7
            8 -> ecdictWordsChunk8
            9 -> ecdictWordsChunk9
            else -> return null
        }

        val indexInChunk = (wordId - 1) % 500
        return chunk.getOrNull(indexInChunk)
    }

    /**
     * 预加载下一页
     */
    fun preloadNextPage(currentPageIndex: Int) {
        val nextPageIndex = currentPageIndex + 1
        if (nextPageIndex * PAGE_SIZE < 5000) {
            // 异步预加载，不阻塞当前线程
            Thread {
                getPage(nextPageIndex)
            }.start()
        }
    }

    /**
     * 清空缓存
     */
    fun clearCache() {
        pageCache.evictAll()
    }

    /**
     * 获取总页数
     */
    fun getTotalPages(): Int {
        return (5000 + PAGE_SIZE - 1) / PAGE_SIZE  // 向上取整
    }
}
