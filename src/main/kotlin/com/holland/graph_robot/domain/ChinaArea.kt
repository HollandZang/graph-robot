package com.holland.graph_robot.domain

data class ChinaArea(
    val id: Int,
    val pid: Int? = null,
    val deep: Int? = null,
    val name: String? = null,
    val pinyinPrefix: String? = null,
    val pinyinPrefix2: String? = null,
    val pinyin: String? = null,
    val extId: String? = null,
    val extName: String? = null,
    val fullName: String? = null
) {
}