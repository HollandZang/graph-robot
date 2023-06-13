package com.holland.graph_robot.filter

import org.lionsoul.ip2region.xdb.Searcher
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.io.IOException


@Order(100)
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
class IpFilter : WebFilter {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val searcher: Searcher

    init {
        val ip2regionXdb = this.javaClass.classLoader.getResource("ip2region.xdb")
        val dbPath = ip2regionXdb?.path
        val cBuff: ByteArray = try {
            Searcher.loadContentFromFile(dbPath)
        } catch (e: Exception) {
            throw RuntimeException("failed to load content from `$dbPath`", e)
        }
        searcher = try {
            Searcher.newWithBuffer(cBuff)
        } catch (e: Exception) {
            throw RuntimeException("failed to create content cached searcher", e)
        }

        Thread {
            log.info("Closing searcher instance")
            try {
                searcher.close()
            } catch (e: IOException) {
                log.error("Closing searcher instance error", e)
            }
        }
            .also {
                it.name = "SearcherShutdownHook"
                Runtime.getRuntime().addShutdownHook(it)
            }
    }

    private fun searchIP(ip: String): SearcherRes =
        try {
            val region = searcher.search(ip)
            SearcherRes.convert(region)
        } catch (e: Exception) {
            log.error("failed to search($ip)", e)
            SearcherRes.unknown()
        }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request

        var ip = ipHeaders
            .map { request.headers.getFirst(it) }
            .firstOrNull { !it.isNullOrEmpty() && "unknown" != it && "UNKNOWN" != it }
            ?: request.remoteAddress.toString()

        val s = ip.indexOf('/')
        if (s >= 0) ip = ip.substring(s + 1)
        val e = ip.indexOf(':')
        if (e >= 0) ip = ip.substring(0, e)

        exchange.attributes["ip"] = ip
        exchange.attributes["region"] = searchIP(ip)

        return chain.filter(exchange)
    }

    companion object {
        val ipHeaders = arrayOf(
            "x-forwarded-for",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        )
    }

    /**
     * 国家|区域|省份|城市|ISP(服务供应商)
     */
    class SearcherRes(
        val national: String,
        val region: String,
        val province: String,
        val city: String,
        val isp: String,
    ) {
        companion object {
            fun convert(search: String): SearcherRes {
                search.split("|").also {
                    return SearcherRes(it[0], it[1], it[2], it[3], it[4])
                }
            }

            fun unknown(): SearcherRes = SearcherRes("0", "0", "0", "0", "0")

        }

        override fun toString(): String = "$national|$region|$province|$city|$isp"
    }
}