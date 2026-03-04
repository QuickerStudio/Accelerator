/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator

import android.app.Application
import com.english.accelerator.utils.AppLogger
import com.english.accelerator.utils.AutoBackupManager
import com.english.accelerator.utils.DataStateTracker
import com.english.accelerator.utils.DConfig
import com.english.accelerator.ai.model.ModelConfig
import com.english.accelerator.ai.session.SessionManager
import com.english.accelerator.ai.history.HistoryManager
import com.english.accelerator.data.WordLearningManager
import com.english.accelerator.data.WordRepository
import com.english.accelerator.data.BookmarkManager
import com.english.accelerator.data.EssayCollectionManager
import com.english.accelerator.utils.WordLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application 类 - 应用全局初始化
 *
 * 功能：
 * - 初始化所有管理器
 * - 自动恢复备份数据（双保险机制）
 * - 确保数据持久化
 */
class AcceleratorApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        // 初始化基础设施系统（必须最先初始化）
        AppLogger.init(this)
        DConfig.init(this)
        ModelConfig.init(this)
        DataStateTracker.init(this)

        AppLogger.info("Application", "Application started")

        // 检查是否需要自动恢复备份
        if (AutoBackupManager.shouldAutoRestore(this)) {
            AppLogger.info("Application", "Detected app update or data loss, attempting auto-restore")
            applicationScope.launch {
                val result = AutoBackupManager.autoRestore(this@AcceleratorApplication)
                result.onSuccess { message ->
                    AppLogger.info("Application", "Auto-restore successful: $message")
                }.onFailure { error ->
                    AppLogger.error("Application", "Auto-restore failed", error)
                }

                // 恢复后初始化所有管理器
                initializeManagers()
            }
        } else {
            // 正常启动，直接初始化
            initializeManagers()
        }
    }

    /**
     * 初始化所有数据管理器
     */
    private fun initializeManagers() {
        // 初始化 AI 系统
        SessionManager.init(this)
        HistoryManager.init(this)

        // 初始化数据管理器
        WordLearningManager.init(this)
        WordRepository.init(this)
        BookmarkManager.init(this)
        EssayCollectionManager.init(this)

        // 初始化 WordLoader（中间件层）
        WordLoader.init(this)

        AppLogger.info("Application", "All managers initialized successfully")
    }
}
