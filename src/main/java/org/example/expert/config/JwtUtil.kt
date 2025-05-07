package org.example.expert.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import lombok.extern.slf4j.Slf4j
import org.example.expert.domain.common.exception.ServerException
import org.example.expert.domain.user.enums.UserRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.security.Key
import java.util.*

@Component
class JwtUtil {
    @Value("\${jwt.secret.key}")
    private lateinit var secretKey: String

    private lateinit var key: Key
    private val signatureAlgorithm = SignatureAlgorithm.HS256

    @PostConstruct
    fun init() {
        val bytes = Base64.getDecoder().decode(secretKey)
        key = Keys.hmacShaKeyFor(bytes)
    }

    fun createToken(userId: Long, email: String, nickname: String, userRole: UserRole): String {
        val date = Date()

        return BEARER_PREFIX +
                Jwts.builder()
                    .setSubject(userId.toString())
                    .claim("email", email)
                    .claim("nickname", nickname)
                    .claim("userRole", userRole)
                    .setExpiration(Date(date.time + TOKEN_TIME))
                    .setIssuedAt(date) // 발급일
                    .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                    .compact()
    }

    fun substringToken(tokenValue: String): String {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7)
        }
        throw ServerException("Not Found Token")
    }

    fun extractClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val TOKEN_TIME = 60 * 60 * 1000L // 60분
    }
}
