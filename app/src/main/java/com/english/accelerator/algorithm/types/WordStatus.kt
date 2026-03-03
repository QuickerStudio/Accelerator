package com.english.accelerator.algorithm.types

/**
 * 单词状态枚举
 */
enum class WordStatus {
    /**
     * 已记住：用户已经掌握的单词
     */
    MEMORIZED,

    /**
     * 未记住：用户学习过但还没掌握的单词
     */
    UNMEMORIZED,

    /**
     * 复习中：正在复习的单词
     */
    REVIEWING,

    /**
     * 未学习：还没有学习过的单词
     */
    NOT_STUDIED
}
