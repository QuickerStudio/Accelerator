package com.english.accelerator.utils

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * 自动备份管理器 - 双保险机制
 *
 * 功能：
 * - 完整备份 files/ 目录到外部存储 .Accelerator/
 * - 使用 DataStateTracker 精确追踪数据修改时间
 * - 应用启动时对比内部和外部备份，使用最新的恢复
 * - 包括模型文件、截图、配置等所有数据
 */
object AutoBackupManager {

    private const val EXTERNAL_BACKUP_DIR = ".Accelerator"
    private const val STATE_FILE_NAME = "backup_state.json"
    private const val TAG = "AutoBackupManager"

    /**
     * 获取内部 files 目录
     */
    private fun getInternalFilesDir(context: Context): File {
        return context.filesDir
    }

    /**
     * 获取外部备份目录
     */
    private fun getExternalBackupDir(context: Context): File {
        val externalDir = context.getExternalFilesDir(null)
        return File(externalDir, EXTERNAL_BACKUP_DIR)
    }

    /**
     * 获取内部状态文件
     */
    private fun getInternalStateFile(context: Context): File {
        return File(getInternalFilesDir(context), STATE_FILE_NAME)
    }

    /**
     * 获取外部状态文件
     */
    private fun getExternalStateFile(context: Context): File {
        return File(getExternalBackupDir(context), STATE_FILE_NAME)
    }

    /**
     * 自动备份整个 files/ 目录到外部存储
     * 在后台异步执行，不阻塞主线程
     */
    fun autoBackup(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 检查数据是否完整，不备份空数据
                if (!DataStateTracker.isDataComplete()) {
                    AppLogger.info(TAG, "Data is incomplete, skipping backup")
                    return@launch
                }

                val internalDir = getInternalFilesDir(context)
                val externalBackupDir = getExternalBackupDir(context)

                // 确保外部备份目录存在
                if (!externalBackupDir.exists()) {
                    externalBackupDir.mkdirs()
                }

                // 递归复制整个 files/ 目录（跳过 models 目录）
                copyDirectory(internalDir, externalBackupDir)

                // 增加版本号
                val newVersion = DataStateTracker.incrementVersion()

                // 保存当前数据状态到外部
                val stateJson = DataStateTracker.exportStates()
                getExternalStateFile(context).writeText(stateJson)

                AppLogger.info(TAG, "Auto backup completed (version=$newVersion): ${internalDir.absolutePath} -> ${externalBackupDir.absolutePath}")
            } catch (e: Exception) {
                AppLogger.error(TAG, "Auto backup failed", e)
            }
        }
    }

    /**
     * 应用启动时自动恢复数据
     * 使用版本号和完整性标记判断是否需要恢复
     */
    suspend fun autoRestore(context: Context): Result<String> {
        return try {
            val internalDir = getInternalFilesDir(context)
            val externalBackupDir = getExternalBackupDir(context)
            val externalStateFile = getExternalStateFile(context)

            if (!externalBackupDir.exists() || !externalStateFile.exists()) {
                AppLogger.info(TAG, "No external backup found, skipping auto-restore")
                return Result.success("No backup found")
            }

            // 读取外部备份的元数据
            val externalStateJson = externalStateFile.readText()
            val externalMetadata = DataStateTracker.extractMetadata(externalStateJson)

            if (externalMetadata == null) {
                AppLogger.error(TAG, "Failed to extract external backup metadata")
                return Result.failure(Exception("Invalid backup metadata"))
            }

            // 检查内部数据是否完整
            val internalComplete = DataStateTracker.isDataComplete()
            val internalVersion = DataStateTracker.getCurrentVersion()

            AppLogger.info(TAG, "Internal: version=$internalVersion, complete=$internalComplete")
            AppLogger.info(TAG, "External: version=${externalMetadata.version}, complete=${externalMetadata.isComplete}")

            // 恢复逻辑：
            // 1. 如果内部数据不完整（空数据或初始化数据），从外部恢复
            // 2. 如果外部备份版本更高且完整，从外部恢复
            val shouldRestore = !internalComplete ||
                               (externalMetadata.isComplete && externalMetadata.version > internalVersion)

            if (shouldRestore) {
                AppLogger.info(TAG, "Restoring from external backup (version=${externalMetadata.version})")

                // 复制整个目录
                copyDirectory(externalBackupDir, internalDir, skipModels = false)

                // 恢复状态追踪器
                DataStateTracker.importStates(externalStateJson)

                AppLogger.info(TAG, "Auto-restore completed from external backup")
                Result.success("Restored from external backup (version ${externalMetadata.version})")
            } else {
                AppLogger.info(TAG, "Internal data is up-to-date, no restore needed")
                Result.success("No restore needed")
            }
        } catch (e: Exception) {
            AppLogger.error(TAG, "Auto-restore failed", e)
            Result.failure(e)
        }
    }

    /**
     * 检查是否需要恢复
     * 用于判断是否是应用更新后首次启动
     */
    fun shouldAutoRestore(context: Context): Boolean {
        val externalBackupDir = getExternalBackupDir(context)
        val externalStateFile = getExternalStateFile(context)

        if (!externalBackupDir.exists() || !externalStateFile.exists()) {
            return false
        }

        try {
            // 读取外部备份的状态时间戳
            val externalStateJson = externalStateFile.readText()
            val externalTime = getLatestTimeFromStateJson(externalStateJson)

            // 获取内部数据的最新时间戳
            val internalTime = DataStateTracker.getLatestModifiedTime()

            return externalTime > internalTime
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to check restore status", e)
            return false
        }
    }

    /**
     * 从状态 JSON 中获取最新修改时间
     */
    private fun getLatestTimeFromStateJson(json: String): Long {
        return try {
            val gson = com.google.gson.Gson()
            val jsonObject = gson.fromJson(json, com.google.gson.JsonObject::class.java)
            jsonObject.entrySet().maxOfOrNull { entry ->
                entry.value.asJsonObject.get("lastModified")?.asLong ?: 0L
            } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * 递归复制目录
     * 跳过模型文件（models/ 目录），避免频繁复制 3.6GB 文件
     */
    private fun copyDirectory(source: File, target: File, skipModels: Boolean = true) {
        if (!source.exists()) {
            return
        }

        if (source.isDirectory) {
            // 跳过 models 目录（模型文件单独管理）
            if (skipModels && source.name == "models") {
                AppLogger.debug(TAG, "Skipping models directory during backup")
                return
            }

            // 创建目标目录
            if (!target.exists()) {
                target.mkdirs()
            }

            // 递归复制子文件和子目录
            source.listFiles()?.forEach { file ->
                val targetFile = File(target, file.name)
                copyDirectory(file, targetFile, skipModels)
            }
        } else {
            // 复制文件
            source.copyTo(target, overwrite = true)
        }
    }

    /**
     * 备份模型文件（仅在模型下载完成时调用）
     */
    fun backupModelFile(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val internalModelsDir = File(getInternalFilesDir(context), "models")
                val externalModelsDir = File(getExternalBackupDir(context), "models")

                if (!internalModelsDir.exists()) {
                    AppLogger.info(TAG, "No models directory to backup")
                    return@launch
                }

                // 确保外部备份目录存在
                if (!externalModelsDir.exists()) {
                    externalModelsDir.mkdirs()
                }

                // 复制模型目录
                copyDirectory(internalModelsDir, externalModelsDir, skipModels = false)

                AppLogger.info(TAG, "Model file backup completed")
            } catch (e: Exception) {
                AppLogger.error(TAG, "Model file backup failed", e)
            }
        }
    }
}
