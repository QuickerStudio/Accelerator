package com.english.accelerator.ai.model

import android.content.Context
import android.content.SharedPreferences

/**
 * Model State - 表示模型的运行状态
 */
sealed class ModelState {
    object Idle : ModelState()
    object Loading : ModelState()
    object Ready : ModelState()
    data class Error(val message: String) : ModelState()
}

/**
 * 模型配置管理器
 *
 * 持久化保存模型相关的配置信息，解决应用"健忘症"问题
 */
class ModelConfig private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "model_config",
        Context.MODE_PRIVATE
    )

    companion object {
        @Volatile
        private var instance: ModelConfig? = null

        private const val KEY_MODEL_DOWNLOADED = "model_downloaded"
        private const val KEY_MODEL_PATH = "model_path"
        private const val KEY_MODEL_SIZE = "model_size"
        private const val KEY_MODEL_INITIALIZED = "model_initialized"
        private const val KEY_LAST_INIT_TIME = "last_init_time"
        private const val KEY_DOWNLOAD_ROUTE = "download_route"

        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ModelConfig(context.applicationContext)
                    }
                }
            }
        }

        fun getInstance(): ModelConfig {
            return instance ?: throw IllegalStateException(
                "ModelConfig not initialized. Call init() first."
            )
        }
    }

    /**
     * 标记模型已下载
     */
    fun markModelDownloaded(modelPath: String, fileSize: Long) {
        prefs.edit().apply {
            putBoolean(KEY_MODEL_DOWNLOADED, true)
            putString(KEY_MODEL_PATH, modelPath)
            putLong(KEY_MODEL_SIZE, fileSize)
            apply()
        }
    }

    /**
     * 检查模型是否已下载（从配置中读取）
     */
    fun isModelDownloaded(): Boolean {
        return prefs.getBoolean(KEY_MODEL_DOWNLOADED, false)
    }

    /**
     * 获取模型路径
     */
    fun getModelPath(): String? {
        return prefs.getString(KEY_MODEL_PATH, null)
    }

    /**
     * 获取模型文件大小
     */
    fun getModelSize(): Long {
        return prefs.getLong(KEY_MODEL_SIZE, 0L)
    }

    /**
     * 标记模型已初始化
     */
    fun markModelInitialized() {
        prefs.edit().apply {
            putBoolean(KEY_MODEL_INITIALIZED, true)
            putLong(KEY_LAST_INIT_TIME, System.currentTimeMillis())
            apply()
        }
    }

    /**
     * 标记初始化成功
     */
    fun markInitializationSuccess() {
        markModelInitialized()
    }

    /**
     * 标记初始化失败
     */
    fun markInitializationFailed(error: String) {
        prefs.edit().apply {
            putBoolean(KEY_MODEL_INITIALIZED, false)
            putString("last_init_error", error)
            putLong("last_init_error_time", System.currentTimeMillis())
            apply()
        }
    }

    /**
     * 检查模型是否已初始化
     */
    fun isModelInitialized(): Boolean {
        return prefs.getBoolean(KEY_MODEL_INITIALIZED, false)
    }

    /**
     * 获取上次初始化时间
     */
    fun getLastInitTime(): Long {
        return prefs.getLong(KEY_LAST_INIT_TIME, 0L)
    }

    /**
     * 保存下载线路
     */
    fun saveDownloadRoute(route: String) {
        prefs.edit().putString(KEY_DOWNLOAD_ROUTE, route).apply()
    }

    /**
     * 获取下载线路
     */
    fun getDownloadRoute(): String? {
        return prefs.getString(KEY_DOWNLOAD_ROUTE, null)
    }

    /**
     * 清除所有配置（用于重置）
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    /**
     * 重置模型初始化状态（保留下载状态）
     */
    fun resetInitializationState() {
        prefs.edit().apply {
            putBoolean(KEY_MODEL_INITIALIZED, false)
            remove(KEY_LAST_INIT_TIME)
            apply()
        }
    }
}
