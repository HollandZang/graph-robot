package com.holland.graph_robot.mapper.relation

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface PersonMapper {
    @Select("SELECT t.* FROM `spring-cloud_gateway`.user t")
    fun all(): List<Map<String, Any>>
}