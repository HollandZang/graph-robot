package com.holland.graph_robot.api

import com.holland.graph_robot.config.JWTSecurityContextRepository
import com.holland.graph_robot.config.JWTTypes
import com.holland.graph_robot.mapper.relation.UserMapper
import jakarta.annotation.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RequestMapping
@RestController
class LoginApi {

    @Resource
    private val userMapper: UserMapper? = null

    @Resource
    private val passwordEncoder: PasswordEncoder? = null

    @PostMapping("/login")
    fun login(account: String, pwd: String): Mono<ResponseEntity<Any>> {
        val userOptional = userMapper!!.findByAccount(account)
        if (userOptional.isEmpty) {
            return Mono.defer { Mono.just("账号不存在").map { ResponseEntity.status(HttpStatus.GONE).body(it) } }
        } else {
            val user = userOptional.get()

            return if (passwordEncoder!!.matches(pwd, user.password)) {
                Mono.defer {
                    val jwt = JWTSecurityContextRepository.createJWT(
                        user.loginName!!,
                        user.username!!,
                        roles = userMapper.roles(user.loginName)
                    )
                    Mono.just(JWTTypes.`Bearer `.name + jwt).map { ResponseEntity.ok().body(it) }
                }
            } else {
                Mono.defer {
                    Mono.just("账号或密码不正确").map { ResponseEntity.status(HttpStatus.BAD_REQUEST).body(it) }
                }
            }
        }
    }
}