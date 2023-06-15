package com.holland.graph_robot.mapper.relation

import com.holland.graph_robot.domain.ChinaArea
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface ChinaAreaMapper {
    @Select("select * from china_area where id like concat(#{idPrefix},'%')")
    fun listByIdPrefix(idPrefix: Int): List<ChinaArea>
}