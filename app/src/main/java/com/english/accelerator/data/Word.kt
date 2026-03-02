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
    ),
    Word(
        id = 6,
        word = "Pronunciation",
        phonetic = "/prəˌnʌnsiˈeɪʃn/",
        translation = "发音",
        example = "Clear pronunciation is important for communication."
    ),
    Word(
        id = 7,
        word = "Comprehension",
        phonetic = "/ˌkɑːmprɪˈhenʃn/",
        translation = "理解；领悟",
        example = "Reading comprehension requires focus."
    ),
    Word(
        id = 8,
        word = "Expression",
        phonetic = "/ɪkˈspreʃn/",
        translation = "表达；表情",
        example = "She has a natural gift for expression."
    ),
    Word(
        id = 9,
        word = "Idiom",
        phonetic = "/ˈɪdiəm/",
        translation = "习语；成语",
        example = "Learning idioms helps you sound more natural."
    ),
    Word(
        id = 10,
        word = "Dialect",
        phonetic = "/ˈdaɪəlekt/",
        translation = "方言；土话",
        example = "Different regions have different dialects."
    ),
    Word(
        id = 11,
        word = "Accent",
        phonetic = "/ˈæksent/",
        translation = "口音；重音",
        example = "She speaks with a British accent."
    ),
    Word(
        id = 12,
        word = "Bilingual",
        phonetic = "/baɪˈlɪŋɡwəl/",
        translation = "双语的",
        example = "Being bilingual opens many opportunities."
    ),
    Word(
        id = 13,
        word = "Translate",
        phonetic = "/trænsˈleɪt/",
        translation = "翻译；转化",
        example = "Can you translate this sentence for me?"
    ),
    Word(
        id = 14,
        word = "Interpret",
        phonetic = "/ɪnˈtɜːrprɪt/",
        translation = "解释；口译",
        example = "She works as an interpreter at conferences."
    ),
    Word(
        id = 15,
        word = "Articulate",
        phonetic = "/ɑːrˈtɪkjuleɪt/",
        translation = "清晰表达；发音清晰的",
        example = "He is very articulate in his presentations."
    ),
    Word(
        id = 16,
        word = "Eloquent",
        phonetic = "/ˈeləkwənt/",
        translation = "雄辩的；有说服力的",
        example = "The speaker gave an eloquent speech."
    ),
    Word(
        id = 17,
        word = "Proficient",
        phonetic = "/prəˈfɪʃnt/",
        translation = "精通的；熟练的",
        example = "She is proficient in three languages."
    ),
    Word(
        id = 18,
        word = "Literacy",
        phonetic = "/ˈlɪtərəsi/",
        translation = "读写能力；识字",
        example = "Digital literacy is essential today."
    ),
    Word(
        id = 19,
        word = "Syntax",
        phonetic = "/ˈsɪntæks/",
        translation = "句法；语法",
        example = "Understanding syntax helps with sentence structure."
    ),
    Word(
        id = 20,
        word = "Semantics",
        phonetic = "/sɪˈmæntɪks/",
        translation = "语义学",
        example = "Semantics deals with the meaning of words."
    )
)
