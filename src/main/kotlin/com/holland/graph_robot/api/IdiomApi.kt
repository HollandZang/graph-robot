package com.holland.graph_robot.api

import com.holland.graph_robot.domain.IdiomG
import com.holland.graph_robot.kit.FileKit
import com.holland.graph_robot.repository.graph.IdiomGRepo
import com.holland.graph_robot.repository.relation.IdiomRepo
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.io.File

@RequestMapping("/idiom")
@RestController
class IdiomApi {

    @Resource
    private lateinit var idiomRepo: IdiomRepo

    @Resource
    private lateinit var idiomGRepo: IdiomGRepo

    //        @PostConstruct
    fun init() {
        val readLines = File("/Users/holland/repo/holland/graph-robot/data/THUOCL_chengyu.txt").readLines()

        idiomGRepo.saveAll(readLines.map { IdiomG(it, listOf()) })
            .subscribe()

        val head = "word_l,word_r"
        FileKit.newFile(head, "./data", "IdiomSolitaire.csv")
        readLines.forEach { word ->
            val last = word.last()

            val lines = readLines.filter { next -> next.first() == last }
                .joinToString("\n") { next -> "$word,$next" }
            if (lines == "") return@forEach
            FileKit.append2File(lines, "./data", "IdiomSolitaire.csv")
        }
        // load csv with headers from "file:/IdiomSolitaire.csv" as line match (from:Idiom {word: line.word_l}),(to:Idiom {word:line.word_r}) create (from)-[r:IdiomSolitaire]->(to)
    }

    @GetMapping("/search")
    fun search(source: String, target: String): Mono<String> {
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

    @GetMapping("/next")
    fun next(source: String, @RequestParam(defaultValue = "10") limit: Int): Mono<String> {
        return source.trim().run {
            idiomGRepo.listNextIdiomSolitaireNodes(source, limit)
                .collectList()
                .flatMap { list ->
                    if (list.isEmpty()) {
                        val last = source.trimEnd().last()
                        val findAll = idiomRepo.list(last.toString(), limit)
                        findAll.collectList()
                            .map {
                                if (it.isEmpty()) "nothing"
                                else it.joinToString(", ") { idiom -> idiom.word }
                            }
                    } else
                        Mono.just(list.joinToString(", "))
                }
        }
    }
}