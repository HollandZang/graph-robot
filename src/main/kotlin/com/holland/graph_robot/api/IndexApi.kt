package com.holland.graph_robot.api

import com.alibaba.fastjson2.JSON
import com.holland.graph_robot.json_rpc2.Request
import com.holland.graph_robot.json_rpc2.Response
import com.holland.graph_robot.mapper.graph.UserGMapper
import com.holland.graph_robot.mapper.relation.UserMapper
import jakarta.annotation.Resource
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.MessageSource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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

    @PostMapping("/JSON-RPC 2.0")
    fun jsonRpc2(exchange: ServerWebExchange, @RequestBody content: String): ResponseEntity<*> {
        return if (JSON.isValidArray(content)) {
            val requests = JSON.parseArray(content, Request::class.java)
            ResponseEntity.ok("")
        } else if (JSON.isValidObject(content)) {
            val request = JSON.parseObject(content, Request::class.java)

            val (beanName, methodName) = request.method.split(".")
            val bean = applicationContext!!.getBean(beanName)
            val method = bean.javaClass.methods.first { it.name == methodName }

            val r = method.invoke(bean, request.params)
            val response = Response(request.jsonrpc, request.id, r, null)
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.ok()
                .body(Response("2.0", null, null, Response.RpcError(-32700, "Parse error")))
        }
    }
}