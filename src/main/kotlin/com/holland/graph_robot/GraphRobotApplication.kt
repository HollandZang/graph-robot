package com.holland.graph_robot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication


@SpringBootApplication(exclude = [ReactiveUserDetailsServiceAutoConfiguration::class])
class GraphRobotApplication

fun main(args: Array<String>) {
    runApplication<GraphRobotApplication>(*args)
}
