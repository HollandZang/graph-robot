package com.holland.graph_robot.config

import com.holland.graph_robot.kit.IgnoreReqKit
import jakarta.annotation.Resource
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.security.authentication.*
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.SecurityContextServerWebExchange
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import java.nio.charset.StandardCharsets


@EnableReactiveMethodSecurity
@Component
class SpringSecurityConfig {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val traceId = "traceId"

    @Resource
    private val jwtSecurityContextRepository: JWTSecurityContextRepository? = null

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(8)

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        return http
            .securityContextRepository(jwtSecurityContextRepository)
            .authorizeExchange {
                it.pathMatchers("/login").permitAll()
                    .pathMatchers("/actuator/**").hasRole("ADMIN")
                    .anyExchange().permitAll()
            }
            .addFilterAt(firstFilter(), SecurityWebFiltersOrder.FIRST)
            .addFilterAt(logFilter(), SecurityWebFiltersOrder.LAST)
            .csrf { it.disable() }
            .build()
    }

    private fun firstFilter(): WebFilter {
        return WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
            val request = exchange.request
            MDC.put(traceId, request.id)
            Schedulers.onScheduleHook("mdc") { runnable: Runnable ->
                val map = MDC.getCopyOfContextMap()
                Runnable {
                    if (map != null) MDC.setContextMap(map)
                    try {
                        runnable.run()
                    } finally {
                        MDC.clear()
                    }
                }
            }
            chain.filter(exchange)
        }
    }

    private fun logFilter(): WebFilter {
        return WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
            val request = exchange.request
            if (IgnoreReqKit.predicate(request)
                    .checkOptions()
                    .checkActuator()
                    .checkWebsocket()
                    .checkMultipart()
                    .test()
            ) {
                return@WebFilter chain.filter(exchange)
            }

            return@WebFilter ReactiveSecurityContextHolder.getContext()
                .flatMap { securityContext ->
                    val authentication = securityContext.authentication
                    DataBufferUtils.join(request.body)
                        .map { reqDataBuffer: DataBuffer -> reqDataBuffer.toString(StandardCharsets.UTF_8) }
                        .map { s: String -> s.replace("\r\n|\n".toRegex(), "") }
                        .switchIfEmpty { Mono.just("nil") }
                        .flatMap { requestBody ->
                            val path = request.uri.path
                            val query: String? = request.uri.query
                            val realPath = if (query == null) path else "$path?$query"

                            val payload = when (authentication) {
                                is JWTAuthentication -> authentication.toString()
                                is AnonymousAuthenticationToken -> "anonymous"
                                else -> "undefined ${authentication.name}: ${authentication.principal}(${authentication.name})"
                            }

                            log.info("{} {} payload[{}] body[{}]", request.method, realPath, payload, requestBody)

                            val startTime = System.currentTimeMillis()
                            val originalResponse = exchange.response
                            val decoratedResponse =
                                object : ServerHttpResponseDecorator(originalResponse) {
                                    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
                                        //输出返回结果
                                        if (body is Flux<*> || body is Mono<*>) {
                                            return super.writeWith(
                                                DataBufferUtils.join(body)
                                                    .doOnNext { dataBuffer: DataBuffer ->
                                                        MDC.put(traceId, request.id)
                                                        val respBody = dataBuffer.toString(StandardCharsets.UTF_8)
                                                        val statusCode = originalResponse.statusCode
                                                        val result = statusCode?.value() ?: -1
                                                        val cost =
                                                            (System.currentTimeMillis() - startTime).toDouble() / 1000
                                                        log.info("({}s) {} body[{}]", cost, result, respBody)
                                                    }
                                            )
                                        }
                                        return super.writeWith(body)
                                    }
                                }

//                            val decoratedRequest = object : ServerHttpRequestDecorator(exchange.request) {
//                                override fun getBody(): Flux<DataBuffer> {
//                                    val nettyDataBufferFactory = NettyDataBufferFactory(UnpooledByteBufAllocator(false))
//                                    val bodyDataBuffer =
//                                        nettyDataBufferFactory.wrap(requestBody.toByteArray())
//                                    return Flux.just(bodyDataBuffer)
//                                }
//                            }

                            val decoratedExchange = object :
                                SecurityContextServerWebExchange(exchange, Mono.just(securityContext)) {
                                override fun getFormData(): Mono<MultiValueMap<String, String>> {
                                    val linkedMultiValueMap = LinkedMultiValueMap<String, String>()
                                    requestBody.split("&")
                                        .forEach {
                                            val (k, v) = it.split("=")
                                            linkedMultiValueMap.add(k, v)
                                        }
                                    return Mono.just(linkedMultiValueMap)
                                }
                            }

                            chain.filter(
//                                decoratedExchange.mutate().request(decoratedRequest).response(decoratedResponse).build()
                                decoratedExchange.mutate().response(decoratedResponse).build()
                            )
                        }
                }
        }
    }
}