/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
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
