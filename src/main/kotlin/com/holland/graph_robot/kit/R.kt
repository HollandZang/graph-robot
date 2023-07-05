package com.holland.graph_robot.kit

import com.holland.graph_robot.config.Context
import com.holland.graph_robot.enums.Messages
import com.holland.graph_robot.extension.I18nExt.getMessage
import com.holland.graph_robot.extension.ServerWebExchangeExt.getLocale
import org.springframework.context.MessageSource
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

data class R<DATA>(
    val code: Int,
    val msg: String,
    val data: DATA?,
) {
    companion object {
        private lateinit var messageSource: MessageSource
        private fun getMessageSource(): MessageSource {
            if (::messageSource.isInitialized.not())
                messageSource = Context.configurableApplicationContext.getBean(MessageSource::class.java)
            return messageSource
        }

        fun <DATA> success(data: DATA) = R(200, "", data)

        fun <DATA> failed(msg: String) = R<DATA>(400, msg, null)
        fun <DATA> failedMono(msg: String) = Mono.just(R<DATA>(400, msg, null))
        fun <DATA> failedMono(messages: Messages, locale: Locale): Mono<R<DATA>> {
            val msg = getMessageSource().getMessage(messages, locale = locale)
            return Mono.just(R(400, msg, null))
        }

        fun <DATA> failedMono(messages: Messages, exchange: ServerWebExchange) =
            failedMono<DATA>(messages, exchange.getLocale())


        fun <DATA> error(msg: String) = R<DATA>(500, msg, null)
        fun <DATA> errorMono(msg: String) = Mono.just(R<DATA>(500, msg, null))
    }
}