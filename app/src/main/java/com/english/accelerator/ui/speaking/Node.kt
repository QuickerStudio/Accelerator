package com.english.accelerator.ui.speaking

import androidx.compose.runtime.Composable

/**
 * Node 接口 - Speaking 模块的基础组件接口
 */
interface Node {
    val id: String

    @Composable
    fun Render()
}
