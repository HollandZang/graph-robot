package com.holland.graph_robot.api

import com.holland.graph_robot.domain.User
import com.holland.graph_robot.repository.graph.ChinaAreaGRepo
import com.holland.graph_robot.repository.relation.UserRepo
import jakarta.annotation.Resource
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.MessageSource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@RequestMapping
@RestController
class IndexApi {

    @Resource
    private lateinit var messageSource: MessageSource

    @Resource
    private lateinit var applicationContext: ConfigurableApplicationContext

    @Resource
    private lateinit var userRepo: UserRepo

    @Resource
    private lateinit var chinaAreaGRepo: ChinaAreaGRepo

    @GetMapping
    fun index(exchange: ServerWebExchange): Mono<User> {
//        val message = messageSource.getMessage(Messages.success, locale = exchange.getLocale())
//        return Mono.just(message)

        return userRepo.findByAccount("hollandX")
//        return chinaAreaGRepo.findOneByName("四川")
    }

}