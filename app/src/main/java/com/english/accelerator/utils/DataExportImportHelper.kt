package com.english.accelerator.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 数据导出导入工具类
 *
 * 功能：
 * - 导出应用数据为 .Accele 格式（JSON）
 * - 导入 .Accele 格式数据
 * - 包含：已记住/未记住单词、设置、统计数据等
 */
object DataExportImportHelper {

    /**
     * 导出所有应用数据
     */
    suspend fun exportData(context: Context): Result<File> = withContext(Dispatchers.IO) {
        try {
            val exportData = JSONObject().apply {
                // 元数据
                put("version", "1.0")
                put("exportDate", System.currentTimeMillis())
                put("appVersion", "0.5.0")

                // 单词学习数据
                put("wordLearning", exportWordLearningData(context))

                // 设置数据
                put("settings", exportSettingsData(context))

                // 统计数据
                put("statistics", exportStatisticsData(context))

                // 收藏数据
                put("bookmarks", exportBookmarksData(context))

                // 写作数据
                put("essays", exportEssaysData(context))
            }

            // 创建导出文件
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Accelerator_Backup_$timestamp.Accele"
            val exportFile = File(context.getExternalFilesDir(null), fileName)

            exportFile.writeText(exportData.toString(2))

            Result.success(exportFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 导入应用数据
     */
    suspend fun importData(context: Context, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().readText()
            } ?: return@withContext Result.failure(Exception("无法读取文件"))

            val importData = JSONObject(jsonString)

            // 验证版本
            val version = importData.optString("version", "")
            if (version.isEmpty()) {
                return@withContext Result.failure(Exception("无效的备份文件"))
            }

            // 导入各模块数据
            if (importData.has("wordLearning")) {
                importWordLearningData(context, importData.getJSONObject("wordLearning"))
            }

            if (importData.has("settings")) {
                importSettingsData(context, importData.getJSONObject("settings"))
            }

            if (importData.has("statistics")) {
                importStatisticsData(context, importData.getJSONObject("statistics"))
            }

            if (importData.has("bookmarks")) {
                importBookmarksData(context, importData.getJSONObject("bookmarks"))
            }

            if (importData.has("essays")) {
                importEssaysData(context, importData.getJSONObject("essays"))
            }

            Result.success("数据导入成功")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== 导出模块 ==========

    private fun exportWordLearningData(context: Context): JSONObject {
        val data = JSONObject()

        // 从 WordLearningManager 获取数据
        val sharedPrefs = context.getSharedPreferences("word_learning", Context.MODE_PRIVATE)
        val allData = sharedPrefs.all

        data.put("memorizedWords", JSONObject(allData.filterKeys { it.startsWith("memorized_") }))
        data.put("unmemorizedWords", JSONObject(allData.filterKeys { it.startsWith("unmemorized_") }))
        data.put("wordProgress", JSONObject(allData.filterKeys { it.startsWith("progress_") }))

        return data
    }

    private fun exportSettingsData(context: Context): JSONObject {
        val data = JSONObject()

        // 从 DConfig 获取设置数据
        val sharedPrefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val allData = sharedPrefs.all

        allData.forEach { (key, value) ->
            data.put(key, value)
        }

        return data
    }

    private fun exportStatisticsData(context: Context): JSONObject {
        val data = JSONObject()

        // 统计数据
        val sharedPrefs = context.getSharedPreferences("statistics", Context.MODE_PRIVATE)
        val allData = sharedPrefs.all

        allData.forEach { (key, value) ->
            data.put(key, value)
        }

        return data
    }

    private fun exportBookmarksData(context: Context): JSONObject {
        val data = JSONObject()

        // 收藏数据
        val sharedPrefs = context.getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val allData = sharedPrefs.all

        allData.forEach { (key, value) ->
            data.put(key, value)
        }

        return data
    }

    private fun exportEssaysData(context: Context): JSONObject {
        val data = JSONObject()

        // 写作数据
        val sharedPrefs = context.getSharedPreferences("essays", Context.MODE_PRIVATE)
        val allData = sharedPrefs.all

        allData.forEach { (key, value) ->
            data.put(key, value)
        }

        return data
    }

    // ========== 导入模块 ==========

    private fun importWordLearningData(context: Context, data: JSONObject) {
        val sharedPrefs = context.getSharedPreferences("word_learning", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        // 导入已记住单词
        if (data.has("memorizedWords")) {
            val memorized = data.getJSONObject("memorizedWords")
            memorized.keys().forEach { key ->
                editor.putBoolean(key, memorized.getBoolean(key))
            }
        }

        // 导入未记住单词
        if (data.has("unmemorizedWords")) {
            val unmemorized = data.getJSONObject("unmemorizedWords")
            unmemorized.keys().forEach { key ->
                editor.putBoolean(key, unmemorized.getBoolean(key))
            }
        }

        // 导入进度数据
        if (data.has("wordProgress")) {
            val progress = data.getJSONObject("wordProgress")
            progress.keys().forEach { key ->
                when (val value = progress.get(key)) {
                    is Int -> editor.putInt(key, value)
                    is Long -> editor.putLong(key, value)
                    is Float -> editor.putFloat(key, value)
                    is String -> editor.putString(key, value)
                    is Boolean -> editor.putBoolean(key, value)
                }
            }
        }

        editor.apply()
    }

    private fun importSettingsData(context: Context, data: JSONObject) {
        val sharedPrefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        data.keys().forEach { key ->
            when (val value = data.get(key)) {
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is String -> editor.putString(key, value)
                is Boolean -> editor.putBoolean(key, value)
            }
        }

        editor.apply()
    }

    private fun importStatisticsData(context: Context, data: JSONObject) {
        val sharedPrefs = context.getSharedPreferences("statistics", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        data.keys().forEach { key ->
            when (val value = data.get(key)) {
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is String -> editor.putString(key, value)
                is Boolean -> editor.putBoolean(key, value)
            }
        }

        editor.apply()
    }

    private fun importBookmarksData(context: Context, data: JSONObject) {
        val sharedPrefs = context.getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        data.keys().forEach { key ->
            when (val value = data.get(key)) {
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is String -> editor.putString(key, value)
                is Boolean -> editor.putBoolean(key, value)
            }
        }

        editor.apply()
    }

    private fun importEssaysData(context: Context, data: JSONObject) {
        val sharedPrefs = context.getSharedPreferences("essays", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        data.keys().forEach { key ->
            when (val value = data.get(key)) {
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is String -> editor.putString(key, value)
                is Boolean -> editor.putBoolean(key, value)
            }
        }

        editor.apply()
    }
}
