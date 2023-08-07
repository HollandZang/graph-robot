package com.holland.graph_robot.repository.relation

import com.holland.graph_robot.domain.Idiom
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface IdiomRepo : R2dbcRepository<Idiom, String> {
    @Query("select * from idiom where word like concat(:firstWord, '%') limit :limit")
    fun list(firstWord: String, limit: Int = 10): Flux<Idiom>
}