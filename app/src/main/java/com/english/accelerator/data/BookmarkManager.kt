package com.english.accelerator.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 收藏单词管理器
 * 负责管理用户收藏的单词，并持久化存储
 */
object BookmarkManager {
    private const val TAG = "BookmarkManager"
    private const val PREFS_NAME = "bookmark_prefs"
    private const val KEY_BOOKMARKS = "bookmarked_words"

    private val bookmarkedWords = mutableStateListOf<Word>()
    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()

    /**
     * 初始化
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFromPreferences()
    }

    /**
     * 从 SharedPreferences 加载收藏数据
     */
    private fun loadFromPreferences() {
        val json = sharedPreferences?.getString(KEY_BOOKMARKS, null) ?: return
        try {
            val type = object : TypeToken<List<Word>>() {}.type
            val words: List<Word> = gson.fromJson(json, type)
            bookmarkedWords.clear()
            bookmarkedWords.addAll(words)
            Log.d(TAG, "loadFromPreferences: 加载了 ${bookmarkedWords.size} 个收藏单词")
        } catch (e: Exception) {
            Log.e(TAG, "loadFromPreferences: 加载失败", e)
        }
    }

    /**
     * 保存数据到 SharedPreferences
     */
    private fun saveToPreferences() {
        val json = gson.toJson(bookmarkedWords.toList())
        sharedPreferences?.edit()?.putString(KEY_BOOKMARKS, json)?.apply()
        Log.d(TAG, "saveToPreferences: 保存了 ${bookmarkedWords.size} 个收藏单词")
    }

    /**
     * 添加收藏
     */
    fun addBookmark(word: Word) {
        if (!bookmarkedWords.any { it.id == word.id }) {
            bookmarkedWords.add(word)
            saveToPreferences()
            Log.d(TAG, "addBookmark: 收藏单词 ${word.word}")
        }
    }

    /**
     * 移除收藏
     */
    fun removeBookmark(word: Word) {
        bookmarkedWords.removeAll { it.id == word.id }
        saveToPreferences()
        Log.d(TAG, "removeBookmark: 取消收藏单词 ${word.word}")
    }

    /**
     * 检查是否已收藏
     */
    fun isBookmarked(word: Word): Boolean {
        return bookmarkedWords.any { it.id == word.id }
    }

    /**
     * 获取所有收藏的单词
     */
    fun getBookmarkedWords(): List<Word> {
        return bookmarkedWords.toList()
    }

    /**
     * 清空所有收藏
     */
    fun clearAll() {
        bookmarkedWords.clear()
        saveToPreferences()
        Log.d(TAG, "clearAll: 清空所有收藏")
    }
}
