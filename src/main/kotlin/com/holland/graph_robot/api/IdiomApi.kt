package com.holland.graph_robot.api

import com.holland.graph_robot.domain.IdiomG
import com.holland.graph_robot.kit.FileKit
import com.holland.graph_robot.repository.graph.IdiomGRepo
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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