/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ai.agent.tools

import com.english.accelerator.utils.AppLogger

/**
 * Write Tool - 写入数据
 */
class WriteTool {
    private val TAG = "WriteTool"
    private val storage = mutableMapOf<String, String>()

    fun execute(key: String, value: String): Result<Unit> {
        return try {
            storage[key] = value
            AppLogger.info(TAG, "Written: $key = $value")
            Result.success(Unit)
        } catch (e: Exception) {
            AppLogger.error(TAG, "Write failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
