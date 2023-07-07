package com.holland.graph_robot.repository.relation

import com.holland.graph_robot.domain.Idiom
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface IdiomRepo : R2dbcRepository<Idiom, String> {
}