package com.holland.graph_robot.mapper.graph

import com.baomidou.dynamic.datasource.annotation.DS
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select


@DS("graph")
@Mapper
interface PersonGMapper {
    @Select("MATCH (person:Person) return person")
    fun all(): List<Map<String, Any>>
}