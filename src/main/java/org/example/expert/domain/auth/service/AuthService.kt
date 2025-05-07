package org.example.expert.domain.auth.service

import org.example.expert.config.JwtUtil
import org.example.expert.domain.auth.dto.request.SigninRequest
import org.example.expert.domain.auth.dto.request.SignupRequest
import org.example.expert.domain.auth.dto.response.SigninResponse
import org.example.expert.domain.auth.dto.response.SignupResponse
import org.example.expert.domain.auth.exception.AuthException
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.user.entity.User
import org.example.expert.domain.user.enums.UserRole.Companion.of
import org.example.expert.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val jwtUtil: JwtUtil
) {
    @Transactional
    fun signup(signupRequest: SignupRequest): SignupResponse {
        if (userRepository.existsByEmail(signupRequest.email)) {
            throw InvalidRequestException("이미 존재하는 이메일입니다.")
        }

        val encodedPassword = passwordEncoder.encode(signupRequest.password)

        val userRole = of(signupRequest.userRole)

        val newUser = User(
            signupRequest.email,
            encodedPassword,
            signupRequest.nickname,
            userRole
        )
        val savedUser = userRepository.save(newUser)

        val bearerToken = jwtUtil.createToken(savedUser.id!!, savedUser.email, savedUser.nickname, userRole)

        return SignupResponse(bearerToken)
    }

    fun signin(signinRequest: SigninRequest): SigninResponse {
        val user = userRepository.findByEmail(signinRequest.email).orElseThrow {
            InvalidRequestException(
                "가입되지 않은 유저입니다."
            )
        }

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(signinRequest.password, user.password)) {
            throw AuthException("잘못된 비밀번호입니다.")
        }

        val bearerToken = jwtUtil.createToken(user.id!!, user.email, user.nickname, user.userRole)

        return SigninResponse(bearerToken)
    }
}
