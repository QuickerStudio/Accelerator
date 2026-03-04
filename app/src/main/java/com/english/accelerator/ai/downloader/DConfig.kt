package com.english.accelerator.ai.downloader

import android.content.Context
import com.english.accelerator.utils.AppLogger
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File

/**
 * Config.json 配置管理器
 *
 * 管理下载器的持久化配置和状态
 * 这是下载管理器的核心记忆文件
 */
class DConfig(private val context: Context) {

    private val configFile = File(context.filesDir, "download_states/Config.json")
    private var config: JsonObject? = null
    private val configLock = Any()

    companion object {
        private const val TAG = "DownloadDConfig"
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
                createDefaultConfig()
            }

            val json = configFile.readText()
            config = Gson().fromJson(json, JsonObject::class.java)
            AppLogger.info(TAG, "Config loaded successfully")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to load config", e)
            createDefaultConfig()
        }
    }

    /**
     * 创建默认配置
     */
    private fun createDefaultConfig() {
        try {
            configFile.parentFile?.mkdirs()

            val defaultConfig = JsonObject().apply {
                // Model config
                add("model", JsonObject().apply {
                    addProperty("name", "gemma-3n-e2b-it-int4")
                    addProperty("expectedSize", 3_655_827_456L)
                    addProperty("sizeTolerance", 1024 * 1024)
                    addProperty("fileName", "gemma-3n-e2b-it-int4.litertlm")
                })

                // Download config
                add("download", JsonObject().apply {
                    addProperty("chunkSize", 8192)
                    addProperty("maxRetries", 3)
                    addProperty("timeoutMs", 30000)
                    addProperty("defaultRoute", "MODELSCOPE")
                    add("routes", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("name", "MODELSCOPE")
                            addProperty("displayName", "魔塔社区")
                            addProperty("url", "https://www.modelscope.cn/models/google/gemma-3n-E2B-it-litert-lm/resolve/master/gemma-3n-E2B-it-int4.litertlm")
                            addProperty("supportsRange", true)  // 默认假设支持
                            addProperty("rangeChecked", false)  // 是否已检测
                        })
                        add(JsonObject().apply {
                            addProperty("name", "HUGGINGFACE")
                            addProperty("displayName", "HuggingFace")
                            addProperty("url", "https://huggingface.co/google/gemma-3n-E2B-it-litert-lm/resolve/main/gemma-3n-E2B-it-int4.litertlm")
                            addProperty("supportsRange", true)  // 默认假设支持
                            addProperty("rangeChecked", false)  // 是否已检测
                        })
                    })
                })

                // State
                add("state", JsonObject().apply {
                    add("currentDownload", JsonObject().apply {
                        addProperty("modelPath", "")
                        addProperty("downloadedBytes", 0L)
                        addProperty("totalBytes", 3_655_827_456L)
                        addProperty("isComplete", false)
                        addProperty("isPaused", false)
                        addProperty("lastUpdateTime", 0L)
                        addProperty("downloadRoute", "MODELSCOPE")
                        addProperty("errorMessage", null as String?)
                    })
                })

                // Logs
                add("logs", JsonObject().apply {
                    add("downloadHistory", JsonArray())
                    add("initializationLogs", JsonArray())
                    add("loadLogs", JsonArray())
                    add("errorLogs", JsonArray())
                    add("resumeLogs", JsonArray())
                })

                addProperty("version", "1.0.0")
            }

            config = defaultConfig
            saveConfig()
            AppLogger.info(TAG, "Default config created")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to create default config", e)
        }
    }

    /**
     * 保存配置文件（内部方法，不加锁）
     */
    private fun saveConfigInternal() {
        try {
            val json = Gson().toJson(config)
            configFile.writeText(json)
            AppLogger.debug(TAG, "Config saved")
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to save config", e)
        }
    }

    /**
     * 保存配置文件（外部调用，加锁）
     */
    private fun saveConfig() {
        synchronized(configLock) {
            saveConfigInternal()
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
        errorMessage: String? = null,
        downloadSpeed: Long = 0L
    ) {
        synchronized(configLock) {
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
                    addProperty("downloadSpeed", downloadSpeed)
                }

                saveConfigInternal()
                AppLogger.debug(TAG, "Download state updated: $downloadedBytes / $totalBytes bytes")
            } catch (e: Exception) {
                AppLogger.error(TAG, "Failed to update download state", e)
            }
        }
    }

    /**
     * 添加下载历史日志
     */
    fun addDownloadLog(message: String) {
        addLog("downloadHistory", message)
    }

    /**
     * 添加初始化日志
     */
    fun addInitializationLog(message: String) {
        addLog("initializationLogs", message)
    }

    /**
     * 添加加载日志
     */
    fun addLoadLog(message: String) {
        addLog("loadLogs", message)
    }

    /**
     * 添加错误日志
     */
    fun addErrorLog(message: String) {
        addLog("errorLogs", message)
    }

    /**
     * 添加断点续传日志
     */
    fun addResumeLog(message: String) {
        addLog("resumeLogs", message)
    }

    /**
     * 通用日志添加方法
     */
    private fun addLog(logType: String, message: String) {
        synchronized(configLock) {
            try {
                val logs = config?.getAsJsonObject("logs")
                val logArray = logs?.getAsJsonArray(logType)

                val logEntry = JsonObject().apply {
                    addProperty("timestamp", System.currentTimeMillis())
                    addProperty("message", message)
                }

                logArray?.add(logEntry)
                saveConfigInternal()
                AppLogger.debug(TAG, "Added $logType: $message")
            } catch (e: Exception) {
                AppLogger.error(TAG, "Failed to add log", e)
            }
        }
    }

    /**
     * 获取当前下载状态
     */
    fun getCurrentDownloadState(): DState? {
        return try {
            val state = config?.getAsJsonObject("state")
            val currentDownload = state?.getAsJsonObject("currentDownload")

            currentDownload?.let {
                DState(
                    modelPath = it.get("modelPath")?.takeIf { !it.isJsonNull }?.asString ?: "",
                    downloadedBytes = it.get("downloadedBytes")?.takeIf { !it.isJsonNull }?.asLong ?: 0L,
                    totalBytes = it.get("totalBytes")?.takeIf { !it.isJsonNull }?.asLong ?: 0L,
                    isComplete = it.get("isComplete")?.takeIf { !it.isJsonNull }?.asBoolean ?: false,
                    isPaused = it.get("isPaused")?.takeIf { !it.isJsonNull }?.asBoolean ?: false,
                    lastUpdateTime = it.get("lastUpdateTime")?.takeIf { !it.isJsonNull }?.asLong ?: 0L,
                    downloadRoute = it.get("downloadRoute")?.takeIf { !it.isJsonNull }?.asString ?: "MODELSCOPE",
                    errorMessage = it.get("errorMessage")?.takeIf { !it.isJsonNull }?.asString,
                    downloadSpeed = it.get("downloadSpeed")?.takeIf { !it.isJsonNull }?.asLong ?: 0L
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
            totalBytes = 3_655_827_456L,
            isComplete = false,
            isPaused = false,
            downloadRoute = "MODELSCOPE",
            errorMessage = null
        )
        AppLogger.info(TAG, "Download state cleared")
    }

    /**
     * 获取模型预期大小
     */
    fun getExpectedModelSize(): Long {
        return try {
            config?.getAsJsonObject("model")?.get("expectedSize")?.asLong ?: 3_655_827_456L
        } catch (e: Exception) {
            3_655_827_456L
        }
    }

    /**
     * 获取模型文件名
     */
    fun getModelFileName(): String {
        return try {
            config?.getAsJsonObject("model")?.get("fileName")?.asString ?: "gemma-3n-e2b-it-int4.litertlm"
        } catch (e: Exception) {
            "gemma-3n-e2b-it-int4.litertlm"
        }
    }

    /**
     * 获取文件大小容差值
     */
    fun getSizeTolerance(): Long {
        return try {
            config?.getAsJsonObject("model")?.get("sizeTolerance")?.asLong ?: (1024 * 1024L)
        } catch (e: Exception) {
            1024 * 1024L
        }
    }

    /**
     * 获取默认下载线路
     */
    fun getDefaultRoute(): String {
        return try {
            config?.getAsJsonObject("download")?.get("defaultRoute")?.asString ?: "MODELSCOPE"
        } catch (e: Exception) {
            "MODELSCOPE"
        }
    }

    /**
     * 获取下载线路列表
     */
    fun getDownloadRoutes(): List<DRoute> {
        return try {
            val download = config?.getAsJsonObject("download")
            val routes = download?.getAsJsonArray("routes")

            routes?.map { routeElement ->
                val route = routeElement.asJsonObject
                DRoute(
                    name = route.get("name")?.asString ?: "",
                    displayName = route.get("displayName")?.asString ?: "",
                    url = route.get("url")?.asString ?: "",
                    supportsRange = route.get("supportsRange")?.asBoolean ?: true,
                    rangeChecked = route.get("rangeChecked")?.asBoolean ?: false
                )
            } ?: emptyList()
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to get download routes", e)
            emptyList()
        }
    }

    /**
     * 更新指定线路的断点续传支持状态
     */
    fun updateRouteRangeSupport(url: String, supportsRange: Boolean) {
        synchronized(configLock) {
            try {
                val download = config?.getAsJsonObject("download")
                val routes = download?.getAsJsonArray("routes")

                routes?.forEach { routeElement ->
                    val route = routeElement.asJsonObject
                    if (route.get("url")?.asString == url) {
                        route.addProperty("supportsRange", supportsRange)
                        route.addProperty("rangeChecked", true)
                        saveConfigInternal()
                        AppLogger.info(TAG, "Updated Range support for $url: $supportsRange")
                        return
                    }
                }
            } catch (e: Exception) {
                AppLogger.error(TAG, "Failed to update route Range support", e)
            }
        }
    }

    /**
     * 检查指定线路是否支持断点续传
     */
    fun isRangeSupportedByUrl(url: String): Boolean? {
        return try {
            val download = config?.getAsJsonObject("download")
            val routes = download?.getAsJsonArray("routes")

            routes?.forEach { routeElement ->
                val route = routeElement.asJsonObject
                if (route.get("url")?.asString == url) {
                    val checked = route.get("rangeChecked")?.asBoolean ?: false
                    return if (checked) {
                        route.get("supportsRange")?.asBoolean ?: true
                    } else {
                        null  // 未检测过，返回 null
                    }
                }
            }
            null
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to check Range support", e)
            null
        }
    }
}
