package com.holland.graph_robot.mapper.relation

import com.holland.graph_robot.domain.User
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface UserMapper {
    @Select("SELECT t.* FROM `spring-cloud_gateway`.user t")
    fun all(): List<User>
}