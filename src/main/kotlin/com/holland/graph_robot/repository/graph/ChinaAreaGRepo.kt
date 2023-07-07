package com.holland.graph_robot.repository.graph

import com.holland.graph_robot.domain.ChinaAreaG
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import reactor.core.publisher.Mono


interface ChinaAreaGRepo : ReactiveNeo4jRepository<ChinaAreaG, Int> {
    fun findOneByName(name: String?): Mono<ChinaAreaG>
}