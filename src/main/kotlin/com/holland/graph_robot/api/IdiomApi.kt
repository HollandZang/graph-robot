package com.holland.graph_robot.api

import com.holland.graph_robot.domain.IdiomG
import com.holland.graph_robot.repository.graph.IdiomGRepo
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File

@RequestMapping("/idiom")
@RestController
class IdiomApi {

    @Resource
    private lateinit var idiomGRepo: IdiomGRepo

    //    @PostConstruct
    fun init() {
        val readLines = File("/Users/holland/repo/holland/graph-robot/data/THUOCL_chengyu.txt").readLines()

        idiomGRepo.saveAll(readLines.map { IdiomG(it, listOf()) })
            .subscribe()

        // TODO: 导入速度很慢，需要改成分批处理+批量插入
        Flux.just(*readLines.toTypedArray())
            .flatMap { word ->
                println("当前word: $word")
                val last = word.last()

                Flux.just(*readLines.toTypedArray())
                    .filter { next -> next.first() == last }
                    .flatMap { next -> idiomGRepo.createIdiomSolitaire(word, next) }
            }
            .subscribe()
    }

    @GetMapping("/search")
    fun index(source: String, target: String): Mono<String> {
        if (source == target) return Mono.just("$source -> $target")

        return idiomGRepo.shortestIdiomSolitaireNodes(source, target)
            .collectList()
            .map { list ->
                if (list.isEmpty())
                    "[$source]无法到达[$target]"
                else
                    list.joinToString(" -> ") { it.word }
            }
    }
}