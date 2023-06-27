package com.holland.graph_robot.config

import com.holland.graph_robot.enums.Times
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.util.*


@Component
class JWTSecurityContextRepository : ServerSecurityContextRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val anonymousAuthenticationToken =
        AnonymousAuthenticationToken("visitor", "visitor", listOf(SimpleGrantedAuthority("ROLE_VISITOR")))

    override fun save(serverWebExchange: ServerWebExchange, securityContext: SecurityContext): Mono<Void> {
        return Mono.empty()
    }

    override fun load(serverWebExchange: ServerWebExchange): Mono<SecurityContext> {
        val headers = serverWebExchange.request.headers

        val authentication = headers.getFirst("Authorization")
            ?.run {
                if (startsWith(JWTTypes.`Bearer `.name)) {
                    val token = substring(JWTTypes.`Bearer `.len)

                    val jwtParser = Jwts.parserBuilder()
                        .setSigningKey(KEY)
                        .build()

                    var jwtBody: Claims? = null
                    try {
                        val claimsJws = jwtParser.parseClaimsJws(token)
                        jwtBody = claimsJws.body
                    } catch (e: ExpiredJwtException) {
                        log.info("JWT 过期: {}", e.message)
                    } catch (e: SignatureException) {
                        log.error("JWT 签名不正确: {}", this, e)
                    } catch (e: IllegalArgumentException) {
                        log.warn("JWT 为空: {} {}", this, e.message)
                    } catch (e: Exception) {
                        log.error("JWT 异常: {}", this, e)
                    }

                    jwtBody
                        ?.let {
                            JWTAuthentication(
                                this,
                                true,
                                it["username"] as String,
                                it["account"] as String,
                                (it["pwd"] ?: "invisible") as String,
                                it["userinfo"],
                                (it["roles"] as String).split(",").map { s -> SimpleGrantedAuthority("ROLE_$s") }
                            )
                        }
                        ?: anonymousAuthenticationToken
                } else {
                    log.error("Not support yet! $this")
                    anonymousAuthenticationToken
                }
            }
            ?: anonymousAuthenticationToken

        return Mono.just(SecurityContextImpl(authentication))
    }

    companion object {
        private val KEY = "40cbf603-boyang-cd-b438-044d47abfb0b".toByteArray(StandardCharsets.UTF_8)
        private val secretKey = Keys.hmacShaKeyFor(KEY)

        fun createJWT(
            account: String,
            username: String = account,
            pwd: String = "invisible",
            userinfo: Map<String, *> = mapOf<String, Any>(),
            roles: Collection<String>,
        ): String {
            return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuedAt(Date())//设置签发时间
                .setExpiration(Date(System.currentTimeMillis() + Times.DAY_30.millSeconds))//设置过期时间

//                .setId(UUID.randomUUID().toString())//设置Id
//                .setSubject("auth")//设置主题

                //设置自定义信息
                .claim("account", account)
                .claim("username", username)
                .claim("userinfo", userinfo)
                .claim("roles", roles.joinToString(","))

                .signWith(secretKey)//设置签名
                .compact()//生成token字符串
        }
    }
}

class JWTAuthentication(
    private val authentication: String,
    private val authenticated: Boolean,
    private val name: String,
    private val principal: String,
    private val credentials: Any,
    private val details: Any?,
    private val authorities: Collection<GrantedAuthority>,
) : Authentication {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getCredentials(): Any {
        return credentials
    }

    override fun getDetails(): Any? {
        return details
    }

    override fun getPrincipal(): Any {
        return principal
    }

    override fun isAuthenticated(): Boolean {
        return authenticated
    }

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
    }

    override fun getName(): String {
        return name
    }
}

enum class JWTTypes(val len: Int, val desc: String) {
    `Basic `(6, "用于 http-basic 认证"),
    `Bearer `(7, "常见于 OAuth 和 JWT 授权"),
    `Digest `(7, "MD5 哈希的 http-basic 认证 (已弃用)"),
    ;
}