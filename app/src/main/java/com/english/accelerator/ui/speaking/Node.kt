package com.english.accelerator.ui.speaking

import androidx.compose.runtime.Composable

/**
 * 节点接口 - 所有 UI 组件的基础
 */
interface Node {
    val id: String

    @Composable
    fun Render()

    fun onAttach() {}
    fun onDetach() {}
}

/**
 * 节点管理器接口
 */
interface NodeManager {
    fun register(node: Node)
    fun unregister(nodeId: String)
    fun getNode(nodeId: String): Node?
    fun getAllNodes(): List<Node>
}
