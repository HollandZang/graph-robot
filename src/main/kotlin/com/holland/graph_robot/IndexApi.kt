package com.holland.graph_robot

import com.alibaba.fastjson2.JSON
import com.holland.graph_robot.mapper.graph.UserGMapper
import com.holland.graph_robot.mapper.relation.UserMapper
import jakarta.annotation.Resource
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
    val messageSource: MessageSource? = null

    @Resource
    val userMapper: UserMapper? = null

    @Resource
    val userGMapper: UserGMapper? = null

    @GetMapping
    fun index(exchange: ServerWebExchange): Mono<Any> {
//        val message = messageSource!!.getMessage(Messages.success, locale = exchange.getLocale())
//        return Mono.just(message)
//        val all1 = userMapper!!.all()
        val all = userGMapper!!.all()
        return Mono.just(all)
    }
}