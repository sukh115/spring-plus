package org.example.expert.config

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.user.enums.UserRole
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.List

@Slf4j
class JwtAuthenticationFilter(val jwtUtil: JwtUtil) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val uri = httpRequest.requestURI

        if (uri.startsWith("/auth") || uri == "/health") {
            filterChain.doFilter(httpRequest, httpResponse)
            return
        }

        val bearerToken = httpRequest.getHeader("Authorization")
        if (bearerToken.isNullOrBlank()) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.")
            return
        }

        val token = try {
            jwtUtil.substringToken(bearerToken)
        } catch (e: Exception) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "토큰 파싱에 실패했습니다.")
            return
        }

        try {
            val claims = jwtUtil.extractClaims(token)

            val userId = claims.subject.toLong()
            val email = claims["email", String::class.java]
            val nickname = claims["nickname", String::class.java]
            val role = UserRole.valueOf(claims["userRole", String::class.java])

            val authUser = AuthUser(userId, email, nickname, role)
            val auth = UsernamePasswordAuthenticationToken(
                authUser,
                null,
                listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
            )

            SecurityContextHolder.getContext().authentication = auth
            filterChain.doFilter(httpRequest, httpResponse)

        } catch (e: SecurityException) {
            log.error("Invalid JWT signature", e)
            respondUnauthorized(httpResponse, "유효하지 않는 JWT 서명입니다.")
        } catch (e: MalformedJwtException) {
            log.error("Malformed JWT token", e)
            respondUnauthorized(httpResponse, "잘못된 JWT 형식입니다.")
        } catch (e: ExpiredJwtException) {
            log.error("Expired JWT token", e)
            respondUnauthorized(httpResponse, "만료된 JWT 토큰입니다.")
        } catch (e: UnsupportedJwtException) {
            log.error("Unsupported JWT token", e)
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.")
        } catch (e: Exception) {
            log.error("Unexpected JWT error", e)
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        }
    }

    private fun respondUnauthorized(response: HttpServletResponse, message: String) {
        SecurityContextHolder.clearContext()
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message)
    }
}
