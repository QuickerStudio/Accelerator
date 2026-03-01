package com.english.accelerator.data

data class Word(
    val id: Int,
    val word: String,
    val phonetic: String,
    val translation: String,
    val example: String
)

// 假数据用于测试
val sampleWords = listOf(
    Word(
        id = 1,
        word = "Vocabulary",
        phonetic = "/vəˈkæbjələri/",
        translation = "词汇；词汇量",
        example = "Reading helps expand your vocabulary."
    ),
    Word(
        id = 2,
        word = "Accelerate",
        phonetic = "/əkˈseləreɪt/",
        translation = "加速；促进",
        example = "Technology can accelerate learning."
    ),
    Word(
        id = 3,
        word = "Practice",
        phonetic = "/ˈpræktɪs/",
        translation = "练习；实践",
        example = "Practice makes perfect."
    ),
    Word(
        id = 4,
        word = "Fluent",
        phonetic = "/ˈfluːənt/",
        translation = "流利的；流畅的",
        example = "She speaks fluent English."
    ),
    Word(
        id = 5,
        word = "Grammar",
        phonetic = "/ˈɡræmər/",
        translation = "语法",
        example = "Good grammar is essential for writing."
    )
)
