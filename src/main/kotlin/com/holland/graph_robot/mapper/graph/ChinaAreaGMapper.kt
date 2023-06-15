package com.holland.graph_robot.mapper.graph

import com.baomidou.dynamic.datasource.annotation.DS
import com.holland.graph_robot.domain.ChinaArea
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper


@DS("graph")
@Mapper
interface ChinaAreaGMapper {
    @Insert("CREATE (n:ChinaArea {id: #{id}, pid: #{pid}, name: #{name}, extName: #{extName}, fullName: #{fullName} })")
    fun createNode(chinaArea: ChinaArea)
}