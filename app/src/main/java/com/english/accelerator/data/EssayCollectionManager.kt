/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import com.english.accelerator.utils.AutoBackupManager
import com.english.accelerator.utils.DataStateTracker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 作文收藏管理器
 * 负责管理用户收藏的作文，并持久化存储
 */
object EssayCollectionManager {
    private const val PREFS_NAME = "essay_collection_prefs"
    private const val KEY_ESSAYS = "collected_essays"

    private val collectedEssays = mutableStateListOf<Essay>()
    private var sharedPreferences: SharedPreferences? = null
    private var context: Context? = null
    private val gson = Gson()

    /**
     * 初始化
     */
    fun init(context: Context) {
        this.context = context
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFromPreferences()
    }

    /**
     * 从 SharedPreferences 加载收藏数据
     */
    private fun loadFromPreferences() {
        val json = sharedPreferences?.getString(KEY_ESSAYS, null) ?: return
        try {
            val type = object : TypeToken<List<Essay>>() {}.type
            val essays: List<Essay> = gson.fromJson(json, type)
            collectedEssays.clear()
            collectedEssays.addAll(essays)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 保存数据到 SharedPreferences
     */
    private fun saveToPreferences() {
        val json = gson.toJson(collectedEssays.toList())
        sharedPreferences?.edit()?.putString(KEY_ESSAYS, json)?.commit()

        // 更新状态追踪器并触发自动备份
        context?.let {
            DataStateTracker.updateState(DataStateTracker.DataType.ESSAYS)
            AutoBackupManager.autoBackup(it)
        }
    }

    /**
     * 添加作文到收藏库
     */
    fun addEssay(title: String, content: String, grammarScore: Int = 0): Essay {
        val wordCount = content.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        val essay = Essay(
            id = System.currentTimeMillis(),
            title = title.ifEmpty { "无标题" },
            content = content,
            grammarScore = grammarScore,
            wordCount = wordCount
        )
        collectedEssays.add(0, essay) // 添加到列表开头
        saveToPreferences()
        return essay
    }

    /**
     * 移除作文
     */
    fun removeEssay(essay: Essay) {
        collectedEssays.removeAll { it.id == essay.id }
        saveToPreferences()
    }

    /**
     * 更新作文
     */
    fun updateEssay(essay: Essay) {
        val index = collectedEssays.indexOfFirst { it.id == essay.id }
        if (index != -1) {
            collectedEssays[index] = essay
            saveToPreferences()
        }
    }

    /**
     * 获取所有收藏的作文
     */
    fun getCollectedEssays(): List<Essay> {
        return collectedEssays.toList()
    }

    /**
     * 根据 ID 获取作文
     */
    fun getEssayById(id: Long): Essay? {
        return collectedEssays.find { it.id == id }
    }

    /**
     * 清空所有收藏
     */
    fun clearAll() {
        collectedEssays.clear()
        saveToPreferences()
    }

    /**
     * 获取收藏数量
     */
    fun getCount(): Int {
        return collectedEssays.size
    }
}
