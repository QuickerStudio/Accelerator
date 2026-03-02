package com.english.accelerator.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

/**
 * JSON 单词加载器
 * 从 res/raw/ecdict_words.json 加载单词数据
 */
object JsonWordLoader {
    private const val TAG = "JsonWordLoader"
    private var allWords: List<Word>? = null

    /**
     * 加载所有单词
     */
    fun loadWords(context: Context): List<Word> {
        if (allWords != null) {
            Log.d(TAG, "loadWords: 使用缓存数据，共 ${allWords!!.size} 个单词")
            return allWords!!
        }

        Log.d(TAG, "loadWords: 开始从 JSON 加载单词数据")
        val startTime = System.currentTimeMillis()

        try {
            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier(
                    "ecdict_words",
                    "raw",
                    context.packageName
                )
            )

            val reader = InputStreamReader(inputStream, Charsets.UTF_8)
            val gson = Gson()
            val type = object : TypeToken<List<Word>>() {}.type
            allWords = gson.fromJson(reader, type)
            reader.close()

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "loadWords: 加载完成，耗时 ${duration}ms，共 ${allWords!!.size} 个单词")

            return allWords!!
        } catch (e: Exception) {
            Log.e(TAG, "loadWords: 加载失败", e)
            return emptyList()
        }
    }

    /**
     * 根据 ID 获取单词
     */
    fun getWordById(context: Context, id: Int): Word? {
        val words = loadWords(context)
        return words.find { it.id == id }
    }

    /**
     * 获取指定范围的单词
     */
    fun getWordsInRange(context: Context, startId: Int, endId: Int): List<Word> {
        val words = loadWords(context)
        return words.filter { it.id in startId..endId }
    }

    /**
     * 清空缓存
     */
    fun clearCache() {
        Log.d(TAG, "clearCache: 清空缓存")
        allWords = null
    }
}
