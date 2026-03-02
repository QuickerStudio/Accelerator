package com.english.accelerator.data

import java.util.concurrent.ConcurrentHashMap

// 单词学习记录
data class WordLearningRecord(
    val wordId: Int,
    val word: String,
    val isMemorized: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

object WordLearningManager {
    // 使用 ConcurrentHashMap 存储单词学习记录，key 为单词 ID
    private val learningRecords = ConcurrentHashMap<Int, WordLearningRecord>()

    /**
     * 记录单词学习状态
     */
    fun recordWord(wordId: Int, word: String, isMemorized: Boolean) {
        learningRecords[wordId] = WordLearningRecord(
            wordId = wordId,
            word = word,
            isMemorized = isMemorized,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * 获取所有学习记录，按时间倒序排列
     */
    fun getAllRecords(): List<WordLearningRecord> {
        return learningRecords.values.sortedByDescending { it.timestamp }
    }

    /**
     * 获取今天的学习记录
     */
    fun getTodayRecords(): List<WordLearningRecord> {
        val todayStart = System.currentTimeMillis() - 24 * 60 * 60 * 1000 // 24小时前
        return learningRecords.values
            .filter { it.timestamp >= todayStart }
            .sortedByDescending { it.timestamp }
    }

    /**
     * 获取本周的学习记录
     */
    fun getThisWeekRecords(): List<WordLearningRecord> {
        val weekStart = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000 // 7天前
        val todayStart = System.currentTimeMillis() - 24 * 60 * 60 * 1000 // 24小时前
        return learningRecords.values
            .filter { it.timestamp in weekStart..<todayStart }
            .sortedByDescending { it.timestamp }
    }

    /**
     * 获取更早的学习记录
     */
    fun getEarlierRecords(): List<WordLearningRecord> {
        val weekStart = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000 // 7天前
        return learningRecords.values
            .filter { it.timestamp < weekStart }
            .sortedByDescending { it.timestamp }
    }

    /**
     * 获取重点单词（未记住的单词）
     */
    fun getImportantWords(): List<WordLearningRecord> {
        return learningRecords.values
            .filter { !it.isMemorized }
            .sortedByDescending { it.timestamp }
    }

    /**
     * 清空所有学习记录
     */
    fun clearAll() {
        learningRecords.clear()
    }
}
