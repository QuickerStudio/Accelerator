package com.english.accelerator.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 统一配置管理系统
 *
 * 功能：
 * - 集中管理所有应用配置
 * - 类型安全的配置读写
 * - 配置版本管理和迁移
 * - 配置导出和导入
 * - 配置变更监听
 */
class ConfigManager private constructor(context: Context) {

    internal val prefs: SharedPreferences = context.getSharedPreferences(
        "app_config",
        Context.MODE_PRIVATE
    )

    internal val gson = Gson()
    private val listeners = mutableListOf<ConfigChangeListener>()

    companion object {
        @Volatile
        private var instance: ConfigManager? = null

        private const val KEY_CONFIG_VERSION = "config_version"
        private const val CURRENT_CONFIG_VERSION = 1

        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ConfigManager(context.applicationContext)
                        instance?.migrateIfNeeded()
                    }
                }
            }
        }

        fun getInstance(): ConfigManager {
            return instance ?: throw IllegalStateException(
                "ConfigManager not initialized. Call init() first."
            )
        }
    }

    interface ConfigChangeListener {
        fun onConfigChanged(key: String, value: Any?)
    }

    init {
        AppLogger.info("ConfigManager", "ConfigManager initialized")
    }

    // ==================== 基础读写方法 ====================

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
        notifyListeners(key, value)
        AppLogger.debug("ConfigManager", "Set string: $key = $value")
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return prefs.getString(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
        notifyListeners(key, value)
        AppLogger.debug("ConfigManager", "Set int: $key = $value")
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return prefs.getInt(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
        notifyListeners(key, value)
        AppLogger.debug("ConfigManager", "Set long: $key = $value")
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return prefs.getLong(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
        notifyListeners(key, value)
        AppLogger.debug("ConfigManager", "Set boolean: $key = $value")
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    fun putFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
        notifyListeners(key, value)
        AppLogger.debug("ConfigManager", "Set float: $key = $value")
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return prefs.getFloat(key, defaultValue)
    }

    // ==================== 复杂对象存储 ====================

    fun <T> putObject(key: String, value: T) {
        val json = gson.toJson(value)
        putString(key, json)
        AppLogger.debug("ConfigManager", "Set object: $key")
    }

    fun <T> getObject(key: String, clazz: Class<T>): T? {
        val json = prefs.getString(key, null) ?: return null
        return try {
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            AppLogger.error("ConfigManager", "Failed to parse object: $key", e)
            null
        }
    }

    // ==================== 列表存储 ====================

    fun <T> putList(key: String, list: List<T>) {
        val json = gson.toJson(list)
        putString(key, json)
        AppLogger.debug("ConfigManager", "Set list: $key (${list.size} items)")
    }

    fun <T> getList(key: String, type: java.lang.reflect.Type): List<T>? {
        val json = prefs.getString(key, null) ?: return null
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            AppLogger.error("ConfigManager", "Failed to parse list: $key", e)
            null
        }
    }

    // ==================== 配置管理 ====================

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
        notifyListeners(key, null)
        AppLogger.debug("ConfigManager", "Removed: $key")
    }

    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    fun clear() {
        prefs.edit().clear().apply()
        AppLogger.warn("ConfigManager", "All config cleared")
    }

    fun getAllKeys(): Set<String> {
        return prefs.all.keys
    }

    // ==================== 配置导出和导入 ====================

    fun exportConfig(): Map<String, Any?> {
        val config = mutableMapOf<String, Any?>()
        prefs.all.forEach { (key, value) ->
            config[key] = value
        }
        AppLogger.info("ConfigManager", "Config exported (${config.size} items)")
        return config
    }

    fun importConfig(config: Map<String, Any?>) {
        prefs.edit().apply {
            config.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                }
            }
            apply()
        }
        AppLogger.info("ConfigManager", "Config imported (${config.size} items)")
    }

    // ==================== 配置版本管理 ====================

    private fun getConfigVersion(): Int {
        return getInt(KEY_CONFIG_VERSION, 0)
    }

    private fun setConfigVersion(version: Int) {
        putInt(KEY_CONFIG_VERSION, version)
    }

    private fun migrateIfNeeded() {
        val currentVersion = getConfigVersion()
        if (currentVersion < CURRENT_CONFIG_VERSION) {
            AppLogger.info("ConfigManager", "Migrating config from v$currentVersion to v$CURRENT_CONFIG_VERSION")
            performMigration(currentVersion, CURRENT_CONFIG_VERSION)
            setConfigVersion(CURRENT_CONFIG_VERSION)
        }
    }

    private fun performMigration(fromVersion: Int, toVersion: Int) {
        // 在这里实现配置迁移逻辑
        // 例如：重命名键、转换数据格式等
        when (fromVersion) {
            0 -> {
                // 从版本0迁移到版本1
                AppLogger.info("ConfigManager", "Performing migration from v0 to v1")
            }
        }
    }

    // ==================== 配置变更监听 ====================

    fun addListener(listener: ConfigChangeListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ConfigChangeListener) {
        listeners.remove(listener)
    }

    private fun notifyListeners(key: String, value: Any?) {
        listeners.forEach { listener ->
            try {
                listener.onConfigChanged(key, value)
            } catch (e: Exception) {
                AppLogger.error("ConfigManager", "Listener error for key: $key", e)
            }
        }
    }

    // ==================== 调试和诊断 ====================

    fun printAllConfig() {
        val allConfig = prefs.all
        AppLogger.info("ConfigManager", "=== All Config (${allConfig.size} items) ===")
        allConfig.forEach { (key, value) ->
            AppLogger.info("ConfigManager", "  $key = $value")
        }
    }

    fun getConfigSummary(): String {
        val allConfig = prefs.all
        return buildString {
            appendLine("Config Summary:")
            appendLine("  Total items: ${allConfig.size}")
            appendLine("  Config version: ${getConfigVersion()}")
            appendLine("  Keys: ${allConfig.keys.joinToString(", ")}")
        }
    }
}
