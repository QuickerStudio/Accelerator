/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.ai.agent.tools

import com.english.accelerator.utils.AppLogger

/**
 * Read Tool - 读取数据
 */
class ReadTool {
    private val TAG = "ReadTool"
    private val storage = mutableMapOf<String, String>()

    fun execute(key: String): Result<String> {
        return try {
            val value = storage[key]
            if (value != null) {
                Result.success(value)
            } else {
                Result.failure(Exception("Key not found: $key"))
            }
        } catch (e: Exception) {
            AppLogger.error(TAG, "Read failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
