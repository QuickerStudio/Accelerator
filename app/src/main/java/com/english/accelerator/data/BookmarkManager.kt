package com.english.accelerator.data

import androidx.compose.runtime.mutableStateListOf

// 收藏单词管理
object BookmarkManager {
    private val bookmarkedWords = mutableStateListOf<Word>()

    fun addBookmark(word: Word) {
        if (!bookmarkedWords.contains(word)) {
            bookmarkedWords.add(word)
        }
    }

    fun removeBookmark(word: Word) {
        bookmarkedWords.remove(word)
    }

    fun isBookmarked(word: Word): Boolean {
        return bookmarkedWords.contains(word)
    }

    fun getBookmarkedWords(): List<Word> {
        return bookmarkedWords.toList()
    }
}
