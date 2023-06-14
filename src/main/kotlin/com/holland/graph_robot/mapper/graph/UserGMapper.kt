package com.holland.graph_robot.mapper.graph

import com.baomidou.dynamic.datasource.annotation.DS
import com.holland.graph_robot.domain.User
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select


@DS("graph")
@Mapper
interface UserGMapper {
    @Select("MATCH (user:User) return user")
    fun all(): List<Map<String, User>>
}