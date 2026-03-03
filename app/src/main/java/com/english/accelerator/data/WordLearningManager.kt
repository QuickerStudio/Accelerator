package com.english.accelerator.data

import android.content.Context
import android.content.SharedPreferences
import com.english.accelerator.utils.AutoBackupManager
import com.english.accelerator.utils.DataStateTracker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.ConcurrentHashMap

// 单词学习记录
data class WordLearningRecord(
    val wordId: Int,
    val word: String,
    val isMemorized: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isImportant: Boolean = false  // 是否标记为重点单词
)

object WordLearningManager {
    // 使用 ConcurrentHashMap 存储单词学习记录，key 为单词 ID
    private val learningRecords = ConcurrentHashMap<Int, WordLearningRecord>()
    private var sharedPreferences: SharedPreferences? = null
    private var context: Context? = null
    private val gson = Gson()
    private const val PREFS_NAME = "word_learning_prefs"
    private const val KEY_RECORDS = "learning_records"

    /**
     * 初始化，从 SharedPreferences 加载数据
     */
    fun init(context: Context) {
        this.context = context
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFromPreferences()
    }

    /**
     * 从 SharedPreferences 加载数据
     */
    private fun loadFromPreferences() {
        val json = sharedPreferences?.getString(KEY_RECORDS, null) ?: return
        try {
            val type = object : TypeToken<Map<Int, WordLearningRecord>>() {}.type
            val records: Map<Int, WordLearningRecord> = gson.fromJson(json, type)
            learningRecords.clear()
            learningRecords.putAll(records)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 保存数据到 SharedPreferences
     */
    private fun saveToPreferences() {
        val json = gson.toJson(learningRecords.toMap())
        sharedPreferences?.edit()?.putString(KEY_RECORDS, json)?.commit()

        // 更新状态追踪器
        context?.let {
            DataStateTracker.updateState(DataStateTracker.DataType.WORD_LEARNING)
            AutoBackupManager.autoBackup(it)
        }
    }

    /**
     * 记录单词学习状态
     */
    fun recordWord(wordId: Int, word: String, isMemorized: Boolean) {
        val existingRecord = learningRecords[wordId]
        learningRecords[wordId] = WordLearningRecord(
            wordId = wordId,
            word = word,
            isMemorized = isMemorized,
            timestamp = System.currentTimeMillis(),
            isImportant = existingRecord?.isImportant ?: false  // 保留重点标记
        )
        saveToPreferences()
    }

    /**
     * 切换单词的重点标记
     */
    fun toggleImportant(wordId: Int) {
        val record = learningRecords[wordId]
        if (record != null) {
            learningRecords[wordId] = record.copy(isImportant = !record.isImportant)
            saveToPreferences()
        }
    }

    /**
     * 检查单词是否被标记为重点
     */
    fun isImportant(wordId: Int): Boolean {
        return learningRecords[wordId]?.isImportant ?: false
    }

    /**
     * 获取所有学习记录，按时间倒序排列
     */
    fun getAllRecords(): List<WordLearningRecord> {
        return learningRecords.values.sortedByDescending { it.timestamp }
    }

    /**
     * 获取今天的学习记录（最近24小时）
     */
    fun getTodayRecords(): List<WordLearningRecord> {
        val todayStart = System.currentTimeMillis() - 24 * 60 * 60 * 1000 // 24小时前
        return learningRecords.values
            .filter { it.timestamp >= todayStart }
            .sortedByDescending { it.timestamp }
    }

    /**
     * 获取本周的学习记录（1-7天前）
     */
    fun getThisWeekRecords(): List<WordLearningRecord> {
        val weekStart = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000 // 7天前
        val todayStart = System.currentTimeMillis() - 24 * 60 * 60 * 1000 // 24小时前
        return learningRecords.values
            .filter { it.timestamp in weekStart..<todayStart }
            .sortedByDescending { it.timestamp }
    }

    /**
     * 获取更早的学习记录（7天前）
     */
    fun getEarlierRecords(): List<WordLearningRecord> {
        val weekStart = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000 // 7天前
        return learningRecords.values
            .filter { it.timestamp < weekStart }
            .sortedByDescending { it.timestamp }
    }

    /**
     * 获取重点单词（手动标记的单词）
     */
    fun getImportantWords(): List<WordLearningRecord> {
        return learningRecords.values
            .filter { it.isImportant }
            .sortedByDescending { it.timestamp }
    }

    /**
     * 获取已记住的单词数量
     */
    fun getMemorizedWordsCount(): Int {
        return learningRecords.values.count { it.isMemorized }
    }

    /**
     * 清空所有学习记录
     */
    fun clearAll() {
        learningRecords.clear()
        saveToPreferences()
    }
}
