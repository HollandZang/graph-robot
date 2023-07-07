package com.holland.graph_robot.api

import com.holland.graph_robot.enums.Messages
import com.holland.graph_robot.extension.I18nExt.getMessage
import com.holland.graph_robot.extension.ServerWebExchangeExt.getLocale
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
    private lateinit var messageSource: MessageSource

    @GetMapping
    fun index(exchange: ServerWebExchange): Mono<String> {
        val message = messageSource.getMessage(Messages.success, locale = exchange.getLocale())
        return Mono.just(message)
    }

}