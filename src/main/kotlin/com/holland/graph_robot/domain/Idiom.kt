package com.holland.graph_robot.domain

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.relational.core.mapping.Table

@Table
data class Idiom(
    @org.springframework.data.annotation.Id val word: String,
) {
}


@Node("Idiom")
data class IdiomG(
    @Id val word: String,
    @Relationship(type = "IdiomSolitaire") val nextWords: List<IdiomG>,
) {
}