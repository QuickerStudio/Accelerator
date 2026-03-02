package com.english.accelerator.data

/**
 * 作文数据类
 */
data class Essay(
    val id: Long,
    val title: String,
    val content: String,
    val grammarScore: Int = 0,
    val wordCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
