package com.english.accelerator.algorithm.types

/**
 * 单词池类型枚举
 */
enum class WordPoolType {
    /**
     * 默认计划池：包含所有剩余单词（从词典5000个统计）
     * 触发条件：每日学习单词数设置为 0
     */
    DEFAULT_PLAN,

    /**
     * 每日计划池：固定大小的学习池（如100个）
     * 触发条件：每日学习单词数设置 > 0
     */
    DAILY_PLAN
}

/**
 * 单词池统计信息
 */
data class WordPoolStatistics(
    /**
     * 已记住的单词总数（累计）
     */
    val memorizedCount: Int,

    /**
     * 未记住的单词数（学习过但还没掌握）
     */
    val unmemorizedCount: Int,

    /**
     * 学习中的单词数（当前学习池中的单词数）
     */
    val studyingCount: Int,

    /**
     * 剩余单词数（词典中还没学习的单词数）
     */
    val remainingCount: Int,

    /**
     * 复习次数
     */
    val reviewCount: Int,

    /**
     * 当前使用的单词池类型
     */
    val poolType: WordPoolType
)
