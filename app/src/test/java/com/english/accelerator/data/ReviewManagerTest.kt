package com.english.accelerator.data

import android.content.Context
import android.content.SharedPreferences
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * ReviewManager 单元测试
 * 测试洗牌算法的正确性和边界条件
 */
class ReviewManagerTest {

    private lateinit var mockContext: Context
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        // Mock Android 依赖
        mockContext = mockk(relaxed = true)
        mockSharedPreferences = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)

        every { mockContext.getSharedPreferences(any(), any()) } returns mockSharedPreferences
        every { mockSharedPreferences.getInt(any(), any()) } returns 8 // 默认比例
        every { mockSharedPreferences.edit() } returns mockEditor
        every { mockEditor.putInt(any(), any()) } returns mockEditor
        every { mockEditor.apply() } just Runs

        // Mock WordLearningManager 和 WordRepository
        mockkObject(WordLearningManager)
        mockkObject(WordRepository)

        // 初始化 ReviewManager
        ReviewManager.init(mockContext)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `shuffleWithReviewWords - 空新单词列表返回空列表`() {
        // Given
        val newWords = emptyList<Word>()

        // When
        val result = ReviewManager.shuffleWithReviewWords(newWords)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `shuffleWithReviewWords - 没有未记住单词时返回原列表`() {
        // Given
        val newWords = listOf(
            createWord(1, "test1"),
            createWord(2, "test2"),
            createWord(3, "test3")
        )
        every { WordLearningManager.getAllRecords() } returns emptyList()

        // When
        val result = ReviewManager.shuffleWithReviewWords(newWords)

        // Then
        assertEquals(newWords.size, result.size)
        assertEquals(newWords, result)
    }

    @Test
    fun `shuffleWithReviewWords - 正确计算复习单词数量`() {
        // Given: 50 个新单词，比例 8:1，应该插入 6 个复习单词
        val newWords = (1..50).map { createWord(it, "word$it") }
        val unmemorizedRecords = (51..60).map {
            WordLearningRecord(it, "review$it", false, System.currentTimeMillis())
        }
        val unmemorizedWords = (51..60).map { createWord(it, "review$it") }

        every { WordLearningManager.getAllRecords() } returns unmemorizedRecords
        unmemorizedWords.forEach { word ->
            every { WordRepository.getWordById(word.id) } returns word
        }

        // When
        val result = ReviewManager.shuffleWithReviewWords(newWords)

        // Then
        val expectedCount = 50 + 6 // 50 新单词 + 6 复习单词
        assertEquals(expectedCount, result.size)
    }

    @Test
    fun `shuffleWithReviewWords - 复习单词数量不超过未记住单词总数`() {
        // Given: 50 个新单词，但只有 3 个未记住单词
        val newWords = (1..50).map { createWord(it, "word$it") }
        val unmemorizedRecords = (51..53).map {
            WordLearningRecord(it, "review$it", false, System.currentTimeMillis())
        }
        val unmemorizedWords = (51..53).map { createWord(it, "review$it") }

        every { WordLearningManager.getAllRecords() } returns unmemorizedRecords
        unmemorizedWords.forEach { word ->
            every { WordRepository.getWordById(word.id) } returns word
        }

        // When
        val result = ReviewManager.shuffleWithReviewWords(newWords)

        // Then
        val expectedCount = 50 + 3 // 50 新单词 + 3 复习单词（最多）
        assertEquals(expectedCount, result.size)
    }

    @Test
    fun `shuffleWithReviewWords - 复习单词被正确插入`() {
        // Given
        val newWords = (1..10).map { createWord(it, "word$it") }
        val unmemorizedRecords = listOf(
            WordLearningRecord(11, "review11", false, System.currentTimeMillis())
        )
        val reviewWord = createWord(11, "review11")

        every { WordLearningManager.getAllRecords() } returns unmemorizedRecords
        every { WordRepository.getWordById(11) } returns reviewWord

        // When
        val result = ReviewManager.shuffleWithReviewWords(newWords)

        // Then
        assertEquals(11, result.size) // 10 + 1
        assertTrue(result.contains(reviewWord))

        // 验证复习单词不在前 2 个位置
        val reviewWordIndex = result.indexOf(reviewWord)
        assertTrue(reviewWordIndex >= 2)
    }

    @Test
    fun `shuffleWithReviewWords - 所有新单词都保留在结果中`() {
        // Given
        val newWords = (1..20).map { createWord(it, "word$it") }
        val unmemorizedRecords = (21..25).map {
            WordLearningRecord(it, "review$it", false, System.currentTimeMillis())
        }
        val unmemorizedWords = (21..25).map { createWord(it, "review$it") }

        every { WordLearningManager.getAllRecords() } returns unmemorizedRecords
        unmemorizedWords.forEach { word ->
            every { WordRepository.getWordById(word.id) } returns word
        }

        // When
        val result = ReviewManager.shuffleWithReviewWords(newWords)

        // Then
        newWords.forEach { word ->
            assertTrue("新单词 ${word.word} 应该在结果中", result.contains(word))
        }
    }

    @Test
    fun `hasReviewWords - 有未记住单词时返回 true`() {
        // Given
        val unmemorizedRecords = listOf(
            WordLearningRecord(1, "test", false, System.currentTimeMillis())
        )
        every { WordLearningManager.getAllRecords() } returns unmemorizedRecords
        every { WordRepository.getWordById(1) } returns createWord(1, "test")

        // When
        val result = ReviewManager.hasReviewWords()

        // Then
        assertTrue(result)
    }

    @Test
    fun `hasReviewWords - 没有未记住单词时返回 false`() {
        // Given
        every { WordLearningManager.getAllRecords() } returns emptyList()

        // When
        val result = ReviewManager.hasReviewWords()

        // Then
        assertFalse(result)
    }

    @Test
    fun `getUnmemorizedCount - 正确返回未记住单词数量`() {
        // Given
        val unmemorizedRecords = (1..5).map {
            WordLearningRecord(it, "word$it", false, System.currentTimeMillis())
        }
        val unmemorizedWords = (1..5).map { createWord(it, "word$it") }

        every { WordLearningManager.getAllRecords() } returns unmemorizedRecords
        unmemorizedWords.forEach { word ->
            every { WordRepository.getWordById(word.id) } returns word
        }

        // When
        val result = ReviewManager.getUnmemorizedCount()

        // Then
        assertEquals(5, result)
    }

    @Test
    fun `setReviewRatio - 正确设置复习比例`() {
        // When
        ReviewManager.setReviewRatio(10)

        // Then
        assertEquals(10, ReviewManager.getReviewRatio())
        verify { mockEditor.putInt("review_ratio", 10) }
    }

    @Test
    fun `setReviewRatio - 限制最小值为 3`() {
        // When
        ReviewManager.setReviewRatio(1)

        // Then
        assertEquals(3, ReviewManager.getReviewRatio())
    }

    @Test
    fun `setReviewRatio - 限制最大值为 20`() {
        // When
        ReviewManager.setReviewRatio(100)

        // Then
        assertEquals(20, ReviewManager.getReviewRatio())
    }

    @Test
    fun `shuffleWithReviewWords - 边界条件：新单词少于 2 个`() {
        // Given
        val newWords = listOf(createWord(1, "word1"))
        val unmemorizedRecords = listOf(
            WordLearningRecord(2, "review2", false, System.currentTimeMillis())
        )
        val reviewWord = createWord(2, "review2")

        every { WordLearningManager.getAllRecords() } returns unmemorizedRecords
        every { WordRepository.getWordById(2) } returns reviewWord

        // When
        val result = ReviewManager.shuffleWithReviewWords(newWords)

        // Then
        // 应该返回原列表，因为计算出的复习单词数为 0 (1/8 = 0)
        assertEquals(1, result.size)
    }

    @Test
    fun `shuffleWithReviewWords - 只过滤未记住的单词`() {
        // Given
        val newWords = (1..10).map { createWord(it, "word$it") }
        val allRecords = listOf(
            WordLearningRecord(11, "memorized", true, System.currentTimeMillis()),
            WordLearningRecord(12, "unmemorized", false, System.currentTimeMillis())
        )
        val unmemorizedWord = createWord(12, "unmemorized")

        every { WordLearningManager.getAllRecords() } returns allRecords
        every { WordRepository.getWordById(12) } returns unmemorizedWord

        // When
        val result = ReviewManager.shuffleWithReviewWords(newWords)

        // Then
        assertEquals(11, result.size) // 10 + 1
        assertTrue(result.contains(unmemorizedWord))
        assertFalse(result.any { it.id == 11 }) // 已记住的不应该出现
    }

    // 辅助方法：创建测试用的 Word 对象
    private fun createWord(id: Int, word: String): Word {
        return Word(
            id = id,
            word = word,
            phonetic = "/test/",
            translation = "测试",
            example = "Example",
            frequency = 100,
            level = "CET4",
            pos = "n."
        )
    }
}
