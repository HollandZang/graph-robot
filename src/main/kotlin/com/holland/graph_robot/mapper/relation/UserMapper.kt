package com.holland.graph_robot.mapper.relation

import com.holland.graph_robot.domain.User
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import java.util.*

@Mapper
interface UserMapper {
    @Select("SELECT t.* FROM user t")
    fun all(): List<User>

    @Select("SELECT t.* FROM user t where login_name = #{account}")
    fun findByAccount(loginName: String): Optional<User>

    @Select("SELECT t.roles FROM user_role t where login_name = #{account}")
    fun roles(loginName: String): Set<String>
}