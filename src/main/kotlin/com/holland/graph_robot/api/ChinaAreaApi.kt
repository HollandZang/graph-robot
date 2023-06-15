package com.holland.graph_robot.api

import com.holland.graph_robot.mapper.graph.ChinaAreaGMapper
import com.holland.graph_robot.mapper.relation.ChinaAreaMapper
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RequestMapping
@RestController("/ChinaArea")
class ChinaAreaApi {
    @Resource
    private val chinaAreaMapper: ChinaAreaMapper? = null

    @Resource
    private val chinaAreaGMapper: ChinaAreaGMapper? = null

    @GetMapping("/listByIdPrefix")
    fun listByIdPrefix(idPrefix: Int): Mono<Any> {
        val list = chinaAreaMapper!!.listByIdPrefix(idPrefix)

        return Mono.just(list)
    }
}