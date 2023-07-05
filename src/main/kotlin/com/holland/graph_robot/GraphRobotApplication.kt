package com.holland.graph_robot

import com.holland.graph_robot.config.Context
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@EnableR2dbcRepositories(basePackages = ["com.holland.graph_robot.repository.relation"])
@EnableReactiveNeo4jRepositories(basePackages = ["com.holland.graph_robot.repository.graph"])
@SpringBootApplication(exclude = [ReactiveUserDetailsServiceAutoConfiguration::class])
class GraphRobotApplication

fun main(args: Array<String>) {
    val context = runApplication<GraphRobotApplication>(*args)
    Context.configurableApplicationContext = context
}
