package com.english.accelerator.ai.downloader

import android.content.Context
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import java.io.File

/**
 * 下载器诊断测试
 *
 * 用于诊断下载进度和状态持久化问题
 */
class DDownloadDiagnosticTest {

    /**
     * 测试：检查文件路径和目录创建
     */
    @Test
    fun testFilePathAndDirectory() {
        println("\n=== 测试1: 文件路径和目录 ===")

        // 模拟 Android Context
        val mockContext = MockContext()
        val configManager = DConfig(mockContext)
        val stateMonitor = DStateMonitor(mockContext, configManager)

        // 获取文件路径
        val filePath = stateMonitor.getFilePath()
        println("模型文件路径: $filePath")

        // 检查父目录
        val file = File(filePath)
        val parentDir = file.parentFile
        println("父目录: ${parentDir?.absolutePath}")
        println("父目录是否存在: ${parentDir?.exists()}")

        // 尝试创建目录
        val created = parentDir?.mkdirs() ?: false
        println("创建目录结果: $created")
        println("创建后父目录是否存在: ${parentDir?.exists()}")
    }

    /**
     * 测试：检查下载状态持久化
     */
    @Test
    fun testDownloadStatePersistence() {
        println("\n=== 测试2: 下载状态持久化 ===")

        val mockContext = MockContext()
        val configManager = DConfig(mockContext)

        // 模拟下载进度
        println("保存下载状态: 50% (1GB / 2GB)")
        configManager.updateDownloadState(
            modelPath = "/data/models/test.bin",
            downloadedBytes = 1_000_000_000L,
            totalBytes = 2_000_000_000L,
            isComplete = false,
            isPaused = true,
            downloadRoute = "MODELSCOPE"
        )

        // 读取状态
        val state = configManager.getCurrentDownloadState()
        println("读取的下载状态:")
        println("  - 已下载: ${state?.downloadedBytes} bytes")
        println("  - 总大小: ${state?.totalBytes} bytes")
        println("  - 是否暂停: ${state?.isPaused}")
        println("  - 是否完成: ${state?.isComplete}")

        // 验证
        assertNotNull("状态不应该为空", state)
        assertEquals("已下载字节数应该匹配", 1_000_000_000L, state?.downloadedBytes)
        assertEquals("总字节数应该匹配", 2_000_000_000L, state?.totalBytes)
        assertTrue("应该是暂停状态", state?.isPaused ?: false)
        assertFalse("不应该是完成状态", state?.isComplete ?: true)
    }

    /**
     * 测试：检查文件大小检测
     */
    @Test
    fun testFileSizeDetection() {
        println("\n=== 测试3: 文件大小检测 ===")

        val mockContext = MockContext()
        val configManager = DConfig(mockContext)
        val stateMonitor = DStateMonitor(mockContext, configManager)

        // 创建测试文件
        val testFile = File(stateMonitor.getFilePath())
        testFile.parentFile?.mkdirs()

        // 写入测试数据
        val testData = ByteArray(1024 * 1024) { it.toByte() } // 1MB
        testFile.writeBytes(testData)

        println("写入测试文件: ${testFile.absolutePath}")
        println("写入大小: ${testData.size} bytes")

        // 检测文件大小
        val detectedSize = stateMonitor.getFileSize()
        println("检测到的文件大小: $detectedSize bytes")

        // 检查状态
        val status = stateMonitor.getFileStatus()
        println("文件状态: $status")

        // 验证
        assertEquals("检测的文件大小应该匹配", testData.size.toLong(), detectedSize)
        assertEquals("状态应该是 PARTIAL", DStatus.PARTIAL, status)

        // 清理
        testFile.delete()
    }

    /**
     * 测试：检查进度计算
     */
    @Test
    fun testProgressCalculation() {
        println("\n=== 测试4: 进度计算 ===")

        val mockContext = MockContext()
        val configManager = DConfig(mockContext)
        val stateMonitor = DStateMonitor(mockContext, configManager)

        // 创建部分下载的文件
        val testFile = File(stateMonitor.getFilePath())
        testFile.parentFile?.mkdirs()

        val expectedSize = configManager.getExpectedModelSize()
        val downloadedSize = expectedSize / 2 // 50%

        val testData = ByteArray(downloadedSize.toInt()) { it.toByte() }
        testFile.writeBytes(testData)

        println("预期大小: $expectedSize bytes")
        println("已下载: $downloadedSize bytes")

        // 计算进度
        val progress = stateMonitor.getProgressPercentage()
        println("计算的进度: $progress%")

        // 验证
        assertTrue("进度应该接近 50%", progress in 49.0f..51.0f)

        // 清理
        testFile.delete()
    }

    /**
     * Mock Context for testing
     */
    private class MockContext : Context() {
        private val testFilesDir = File(System.getProperty("java.io.tmpdir"), "test_accelerator")

        init {
            testFilesDir.mkdirs()
        }

        override fun getFilesDir(): File = testFilesDir

        // 其他必需的方法可以返回 null 或抛出异常
        override fun getApplicationContext(): Context = this
        override fun getPackageName(): String = "com.english.accelerator.test"
    }
}
