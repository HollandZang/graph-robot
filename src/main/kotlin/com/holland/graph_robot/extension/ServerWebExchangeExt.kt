package com.holland.graph_robot.extension

import com.holland.graph_robot.filter.IpFilter
import org.springframework.web.server.ServerWebExchange
import java.util.*

object ServerWebExchangeExt {
    @JvmName("ServerWebExchange")
    fun ServerWebExchange.getLocale() =
        (this.attributes["locale"] ?: Locale.CHINA) as Locale

    @JvmName("ServerWebExchange")
    fun ServerWebExchange.getIp() =
        (this.attributes["ip"] ?: "unknown") as String

    @JvmName("ServerWebExchange")
    fun ServerWebExchange.getRegion() =
        (this.attributes["region"] ?: IpFilter.SearcherRes.unknown()) as IpFilter.SearcherRes
}