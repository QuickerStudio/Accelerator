package com.english.accelerator.ai.download.States

import android.content.Context
import com.english.accelerator.utils.AppLogger
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

/**
 * Config.json 配置管理器
 *
 * 管理下载器的持久化配置和状态
 */
class ConfigManager(private val context: Context) {

    private val configFile = File(context.filesDir, "download_states/Config.json")
    private var config: JsonObject? = null

    companion object {
        private const val TAG = "DownloadConfigManager"
    }

    init {
        loadConfig()
    }

    /**
     * 加载配置文件
     */
    private fun loadConfig() {
        try {
            if (!configFile.exists()) {
                // 从 assets 复制默认配置
                copyDefaultConfig()
            }

            val json = configFile.readText()
            config = Gson().fromJson(json, JsonObject::class.java)
            AppLogger.info(TAG, "Config loaded successfully")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to load config", e)
        }
    }

    /**
     * 从 assets 复制默认配置
     */
    private fun copyDefaultConfig() {
        try {
            configFile.parentFile?.mkdirs()
            val defaultConfig = context.assets.open("download_states/Config.json").bufferedReader().use { it.readText() }
            configFile.writeText(defaultConfig)
            AppLogger.info(TAG, "Default config copied")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to copy default config", e)
        }
    }

    /**
     * 保存配置文件
     */
    private fun saveConfig() {
        try {
            val json = Gson().toJson(config)
            configFile.writeText(json)
            AppLogger.debug(TAG, "Config saved")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to save config", e)
        }
    }

    /**
     * 更新当前下载状态
     */
    fun updateDownloadState(
        modelPath: String,
        downloadedBytes: Long,
        totalBytes: Long,
        isComplete: Boolean,
        isPaused: Boolean,
        downloadRoute: String,
        errorMessage: String? = null
    ) {
        try {
            val state = config?.getAsJsonObject("state")
            val currentDownload = state?.getAsJsonObject("currentDownload")

            currentDownload?.apply {
                addProperty("modelPath", modelPath)
                addProperty("downloadedBytes", downloadedBytes)
                addProperty("totalBytes", totalBytes)
                addProperty("isComplete", isComplete)
                addProperty("isPaused", isPaused)
                addProperty("lastUpdateTime", System.currentTimeMillis())
                addProperty("downloadRoute", downloadRoute)
                addProperty("errorMessage", errorMessage)
            }

            saveConfig()
            AppLogger.debug(TAG, "Download state updated: $downloadedBytes / $totalBytes bytes")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to update download state", e)
        }
    }

    /**
     * 获取当前下载状态
     */
    fun getCurrentDownloadState(): DownloadStateInfo? {
        return try {
            val state = config?.getAsJsonObject("state")
            val currentDownload = state?.getAsJsonObject("currentDownload")

            currentDownload?.let {
                DownloadStateInfo(
                    modelPath = it.get("modelPath")?.asString ?: "",
                    downloadedBytes = it.get("downloadedBytes")?.asLong ?: 0L,
                    totalBytes = it.get("totalBytes")?.asLong ?: 0L,
                    isComplete = it.get("isComplete")?.asBoolean ?: false,
                    isPaused = it.get("isPaused")?.asBoolean ?: false,
                    lastUpdateTime = it.get("lastUpdateTime")?.asLong ?: 0L,
                    downloadRoute = it.get("downloadRoute")?.asString ?: "HUGGINGFACE",
                    errorMessage = it.get("errorMessage")?.asString
                )
            }
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to get download state", e)
            null
        }
    }

    /**
     * 清除当前下载状态
     */
    fun clearDownloadState() {
        updateDownloadState(
            modelPath = "",
            downloadedBytes = 0L,
            totalBytes = 1_932_735_488L,
            isComplete = false,
            isPaused = false,
            downloadRoute = "HUGGINGFACE",
            errorMessage = null
        )
        AppLogger.info(TAG, "Download state cleared")
    }

    /**
     * 获取模型预期大小
     */
    fun getExpectedModelSize(): Long {
        return try {
            config?.getAsJsonObject("model")?.get("expectedSize")?.asLong ?: 1_932_735_488L
        } catch (e: Exception) {
            1_932_735_488L
        }
    }

    /**
     * 获取下载线路列表
     */
    fun getDownloadRoutes(): List<DownloadRouteInfo> {
        return try {
            val download = config?.getAsJsonObject("download")
            val routes = download?.getAsJsonArray("routes")

            routes?.map { routeElement ->
                val route = routeElement.asJsonObject
                DownloadRouteInfo(
                    name = route.get("name")?.asString ?: "",
                    displayName = route.get("displayName")?.asString ?: "",
                    url = route.get("url")?.asString ?: ""
                )
            } ?: emptyList()
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to get download routes", e)
            emptyList()
        }
    }
}

/**
 * 下载状态信息
 */
data class DownloadStateInfo(
    val modelPath: String,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val isComplete: Boolean,
    val isPaused: Boolean,
    val lastUpdateTime: Long,
    val downloadRoute: String,
    val errorMessage: String?
)

/**
 * 下载线路信息
 */
data class DownloadRouteInfo(
    val name: String,
    val displayName: String,
    val url: String
)
