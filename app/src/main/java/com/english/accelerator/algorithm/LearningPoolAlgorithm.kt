package com.english.accelerator.algorithm

import com.english.accelerator.data.LearningProgressData

/**
 * 学习池算法（算法层）
 *
 * 职责：
 * - 学习池初始化算法
 * - 学习池调整算法
 * - 单词补充算法
 * - 已记住单词处理算法
 * - 不直接访问 SharedPreferences，通过 LearningProgressData 访问数据
 */
object LearningPoolAlgorithm {

    /**
     * 设置学习池大小（0表示默认模式）
     */
    fun setPoolSize(size: Int) {
        val currentSize = LearningProgressData.getPoolSize()
        LearningProgressData.setPoolSize(size)

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
     * 初始化学习池
     */
    private fun initializeLearningPool(size: Int) {
        val pool = mutableListOf<Int>()
        val startId = 1

        // 填充学习池
        for (i in 0 until size.coerceAtMost(LearningProgressData.TOTAL_WORDS)) {
            pool.add(startId + i)
        }

        LearningProgressData.saveLearningPool(pool)
        LearningProgressData.setNextWordId(startId + size)
    }

    /**
     * 调整学习池大小
     */
    private fun adjustLearningPool(newSize: Int) {
        val currentPool = LearningProgressData.getLearningPool().toMutableList()

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

        LearningProgressData.saveLearningPool(currentPool)
    }

    /**
     * 从词典获取新单词
     */
    private fun getNewWords(count: Int): List<Int> {
        val newWords = mutableListOf<Int>()
        var nextId = LearningProgressData.getNextWordId()

        for (i in 0 until count) {
            if (nextId <= LearningProgressData.TOTAL_WORDS) {
                newWords.add(nextId)
                nextId++
            } else {
                break
            }
        }

        LearningProgressData.setNextWordId(nextId)
        return newWords
    }

    /**
     * 标记单词为已记住（从学习池中移除并补充新单词）
     */
    fun markWordAsMemorized(wordId: Int) {
        val poolSize = LearningProgressData.getPoolSize()

        // 如果是学习池模式
        if (poolSize > 0) {
            val pool = LearningProgressData.getLearningPool().toMutableList()

            // 从学习池中移除
            if (pool.remove(wordId)) {
                // 补充一个新单词
                val newWords = getNewWords(1)
                if (newWords.isNotEmpty()) {
                    pool.add(newWords[0])
                }

                LearningProgressData.saveLearningPool(pool)
            }
        }

        // 更新今日记住的单词数
        LearningProgressData.incrementTodayMemorizedCount()
    }

    /**
     * 获取超额完成的单词数
     */
    fun getExtraWordsCount(): Int {
        val poolSize = LearningProgressData.getPoolSize()
        if (poolSize == 0) return 0 // 默认模式没有超额概念

        val memorized = LearningProgressData.getTodayMemorizedCount()
        return (memorized - poolSize).coerceAtLeast(0)
    }

    /**
     * 获取学习中的单词数（学习池中还没记住的）
     */
    fun getStudyingWordsCount(): Int {
        val poolSize = LearningProgressData.getPoolSize()
        if (poolSize == 0) {
            // 默认模式：返回词典总数减去已记住的
            val memorized = com.english.accelerator.data.WordLearningManager.getMemorizedWordsCount()
            return (LearningProgressData.TOTAL_WORDS - memorized).coerceAtLeast(0)
        }

        // 学习池模式：返回学习池大小
        return LearningProgressData.getLearningPool().size
    }
}
