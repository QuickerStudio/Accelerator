/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * 数据状态追踪器 - 精确追踪每种数据的时间戳和完整性
 *
 * 功能：
 * - 记录每种数据类型的最后修改时间（精确到毫秒）
 * - 记录数据版本号和完整性标记
 * - 用于判断哪些数据需要备份/恢复
 * - 避免用空数据覆盖完整备份
 */
object DataStateTracker {

    private const val PREFS_NAME = "data_state_tracker"
    private const val KEY_STATE = "data_states"
    private const val KEY_VERSION = "backup_version"
    private const val KEY_IS_COMPLETE = "is_complete"
    private const val TAG = "DataStateTracker"

    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()

    /**
     * 数据类型枚举
     */
    enum class DataType {
        WORD_LEARNING,          // 单词学习记录
        LEARNING_PROGRESS,      // 学习进度
        BOOKMARKS,              // 收藏单词
        ESSAYS,                 // 作文收藏
        MODEL_CONFIG,           // 模型配置
        APP_CONFIG,             // 应用配置
        MODEL_FILE,             // 模型文件
        SCREENSHOTS,            // 截图
        DOWNLOAD_CONFIG         // 下载配置
    }

    /**
     * 数据状态
     */
    data class DataState(
        val type: DataType,
        val lastModified: Long,
        val size: Long = 0L,
        val checksum: String? = null,
        val isComplete: Boolean = true  // 数据是否完整（不是空数据）
    )

    /**
     * 备份元数据
     */
    data class BackupMetadata(
        val version: Long,              // 备份版本号（递增）
        val timestamp: Long,            // 备份时间戳
        val isComplete: Boolean,        // 数据是否完整
        val dataCount: Int              // 数据项数量
    )

    /**
     * 初始化
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 更新数据状态
     */
    fun updateState(type: DataType, size: Long = 0L, checksum: String? = null, isComplete: Boolean = true) {
        try {
            val states = loadStates().toMutableMap()
            states[type.name] = DataState(
                type = type,
                lastModified = System.currentTimeMillis(),
                size = size,
                checksum = checksum,
                isComplete = isComplete
            )
            saveStates(states)
            AppLogger.debug(TAG, "Updated state for ${type.name}: ${states[type.name]?.lastModified}, complete=$isComplete")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to update state for ${type.name}", e)
        }
    }

    /**
     * 获取数据状态
     */
    fun getState(type: DataType): DataState? {
        return loadStates()[type.name]
    }

    /**
     * 获取所有数据状态
     */
    fun getAllStates(): Map<String, DataState> {
        return loadStates()
    }

    /**
     * 获取最新修改时间
     */
    fun getLatestModifiedTime(): Long {
        val states = loadStates()
        return states.values.maxOfOrNull { it.lastModified } ?: 0L
    }

    /**
     * 检查数据是否完整（不是空数据）
     */
    fun isDataComplete(): Boolean {
        val states = loadStates()
        // 如果没有任何数据状态，说明是刚初始化的空数据
        if (states.isEmpty()) {
            return false
        }
        // 检查是否所有数据都标记为完整
        return states.values.all { it.isComplete }
    }

    /**
     * 获取当前备份版本号
     */
    fun getCurrentVersion(): Long {
        return sharedPreferences?.getLong(KEY_VERSION, 0L) ?: 0L
    }

    /**
     * 增加备份版本号
     */
    fun incrementVersion(): Long {
        val newVersion = getCurrentVersion() + 1
        sharedPreferences?.edit()?.putLong(KEY_VERSION, newVersion)?.commit()
        return newVersion
    }

    /**
     * 设置备份版本号
     */
    fun setVersion(version: Long) {
        sharedPreferences?.edit()?.putLong(KEY_VERSION, version)?.commit()
    }

    /**
     * 标记数据为完整
     */
    fun markAsComplete() {
        sharedPreferences?.edit()?.putBoolean(KEY_IS_COMPLETE, true)?.commit()
    }

    /**
     * 标记数据为不完整
     */
    fun markAsIncomplete() {
        sharedPreferences?.edit()?.putBoolean(KEY_IS_COMPLETE, false)?.commit()
    }

    /**
     * 加载状态
     */
    private fun loadStates(): Map<String, DataState> {
        val json = sharedPreferences?.getString(KEY_STATE, null) ?: return emptyMap()
        return try {
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            jsonObject.entrySet().associate { (key, value) ->
                val stateObj = value.asJsonObject
                key to DataState(
                    type = DataType.valueOf(key),
                    lastModified = stateObj.get("lastModified").asLong,
                    size = stateObj.get("size")?.asLong ?: 0L,
                    checksum = stateObj.get("checksum")?.asString,
                    isComplete = stateObj.get("isComplete")?.asBoolean ?: true
                )
            }
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to load states", e)
            emptyMap()
        }
    }

    /**
     * 保存状态
     */
    private fun saveStates(states: Map<String, DataState>) {
        try {
            val jsonObject = JsonObject()
            states.forEach { (key, state) ->
                val stateObj = JsonObject().apply {
                    addProperty("lastModified", state.lastModified)
                    addProperty("size", state.size)
                    addProperty("checksum", state.checksum)
                    addProperty("isComplete", state.isComplete)
                }
                jsonObject.add(key, stateObj)
            }
            sharedPreferences?.edit()?.putString(KEY_STATE, gson.toJson(jsonObject))?.commit()
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to save states", e)
        }
    }

    /**
     * 导出状态到 JSON（用于备份）
     */
    fun exportStates(): String {
        val states = loadStates()
        val metadata = BackupMetadata(
            version = getCurrentVersion(),
            timestamp = System.currentTimeMillis(),
            isComplete = isDataComplete(),
            dataCount = states.size
        )

        val exportData = JsonObject().apply {
            add("metadata", gson.toJsonTree(metadata))
            add("states", gson.toJsonTree(states))
        }

        return gson.toJson(exportData)
    }

    /**
     * 从 JSON 导入状态（用于恢复）
     */
    fun importStates(json: String) {
        try {
            val exportData = gson.fromJson(json, JsonObject::class.java)

            // 导入元数据
            val metadataObj = exportData.getAsJsonObject("metadata")
            val version = metadataObj.get("version")?.asLong ?: 0L

            // 导入状态
            val statesObj = exportData.getAsJsonObject("states")
            val states = statesObj.entrySet().associate { (key, value) ->
                val stateObj = value.asJsonObject
                key to DataState(
                    type = DataType.valueOf(key),
                    lastModified = stateObj.get("lastModified").asLong,
                    size = stateObj.get("size")?.asLong ?: 0L,
                    checksum = stateObj.get("checksum")?.asString,
                    isComplete = stateObj.get("isComplete")?.asBoolean ?: true
                )
            }

            saveStates(states)
            setVersion(version)
            markAsComplete()

            AppLogger.info(TAG, "Imported ${states.size} data states, version=$version")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to import states", e)
        }
    }

    /**
     * 从 JSON 中提取备份元数据
     */
    fun extractMetadata(json: String): BackupMetadata? {
        return try {
            val exportData = gson.fromJson(json, JsonObject::class.java)
            val metadataObj = exportData.getAsJsonObject("metadata")
            BackupMetadata(
                version = metadataObj.get("version")?.asLong ?: 0L,
                timestamp = metadataObj.get("timestamp")?.asLong ?: 0L,
                isComplete = metadataObj.get("isComplete")?.asBoolean ?: false,
                dataCount = metadataObj.get("dataCount")?.asInt ?: 0
            )
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to extract metadata", e)
            null
        }
    }
}
