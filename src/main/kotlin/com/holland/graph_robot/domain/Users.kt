package com.holland.graph_robot.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table
data class User(
    @Id val id: Int,
    val loginName: String,
    val password: String? = null,
    val username: String? = null,
    val createTime: LocalDateTime? = null,
    val updateTime: LocalDateTime? = null,
) {

}