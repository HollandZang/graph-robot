package com.holland.graph_robot.repository.relation

import com.holland.graph_robot.domain.ChinaArea
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface ChinaAreaRepo : R2dbcRepository<ChinaArea, Int> {
}