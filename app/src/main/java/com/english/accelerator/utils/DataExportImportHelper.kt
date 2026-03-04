/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
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

                // 学习进度数据
                put("learningProgress", exportLearningProgressData(context))

                // 设置数据
                put("settings", exportSettingsData(context))

                // 统计数据
                put("statistics", exportStatisticsData(context))

                // 收藏数据
                put("bookmarks", exportBookmarksData(context))

                // 写作数据
                put("essays", exportEssaysData(context))

                // 模型配置
                put("modelConfig", exportModelConfigData(context))
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

            if (importData.has("learningProgress")) {
                importLearningProgressData(context, importData.getJSONObject("learningProgress"))
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

            if (importData.has("modelConfig")) {
                importModelConfigData(context, importData.getJSONObject("modelConfig"))
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
        val sharedPrefs = context.getSharedPreferences("word_learning_prefs", Context.MODE_PRIVATE)
        val allData = sharedPrefs.all

        allData.forEach { (key, value) ->
            when (value) {
                is String -> data.put(key, value)
                is Int -> data.put(key, value)
                is Long -> data.put(key, value)
                is Float -> data.put(key, value)
                is Boolean -> data.put(key, value)
            }
        }

        return data
    }

    private fun exportSettingsData(context: Context): JSONObject {
        val data = JSONObject()

        // 从 app_config 获取设置数据
        val sharedPrefs = context.getSharedPreferences("app_config", Context.MODE_PRIVATE)
        val allData = sharedPrefs.all

        allData.forEach { (key, value) ->
            when (value) {
                is String -> data.put(key, value)
                is Int -> data.put(key, value)
                is Long -> data.put(key, value)
                is Float -> data.put(key, value)
                is Boolean -> data.put(key, value)
            }
        }

        return data
    }

    private fun exportLearningProgressData(context: Context): JSONObject {
        val data = JSONObject()

        // 从 learning_progress_prefs 获取学习进度数据
        val sharedPrefs = context.getSharedPreferences("learning_progress_prefs", Context.MODE_PRIVATE)
        val allData = sharedPrefs.all

        allData.forEach { (key, value) ->
            when (value) {
                is String -> data.put(key, value)
                is Int -> data.put(key, value)
                is Long -> data.put(key, value)
                is Float -> data.put(key, value)
                is Boolean -> data.put(key, value)
            }
        }

        return data
    }

    private fun exportModelConfigData(context: Context): JSONObject {
        val data = JSONObject()

        // 从 model_config 获取模型配置数据
        val sharedPrefs = context.getSharedPreferences("model_config", Context.MODE_PRIVATE)
        val allData = sharedPrefs.all

        allData.forEach { (key, value) ->
            when (value) {
                is String -> data.put(key, value)
                is Int -> data.put(key, value)
                is Long -> data.put(key, value)
                is Float -> data.put(key, value)
                is Boolean -> data.put(key, value)
            }
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
        val sharedPrefs = context.getSharedPreferences("bookmark_prefs", Context.MODE_PRIVATE)
        val allData = sharedPrefs.all

        allData.forEach { (key, value) ->
            when (value) {
                is String -> data.put(key, value)
                is Int -> data.put(key, value)
                is Long -> data.put(key, value)
                is Float -> data.put(key, value)
                is Boolean -> data.put(key, value)
            }
        }

        return data
    }

    private fun exportEssaysData(context: Context): JSONObject {
        val data = JSONObject()

        // 写作数据
        val sharedPrefs = context.getSharedPreferences("essay_collection_prefs", Context.MODE_PRIVATE)
        val allData = sharedPrefs.all

        allData.forEach { (key, value) ->
            when (value) {
                is String -> data.put(key, value)
                is Int -> data.put(key, value)
                is Long -> data.put(key, value)
                is Float -> data.put(key, value)
                is Boolean -> data.put(key, value)
            }
        }

        return data
    }

    // ========== 导入模块 ==========

    private fun importWordLearningData(context: Context, data: JSONObject) {
        val sharedPrefs = context.getSharedPreferences("word_learning_prefs", Context.MODE_PRIVATE)
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

        editor.commit()
    }

    private fun importLearningProgressData(context: Context, data: JSONObject) {
        val sharedPrefs = context.getSharedPreferences("learning_progress_prefs", Context.MODE_PRIVATE)
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

        editor.commit()
    }

    private fun importSettingsData(context: Context, data: JSONObject) {
        val sharedPrefs = context.getSharedPreferences("app_config", Context.MODE_PRIVATE)
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

        editor.commit()
    }

    private fun importModelConfigData(context: Context, data: JSONObject) {
        val sharedPrefs = context.getSharedPreferences("model_config", Context.MODE_PRIVATE)
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

        editor.commit()
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

        editor.commit()
    }

    private fun importBookmarksData(context: Context, data: JSONObject) {
        val sharedPrefs = context.getSharedPreferences("bookmark_prefs", Context.MODE_PRIVATE)
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

        editor.commit()
    }

    private fun importEssaysData(context: Context, data: JSONObject) {
        val sharedPrefs = context.getSharedPreferences("essay_collection_prefs", Context.MODE_PRIVATE)
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

        editor.commit()
    }
}
