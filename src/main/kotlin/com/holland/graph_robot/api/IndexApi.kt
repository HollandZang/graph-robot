package com.holland.graph_robot.api

import com.holland.graph_robot.mapper.graph.UserGMapper
import com.holland.graph_robot.mapper.relation.UserMapper
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
    private val messageSource: MessageSource? = null

    @Resource
    private val userMapper: UserMapper? = null

    @Resource
    private val userGMapper: UserGMapper? = null

    @Resource
    private val applicationContext: ConfigurableApplicationContext? = null

    @GetMapping
    fun index(exchange: ServerWebExchange): Mono<Any> {
//        val message = messageSource!!.getMessage(Messages.success, locale = exchange.getLocale())
//        return Mono.just(message)
//        val all1 = userMapper!!.all()
        val all = userGMapper!!.all()
        return Mono.just(all)
    }

}