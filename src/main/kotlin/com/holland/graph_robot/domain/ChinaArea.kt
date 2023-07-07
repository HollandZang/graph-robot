package com.holland.graph_robot.domain

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property

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

@Node("ChinaArea")
data class ChinaAreaG(
    @Id val id: Int,
    @Property val pid: Int? = null,
    @Property val name: String? = null,
    @Property val extName: String? = null,
    @Property val fullName: String? = null
) {
}