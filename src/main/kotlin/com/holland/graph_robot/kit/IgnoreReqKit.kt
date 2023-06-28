package com.holland.graph_robot.kit

import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest

class IgnoreReqKit private constructor(private val request: ServerHttpRequest) {
    private var ignored = false
    private var headers: HttpHeaders? = null
    private var uri: String? = null
    fun test(): Boolean {
        return ignored
    }

    // 过滤 OPTIONS
    fun checkOptions(): IgnoreReqKit {
        if (ignored) return this
        if ("OPTIONS" == request.method.name()) ignored = true
        return this
    }

    // 过滤admin模块健康日志
    fun checkActuator(): IgnoreReqKit {
        if (ignored) return this
        if (null == uri) uri = request.uri.path.replace("/+".toRegex(), "/")
        if (uri!!.startsWith("/actuator")) ignored = true
        return this
    }

    // 过滤 multipart/form-data
    fun checkMultipart(): IgnoreReqKit {
        if (ignored) return this
        if (null == headers) headers = request.headers
        val contentType = headers!!.getFirst("Content-Type")
        if (contentType != null && contentType.startsWith("multipart/form-data")) ignored = true
        return this
    }

    // 过滤 websocket
    fun checkWebsocket(): IgnoreReqKit {
        if (ignored) return this
        if (null == headers) headers = request.headers
        if ("websocket" == headers!!.getFirst("upgrade") || "Upgrade" == headers!!.getFirst("connection")) ignored =
            true
        return this
    }

    companion object {
        fun predicate(request: ServerHttpRequest): IgnoreReqKit {
            return IgnoreReqKit(request)
        }
    }
}
