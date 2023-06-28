package com.holland.graph_robot.filter

import com.holland.graph_robot.enums.ReqHeaders
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*


@Order(1000)
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
class I18nFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val l = request.headers.getFirst(ReqHeaders.l.name) ?: DEF_LANGUAGE
        val c = request.headers.getFirst(ReqHeaders.c.name) ?: DEF_COUNTRY

        exchange.attributes["locale"] = Locale(l, c)
        return chain.filter(exchange)
    }

    companion object {
        const val DEF_LANGUAGE = "zh"
        const val DEF_COUNTRY = "CN"
    }
}