package com.holland.graph_robot.domain

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property

@Node("ChinaArea")
data class ChinaAreaG(
    @Id val id: Int,
    @Property val pid: Int? = null,
    @Property val name: String? = null,
    @Property val extName: String? = null,
    @Property val fullName: String? = null
) {
}