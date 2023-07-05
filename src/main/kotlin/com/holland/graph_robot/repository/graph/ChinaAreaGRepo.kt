package com.holland.graph_robot.repository.graph

import com.holland.graph_robot.domain.ChinaAreaG
import reactor.core.publisher.Mono


interface ChinaAreaGRepo : org.springframework.data.neo4j.repository.ReactiveNeo4jRepository<ChinaAreaG, Int> {
    fun findOneByName(name: String?): Mono<ChinaAreaG>
}