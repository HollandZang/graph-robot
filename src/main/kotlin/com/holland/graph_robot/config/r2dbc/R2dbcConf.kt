@file:Suppress("DEPRECATION")

package com.holland.graph_robot.config.r2dbc

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.dialect.R2dbcDialect
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.Parameter
import org.springframework.r2dbc.core.PreparedOperation
import org.springframework.stereotype.Component

@Component
class R2dbcConf(
    private val databaseClient: DatabaseClient,
    private val converter: R2dbcConverter
) {

    val dialect = DialectResolver.getDialect(this.databaseClient.connectionFactory)

    @Bean
    fun r2dbcEntityTemplate(r2dbcConverter: R2dbcConverter?): R2dbcEntityTemplate? {
        return R2dbcEntityTemplate(databaseClient, MyReactiveDataAccessStrategy(dialect, converter))
    }
}

@Suppress("UNCHECKED_CAST")
class MyReactiveDataAccessStrategy(
    dialect: R2dbcDialect,
    converter: R2dbcConverter
) : DefaultReactiveDataAccessStrategy(dialect, converter) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Deprecated("Deprecated in Java")
    override fun processNamedParameters(
        query: String,
        parameterProvider: ReactiveDataAccessStrategy.NamedParameterProvider
    ): PreparedOperation<*> {
        val expanded = super.processNamedParameters(query, parameterProvider)

        if (log.isDebugEnabled) {
            val fieldParameterSource = expanded.javaClass.getDeclaredField("parameterSource")
            fieldParameterSource.isAccessible = true
            val parameterSource = fieldParameterSource.get(expanded)
            val fieldValues = parameterSource.javaClass.getDeclaredField("values")
            fieldValues.isAccessible = true
            val params = fieldValues.get(parameterSource) as Map<String, Parameter>

            val regex = Regex(":[\\w_]+")
            val execSql = regex.replace(query) {
                val parameter = params[it.value.substring(1)]
                when (val value = parameter?.value ?: "null") {
                    is CharSequence -> "'$value'"
                    is Number -> value.toString()
                    // TODO: 后续类型自行维护
                    else -> "'$value'"
                }
            }

            log.debug("$query -- $execSql")
        }

        return expanded
    }
}