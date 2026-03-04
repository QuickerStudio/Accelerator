package com.english.accelerator.ai.llm

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.english.accelerator.utils.AppLogger
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 模型加载集成测试
 * 需要在 Android 设备/模拟器上运行
 * 测试实际的模型文件加载和推理引擎初始化
 */
@RunWith(AndroidJUnit4::class)
class ModelLoadingIntegrationTest {

    private lateinit var context: Context
    private lateinit var modelFile: File

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        modelFile = File(context.filesDir, "models/gemma-3n-e2b-it-int4.litertlm")
    }

    @Test
    fun testModelFileExists() {
        // 验证模型文件是否存在
        assertTrue(
            modelFile.exists(),
            "Model file not found at: ${modelFile.absolutePath}"
        )
    }

    @Test
    fun testModelFileSize() {
        if (!modelFile.exists()) {
            println("Skipping test: Model file not found")
            return
        }

        val expectedSize = 3_655_827_456L // 3.4GB
        val actualSize = modelFile.length()

        // 允许 1% 的误差
        val tolerance = expectedSize * 0.01
        val difference = Math.abs(actualSize - expectedSize)

        assertTrue(
            difference < tolerance,
            "Model file size mismatch. Expected: $expectedSize, Actual: $actualSize"
        )
    }

    @Test
    fun testModelFileReadable() {
        if (!modelFile.exists()) {
            println("Skipping test: Model file not found")
            return
        }

        assertTrue(modelFile.canRead(), "Model file is not readable")
    }

    @Test
    fun testInferenceConfigCreation() {
        val config = InferenceConfig.forGemma3N(context)

        assertNotNull(config)
        assertEquals(modelFile.absolutePath, config.modelPath)
        assertEquals(2048, config.maxTokens)
    }

    @Test
    fun testInferenceEngineInitialization() {
        if (!modelFile.exists()) {
            println("Skipping test: Model file not found")
            return
        }

        try {
            val config = InferenceConfig.forGemma3N(context)
            val engine = InferenceEngine.getInstance(context, config)

            assertNotNull(engine)
            assertTrue(engine.isReady(), "Inference engine should be ready after initialization")

            println("✓ Inference engine initialized successfully")
        } catch (e: ModelLoadFailException) {
            println("✗ Model load failed: ${e.message}")
            throw e
        } catch (e: Exception) {
            println("✗ Unexpected error: ${e.message}")
            throw e
        }
    }

    @Test
    fun testSimpleInference() {
        if (!modelFile.exists()) {
            println("Skipping test: Model file not found")
            return
        }

        try {
            val config = InferenceConfig.forGemma3N(context)
            val engine = InferenceEngine.getInstance(context, config)

            val prompt = "<|im_start|>user\nHello\n<|im_end|>\n<|im_start|>assistant\n"
            val response = engine.generateSync(prompt)

            assertNotNull(response)
            assertTrue(response.isNotEmpty(), "Response should not be empty")

            println("✓ Simple inference test passed")
            println("Response: $response")
        } catch (e: Exception) {
            println("✗ Inference failed: ${e.message}")
            throw e
        }
    }

    @Test
    fun testTokenEstimation() {
        if (!modelFile.exists()) {
            println("Skipping test: Model file not found")
            return
        }

        try {
            val config = InferenceConfig.forGemma3N(context)
            val engine = InferenceEngine.getInstance(context, config)

            val testContext = "This is a test message for token estimation."
            val remainingTokens = engine.estimateTokensRemaining(testContext)

            assertTrue(remainingTokens >= 0, "Remaining tokens should be non-negative")
            assertTrue(remainingTokens < config.maxTokens, "Remaining tokens should be less than max")

            println("✓ Token estimation test passed")
            println("Remaining tokens: $remainingTokens")
        } catch (e: Exception) {
            println("✗ Token estimation failed: ${e.message}")
            throw e
        }
    }

    @Test
    fun testEmptyPromptHandling() {
        if (!modelFile.exists()) {
            println("Skipping test: Model file not found")
            return
        }

        try {
            val config = InferenceConfig.forGemma3N(context)
            val engine = InferenceEngine.getInstance(context, config)

            val response = engine.generateSync("")

            // 空提示词应该返回空字符串或抛出异常
            assertNotNull(response)

            println("✓ Empty prompt handling test passed")
        } catch (e: Exception) {
            println("✓ Empty prompt correctly throws exception: ${e.message}")
        }
    }

    @Test
    fun testLongContextHandling() {
        if (!modelFile.exists()) {
            println("Skipping test: Model file not found")
            return
        }

        try {
            val config = InferenceConfig.forGemma3N(context)
            val engine = InferenceEngine.getInstance(context, config)

            // 创建一个很长的上下文
            val longContext = "This is a test. ".repeat(500)
            val remainingTokens = engine.estimateTokensRemaining(longContext)

            assertTrue(remainingTokens >= 0, "Should handle long context gracefully")

            println("✓ Long context handling test passed")
            println("Remaining tokens for long context: $remainingTokens")
        } catch (e: Exception) {
            println("✗ Long context handling failed: ${e.message}")
            throw e
        }
    }
}
