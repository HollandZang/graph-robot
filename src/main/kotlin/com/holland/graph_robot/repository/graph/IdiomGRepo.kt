package com.holland.graph_robot.repository.graph

import com.holland.graph_robot.domain.IdiomG
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IdiomGRepo : ReactiveNeo4jRepository<IdiomG, String> {
    @Query("MATCH (a:Idiom {word:\$word}) with a match (b:Idiom {word:\$toWord}) WHERE a <> b create (a)-[r:IdiomSolitaire]->(b)")
    fun createIdiomSolitaire(word: String, toWord: String): Mono<Any>

    @Query("MATCH (a:Idiom {word:\$source}) with a match (b:Idiom {word:\$target}),p=shortestpath((a)-[r:IdiomSolitaire*..20]->(b)) return nodes(p)")
    fun shortestIdiomSolitaireNodes(source: String, target: String, maxPath: Int = 20): Flux<IdiomG>
}