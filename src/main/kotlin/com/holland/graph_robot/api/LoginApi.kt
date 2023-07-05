package com.holland.graph_robot.api

import com.holland.graph_robot.config.JWTSecurityContextRepository
import com.holland.graph_robot.config.JWTTypes
import com.holland.graph_robot.enums.Messages
import com.holland.graph_robot.kit.R
import com.holland.graph_robot.repository.relation.UserRepo
import jakarta.annotation.Resource
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@RequestMapping
@RestController
class LoginApi {

    @Resource
    private lateinit var userRepo: UserRepo

    @Resource
    private lateinit var passwordEncoder: PasswordEncoder

    @PostMapping("/login")
    fun login(exchange: ServerWebExchange, account: String, pwd: String): Mono<R<String>> {
//        val copyOfContextMap = MDC.getCopyOfContextMap()
        return userRepo.findByAccount(account)
            .flatMap { user ->
//                MDC.setContextMap(copyOfContextMap)
                if (passwordEncoder.matches(pwd, user.password)) {
                    userRepo.roles(user.loginName)
                        .buffer().single()
                        .map { roles ->
                            val jwt = JWTSecurityContextRepository.createJWT(
                                user.loginName,
                                user.username ?: user.loginName,
                                roles = roles
                            )
                            R.success(JWTTypes.`Bearer `.name + jwt)
                        }
                        .onErrorResume { R.failedMono(Messages.Not_found_roles, exchange) }
                } else {
                    R.failedMono(Messages.Account_or_pwd_is_not_correct, exchange)
                }
            }
            .switchIfEmpty { R.failedMono(Messages.Not_found_account, exchange) }
    }
}