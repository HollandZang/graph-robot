package com.holland.graph_robot.mapper.graph

import com.baomidou.dynamic.datasource.annotation.DS
import com.holland.graph_robot.domain.ChinaArea
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select


@DS("graph")
@Mapper
interface ChinaAreaGMapper {
    @Insert("CREATE (n:ChinaArea {id: #{id}, pid: #{pid}, name: #{name}, extName: #{extName}, fullName: #{fullName} })")
    fun createNode(chinaArea: ChinaArea)

    fun createNodes(vararg list: ChinaArea)

    @Insert("MATCH (a:ChinaArea),(b:ChinaArea) where a.id=#{nodeA.id} and b.id=#{nodeB.id} create (a)-[r:\${relationship}]->(b) create (b)-[_r:\${relationship}]->(a)")
    fun createBothRelationship(relationship: String, nodeA: ChinaArea, nodeB: ChinaArea)

    @Insert("MATCH p=()-[r:\${relationship}]->() DELETE r")
    fun delRelationship(relationship: String)

    @Select("MATCH p=(a)-[r:adjoin]->(b) where a.id=#{id} return b.id as id, b.pid as pid, b.name as name,b.fullName as fullName, b.extName as extName")
    fun findAdjoinChinaAreas(nodeA: ChinaArea): List<ChinaArea>
}