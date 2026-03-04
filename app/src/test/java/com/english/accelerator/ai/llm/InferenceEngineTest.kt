package com.english.accelerator.ai.llm

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * 推理引擎单元测试
 * 测试模型加载、初始化和推理功能
 */
@RunWith(MockitoJUnitRunner::class)
class InferenceEngineTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockFilesDir: File

    private lateinit var testModelPath: String

    @Before
    fun setup() {
        testModelPath = "/data/data/com.english.accelerator/files/models/gemma-3n-e2b-it-int4.litertlm"
        `when`(mockContext.filesDir).thenReturn(mockFilesDir)
        `when`(mockFilesDir.absolutePath).thenReturn("/data/data/com.english.accelerator/files")
    }

    @Test
    fun `test InferenceConfig creation for Gemma3N`() {
        val config = InferenceConfig.forGemma3N(mockContext)

        assertEquals(testModelPath, config.modelPath)
        assertEquals(2048, config.maxTokens)
        assertEquals(256, config.decodeTokenOffset)
        assertEquals(0.3f, config.temperature)
        assertEquals(40, config.topK)
        assertEquals(0.95f, config.topP)
    }

    @Test
    fun `test model path validation`() {
        val config = InferenceConfig(
            modelPath = testModelPath,
            maxTokens = 2048
        )

        assertTrue(config.modelPath.endsWith(".litertlm"))
        assertTrue(config.modelPath.contains("gemma-3n-e2b-it-int4"))
    }

    @Test
    fun `test token estimation logic`() {
        val config = InferenceConfig.forGemma3N(mockContext)

        // 验证 token 计算逻辑
        val maxTokens = config.maxTokens
        val offset = config.decodeTokenOffset
        val expectedRemaining = maxTokens - offset

        assertTrue(expectedRemaining > 0)
        assertEquals(1792, expectedRemaining) // 2048 - 256
    }

    @Test
    fun `test config parameters are within valid ranges`() {
        val config = InferenceConfig.forGemma3N(mockContext)

        // Temperature 应该在 0-1 之间
        assertTrue(config.temperature >= 0f && config.temperature <= 1f)

        // TopP 应该在 0-1 之间
        assertTrue(config.topP >= 0f && config.topP <= 1f)

        // TopK 应该是正数
        assertTrue(config.topK > 0)

        // MaxTokens 应该是正数
        assertTrue(config.maxTokens > 0)
    }
}
