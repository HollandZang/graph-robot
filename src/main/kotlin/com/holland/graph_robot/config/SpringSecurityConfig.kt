package com.holland.graph_robot.config

import jakarta.annotation.Resource
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.*
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.stereotype.Component


@EnableReactiveMethodSecurity
@Component
class SpringSecurityConfig {

    @Resource
    private val jwtSecurityContextRepository: JWTSecurityContextRepository? = null

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(8)

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        return http
            .securityContextRepository(jwtSecurityContextRepository)
            .authorizeExchange {
                it.pathMatchers("/login").permitAll()
                    .pathMatchers("/actuator/**").hasRole("ADMIN")
                    .anyExchange().authenticated()
            }
            .csrf { it.disable() }
            .build()
    }
}