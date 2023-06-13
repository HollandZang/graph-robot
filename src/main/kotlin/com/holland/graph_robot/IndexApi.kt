package com.holland.graph_robot

import com.holland.graph_robot.mapper.graph.PersonGMapper
import com.holland.graph_robot.mapper.relation.PersonMapper
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
    val personMapper: PersonMapper? = null

    @Resource
    val personGMapper: PersonGMapper? = null

    @GetMapping
    fun index(exchange: ServerWebExchange): Mono<Any> {
//        val message = messageSource!!.getMessage(Messages.success, locale = exchange.getLocale())
//        return Mono.just(message)
        val all1 = personMapper!!.all()
        val all = personGMapper!!.all()
        return Mono.just(all)
    }
}