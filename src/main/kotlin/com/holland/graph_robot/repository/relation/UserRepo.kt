package com.holland.graph_robot.repository.relation

import com.holland.graph_robot.domain.User
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface UserRepo : R2dbcRepository<User, Int> {
    @Query("SELECT * FROM user where login_name = :loginName")
    fun findByAccount(loginName: String): Mono<User>

    @Query("SELECT roles FROM user_role where login_name = :loginName")
    fun roles(loginName: String): Flux<String>
}