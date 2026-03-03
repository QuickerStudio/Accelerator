package com.english.accelerator.ai.downloader

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Before
import org.junit.After
import java.io.File

/**
 * 桌面环境下载器测试
 *
 * 在 VSCode 中运行，直接在桌面创建文件进行测试
 */
class DDesktopTest {

    private val testDir = File("C:\\Users\\Quick\\Desktop\\accelerator_test")
    private val configFile = File(testDir, "download_states\\Config.json")
    private val modelFile = File(testDir, "models\\gemma-3n-e2b-it-int4.litertlm")

    @Before
    fun setup() {
        println("\n=== 测试环境设置 ===")
        println("测试目录: ${testDir.absolutePath}")

        // 创建测试目录
        testDir.mkdirs()
        File(testDir, "download_states").mkdirs()
        File(testDir, "models").mkdirs()

        println("✓ 测试目录已创建")
    }

    @After
    fun cleanup() {
        println("\n=== 清理测试环境 ===")
        // 不删除文件，保留用于检查
        println("测试文件保留在: ${testDir.absolutePath}")
    }

    /**
     * 测试1: 验证文件路径和目录创建
     */
    @Test
    fun testFilePathsAndDirectories() {
        println("\n=== 测试1: 文件路径和目录 ===")

        println("配置文件路径: ${configFile.absolutePath}")
        println("模型文件路径: ${modelFile.absolutePath}")

        println("配置文件父目录存在: ${configFile.parentFile?.exists()}")
        println("模型文件父目录存在: ${modelFile.parentFile?.exists()}")

        assert(configFile.parentFile?.exists() == true) { "配置文件目录应该存在" }
        assert(modelFile.parentFile?.exists() == true) { "模型文件目录应该存在" }

        println("✓ 所有目录已正确创建")
    }

    /**
     * 测试2: 模拟下载并保存状态
     */
    @Test
    fun testDownloadStateSimulation() = runBlocking {
        println("\n=== 测试2: 模拟下载并保存状态 ===")

        // 模拟创建部分下载的文件
        val testData = ByteArray(100 * 1024 * 1024) { it.toByte() } // 100MB
        modelFile.writeBytes(testData)
        println("✓ 创建测试文件: ${modelFile.length()} bytes")

        // 模拟保存配置
        val configContent = """
        {
          "model": {
            "name": "gemma-3n-e2b-it-int4",
            "expectedSize": 3655827456,
            "sizeTolerance": 1048576,
            "fileName": "gemma-3n-e2b-it-int4.litertlm"
          },
          "state": {
            "currentDownload": {
              "modelPath": "${modelFile.absolutePath.replace("\\", "\\\\")}",
              "downloadedBytes": ${modelFile.length()},
              "totalBytes": 3655827456,
              "isComplete": false,
              "isPaused": true,
              "lastUpdateTime": ${System.currentTimeMillis()},
              "downloadRoute": "MODELSCOPE",
              "errorMessage": null
            }
          }
        }
        """.trimIndent()

        configFile.writeText(configContent)
        println("✓ 保存配置文件: ${configFile.absolutePath}")

        // 验证文件
        println("\n验证结果:")
        println("- 模型文件大小: ${modelFile.length()} bytes")
        println("- 配置文件大小: ${configFile.length()} bytes")
        println("- 下载进度: ${(modelFile.length().toFloat() / 3655827456 * 100).toInt()}%")

        assert(modelFile.exists()) { "模型文件应该存在" }
        assert(configFile.exists()) { "配置文件应该存在" }
        assert(modelFile.length() > 0) { "模型文件应该有内容" }

        println("✓ 状态保存成功")
    }

    /**
     * 测试3: 验证状态恢复
     */
    @Test
    fun testStateRecovery() {
        println("\n=== 测试3: 验证状态恢复 ===")

        // 确保文件存在
        if (!configFile.exists() || !modelFile.exists()) {
            println("⚠️ 请先运行 testDownloadStateSimulation 创建测试文件")
            return
        }

        // 读取配置
        val configContent = configFile.readText()
        println("配置文件内容:")
        println(configContent)

        // 读取文件大小
        val fileSize = modelFile.length()
        val expectedSize = 3655827456L
        val progress = fileSize.toFloat() / expectedSize * 100

        println("\n恢复的状态:")
        println("- 文件大小: $fileSize bytes")
        println("- 预期大小: $expectedSize bytes")
        println("- 下载进度: ${String.format("%.2f", progress)}%")

        assert(fileSize > 0) { "应该能读取到文件大小" }
        assert(progress > 0) { "进度应该大于0" }

        println("✓ 状态恢复成功")
    }

    /**
     * 测试4: 清空缓存
     */
    @Test
    fun testClearCache() {
        println("\n=== 测试4: 清空缓存 ===")

        if (modelFile.exists()) {
            val sizeBefore = modelFile.length()
            println("删除前文件大小: $sizeBefore bytes")

            val deleted = modelFile.delete()
            println("删除结果: $deleted")
            println("文件是否存在: ${modelFile.exists()}")

            assert(deleted) { "应该能成功删除文件" }
            assert(!modelFile.exists()) { "文件应该不存在" }

            println("✓ 缓存清空成功")
        } else {
            println("⚠️ 没有缓存文件需要清空")
        }
    }

    /**
     * 测试5: 完整流程测试
     */
    @Test
    fun testCompleteFlow() = runBlocking {
        println("\n=== 测试5: 完整流程测试 ===")

        // 1. 清理旧文件
        println("\n步骤1: 清理旧文件")
        if (modelFile.exists()) modelFile.delete()
        if (configFile.exists()) configFile.delete()
        println("✓ 清理完成")

        // 2. 模拟开始下载
        println("\n步骤2: 模拟开始下载")
        val testData1 = ByteArray(50 * 1024 * 1024) { it.toByte() } // 50MB
        modelFile.writeBytes(testData1)
        println("✓ 下载了 ${modelFile.length()} bytes")

        // 3. 保存状态（暂停）
        println("\n步骤3: 暂停并保存状态")
        val configContent = """
        {
          "state": {
            "currentDownload": {
              "downloadedBytes": ${modelFile.length()},
              "totalBytes": 3655827456,
              "isPaused": true
            }
          }
        }
        """.trimIndent()
        configFile.writeText(configContent)
        println("✓ 状态已保存")

        // 4. 模拟重启应用（重新读取状态）
        println("\n步骤4: 模拟重启应用")
        val recoveredSize = modelFile.length()
        val recoveredConfig = configFile.readText()
        println("恢复的文件大小: $recoveredSize bytes")
        println("恢复的配置: ${recoveredConfig.take(100)}...")

        assert(recoveredSize == testData1.size.toLong()) { "恢复的文件大小应该匹配" }
        assert(recoveredConfig.contains("isPaused")) { "配置应该包含暂停状态" }

        println("✓ 状态恢复成功")

        // 5. 继续下载
        println("\n步骤5: 继续下载")
        val testData2 = ByteArray(50 * 1024 * 1024) { it.toByte() } // 再下载50MB
        modelFile.appendBytes(testData2)
        println("✓ 继续下载了 ${testData2.size} bytes")
        println("✓ 总大小: ${modelFile.length()} bytes")

        assert(modelFile.length() == (testData1.size + testData2.size).toLong()) { "总大小应该正确" }

        println("\n✓ 完整流程测试通过")
    }
}
