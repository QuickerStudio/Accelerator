# 测试策略

## 单元测试

### 测试范围
- ViewModel层的状态管理逻辑
- Use Case的业务规则
- Repository的数据转换逻辑
- 工具类和辅助函数

### 测试框架
JUnit 5 + MockK + Turbine（Flow测试）

### 关键测试用例

```kotlin
class VocabularyViewModelTest {
    @Test
    fun `loadTodayWords should update state to Success when use case succeeds`() = runTest {
        // Given
        val mockWords = listOf(
            Word(id = "1", word = "apple", definition = "a fruit"),
            Word(id = "2", word = "banana", definition = "a fruit")
        )
        coEvery { getWordListUseCase(any()) } returns Result.Success(mockWords)
        
        val viewModel = VocabularyViewModel(getWordListUseCase, learnWordUseCase, ttsService)
        
        // When
        viewModel.loadTodayWords()
        
        // Then
        viewModel.uiState.test {
            assertEquals(VocabularyUiState.Loading, awaitItem())
            assertEquals(VocabularyUiState.Success(mockWords), awaitItem())
        }
    }
    
    @Test
    fun `markWordAsLearned should call use case and reload words`() = runTest {
        // Given
        val wordId = "test-id"
        coEvery { learnWordUseCase(wordId) } returns Result.Success(Unit)
        coEvery { getWordListUseCase(any()) } returns Result.Success(emptyList())
        
        val viewModel = VocabularyViewModel(getWordListUseCase, learnWordUseCase, ttsService)
        
        // When
        viewModel.markWordAsLearned(wordId)
        
        // Then
        coVerify { learnWordUseCase(wordId) }
        coVerify(exactly = 2) { getWordListUseCase(any()) } // 初始加载 + 重新加载
    }
}

class CalculateNextReviewDateTest {
    @Test
    fun `should return correct interval for first review`() {
        // Given
        val word = Word(
            word = "test",
            reviewCount = 0,
            difficulty = DifficultyLevel.MEDIUM
        )
        
        // When
        val nextDate = calculateNextReviewDate(word)
        
        // Then
        assertEquals(LocalDate.now().plusDays(1), nextDate)
    }
    
    @Test
    fun `should adjust interval based on difficulty`() {
        // Given
        val easyWord = Word(word = "test", reviewCount = 1, difficulty = DifficultyLevel.EASY)
        val hardWord = Word(word = "test", reviewCount = 1, difficulty = DifficultyLevel.HARD)
        
        // When
        val easyDate = calculateNextReviewDate(easyWord)
        val hardDate = calculateNextReviewDate(hardWord)
        
        // Then
        assertTrue(easyDate.isAfter(hardDate)) // 简单单词间隔更长
    }
}
```

---

## 属性测试

### 测试库
Kotest Property Testing

### 测试属性

```kotlin
class GrammarCheckerPropertyTest : StringSpec({
    "grammar errors should have valid indices" {
        checkAll(Arb.string(1..1000)) { text ->
            val errors = grammarCheckerService.checkGrammar(text).getOrNull() ?: emptyList()
            
            errors.forEach { error ->
                error.startIndex shouldBeGreaterThanOrEqual 0
                error.endIndex shouldBeLessThanOrEqual text.length
                error.startIndex shouldBeLessThan error.endIndex
            }
        }
    }
    
    "LLM should always generate non-empty response for non-empty prompt" {
        checkAll(Arb.string(1..500)) { prompt ->
            val result = llmService.generateResponse(
                prompt = prompt,
                context = emptyList(),
                maxTokens = 100
            )
            
            result.isSuccess shouldBe true
            result.getOrNull()?.isNotEmpty() shouldBe true
        }
    }
    
    "word review count should never decrease" {
        checkAll(Arb.list(Arb.enum<WordStatus>())) { statusUpdates ->
            var word = Word(word = "test", reviewCount = 0)
            var previousCount = 0
            
            statusUpdates.forEach { status ->
                word = word.copy(status = status)
                if (status == WordStatus.LEARNED || status == WordStatus.MASTERED) {
                    word = word.copy(reviewCount = word.reviewCount + 1)
                }
                
                word.reviewCount shouldBeGreaterThanOrEqual previousCount
                previousCount = word.reviewCount
            }
        }
    }
})
```

---

## 集成测试

### 测试范围
- 端到端用户流程
- 数据库操作
- AI模型推理
- 多组件协作

### 测试框架
AndroidX Test + Hilt Test + Robolectric

### 关键测试场景

```kotlin
@HiltAndroidTest
class VocabularyFlowIntegrationTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var wordRepository: WordRepository
    
    @Test
    fun `complete word learning flow should update database correctly`() = runTest {
        // Given: 插入测试单词
        val testWords = listOf(
            Word(id = "1", word = "apple", learningDate = LocalDate.now()),
            Word(id = "2", word = "banana", learningDate = LocalDate.now())
        )
        wordRepository.insertWords(testWords)
        
        // When: 标记单词为已学习
        wordRepository.updateWordStatus("1", WordStatus.LEARNED)
        
        // Then: 验证数据库状态
        val updatedWord = wordRepository.getWordById("1").getOrNull()
        assertNotNull(updatedWord)
        assertEquals(WordStatus.LEARNED, updatedWord?.status)
        assertEquals(1, updatedWord?.reviewCount)
    }
}

@HiltAndroidTest
class SpeakingFlowIntegrationTest {
    @Test
    fun `speech recognition and LLM response flow should work end-to-end`() = runTest {
        // Given: 准备测试音频
        val testAudio = loadTestAudioFile("test_speech.wav")
        
        // When: 执行语音识别
        val transcription = speechRecognitionService.transcribe(testAudio)
        assertTrue(transcription.isSuccess)
        
        // When: 生成AI回复
        val response = llmService.generateConversationResponse(
            userInput = transcription.getOrNull()!!,
            conversationHistory = emptyList()
        )
        
        // Then: 验证响应
        assertTrue(response.isSuccess)
        assertNotNull(response.getOrNull()?.text)
        assertNotNull(response.getOrNull()?.feedback)
    }
}
```

---

## UI测试

### 测试框架
Jetpack Compose Testing

### 测试用例

```kotlin
class VocabularyScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun `should display word cards when data is loaded`() {
        // Given
        val testWords = listOf(
            Word(word = "apple", definition = "a fruit"),
            Word(word = "banana", definition = "a fruit")
        )
        val viewModel = mockk<VocabularyViewModel> {
            every { uiState } returns MutableStateFlow(VocabularyUiState.Success(testWords))
        }
        
        // When
        composeTestRule.setContent {
            VocabularyScreen(viewModel = viewModel)
        }
        
        // Then
        composeTestRule.onNodeWithText("apple").assertIsDisplayed()
        composeTestRule.onNodeWithText("banana").assertIsDisplayed()
    }
    
    @Test
    fun `should call markWordAsLearned when button is clicked`() {
        // Given
        val viewModel = mockk<VocabularyViewModel>(relaxed = true) {
            every { uiState } returns MutableStateFlow(
                VocabularyUiState.Success(listOf(Word(id = "1", word = "test")))
            )
        }
        
        composeTestRule.setContent {
            VocabularyScreen(viewModel = viewModel)
        }
        
        // When
        composeTestRule.onNodeWithText("已掌握").performClick()
        
        // Then
        verify { viewModel.markWordAsLearned("1") }
    }
}
```

---

## 测试覆盖率目标

- 单元测试覆盖率: >= 80%
- 集成测试覆盖率: >= 60%
- UI测试覆盖率: >= 50%
- 关键路径测试覆盖率: 100%
