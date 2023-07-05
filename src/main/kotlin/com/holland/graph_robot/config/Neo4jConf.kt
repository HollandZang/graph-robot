package com.holland.graph_robot.config

import io.r2dbc.spi.ConnectionFactoryOptions.*
import org.neo4j.cypherdsl.core.renderer.Configuration
import org.neo4j.cypherdsl.core.renderer.Dialect
import org.neo4j.driver.Driver
import org.springframework.context.annotation.Bean
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager
import org.springframework.data.neo4j.repository.config.ReactiveNeo4jRepositoryConfigurationExtension
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager


@Component
class Neo4jConf {

    @Bean
    fun cypherDslConfiguration(): Configuration? {
        return Configuration.newConfig()
            .withDialect(Dialect.NEO4J_5).build()
    }

    @Bean(ReactiveNeo4jRepositoryConfigurationExtension.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
    fun reactiveTransactionManager(
        driver: Driver,
        databaseNameProvider: ReactiveDatabaseSelectionProvider?
    ): ReactiveTransactionManager? {
        return ReactiveNeo4jTransactionManager(driver, databaseNameProvider!!)
    }

}