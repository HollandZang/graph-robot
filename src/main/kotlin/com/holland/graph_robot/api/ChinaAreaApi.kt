package com.holland.graph_robot.api

import com.holland.graph_robot.domain.ChinaArea
import com.holland.graph_robot.mapper.graph.ChinaAreaGMapper
import com.holland.graph_robot.mapper.relation.ChinaAreaMapper
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
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
        chinaAreaGMapper!!.createNodes(*list.toTypedArray())

        return Mono.just(list)
    }

    @PostMapping("/createBothRelationship")
    fun createBothRelationship(): Mono<Any> {
        chinaAreaGMapper!!.createBothRelationship(
            "test",
            ChinaArea(510116),
            ChinaArea(510118)
        )

        return Mono.just("OK")
    }

    @GetMapping("/findAdjoinChinaAreas")
    fun findAdjoinChinaAreas(id: Int): Mono<Any> {
        val chinaAreas = chinaAreaGMapper!!.findAdjoinChinaAreas(ChinaArea(id))

        return Mono.just(chinaAreas)
    }
}