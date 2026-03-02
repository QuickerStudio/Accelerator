package com.english.accelerator.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.ConcurrentHashMap

/**
 * 词库管理类
 * 负责加载、管理和提供单词数据
 */
object WordRepository {
    private val allWords = ConcurrentHashMap<Int, Word>()
    private val gson = Gson()
    private var isInitialized = false
    private lateinit var appContext: Context

    /**
     * 初始化词库
     */
    fun init(context: Context) {
        if (isInitialized) {
            return
        }

        appContext = context.applicationContext
        loadBuiltInWords()
        isInitialized = true
    }

    /**
     * 加载内置词库数据
     */
    private fun loadBuiltInWords() {
        val words = JsonWordLoader.loadWords(appContext)
        words.forEach { word ->
            allWords[word.id] = word
        }
    }

    /**
     * 获取所有单词
     */
    fun getAllWords(): List<Word> {
        return allWords.values.sortedBy { it.id }
    }

    /**
     * 根据 ID 获取单词
     */
    fun getWordById(id: Int): Word? {
        return allWords[id]
    }

    /**
     * 根据等级获取单词
     */
    fun getWordsByLevel(level: String): List<Word> {
        return allWords.values.filter { it.level == level }.sortedByDescending { it.frequency }
    }

    /**
     * 获取高频词汇（按词频排序）
     */
    fun getHighFrequencyWords(limit: Int = 100): List<Word> {
        return allWords.values.sortedByDescending { it.frequency }.take(limit)
    }

    /**
     * 随机获取单词
     */
    fun getRandomWords(count: Int): List<Word> {
        return allWords.values.shuffled().take(count)
    }

    /**
     * 搜索单词
     */
    fun searchWords(query: String): List<Word> {
        val lowerQuery = query.lowercase()
        return allWords.values.filter {
            it.word.lowercase().contains(lowerQuery) ||
            it.translation.contains(query)
        }.sortedByDescending { it.frequency }
    }

    /**
     * 获取词库统计信息
     */
    fun getStatistics(): WordStatistics {
        val words = allWords.values
        return WordStatistics(
            totalWords = words.size,
            cet4Words = words.count { it.level == "CET4" },
            cet6Words = words.count { it.level == "CET6" },
            toeflWords = words.count { it.level == "TOEFL" },
            ieltsWords = words.count { it.level == "IELTS" },
            greWords = words.count { it.level == "GRE" }
        )
    }
}

/**
 * 词库统计信息
 */
data class WordStatistics(
    val totalWords: Int,
    val cet4Words: Int,
    val cet6Words: Int,
    val toeflWords: Int,
    val ieltsWords: Int,
    val greWords: Int
)
