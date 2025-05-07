package org.example.expert.domain.auth.controller

import jakarta.validation.Valid
import org.example.expert.domain.auth.dto.request.SigninRequest
import org.example.expert.domain.auth.dto.request.SignupRequest
import org.example.expert.domain.auth.dto.response.SigninResponse
import org.example.expert.domain.auth.dto.response.SignupResponse
import org.example.expert.domain.auth.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody signupRequest: SignupRequest
    ): ResponseEntity<SignupResponse> {
        return ResponseEntity.ok(authService.signup(signupRequest))
    }

    @PostMapping("/signin")
    fun signin(
        @Valid @RequestBody signinRequest: SigninRequest
    ): ResponseEntity<SigninResponse> {
        return ResponseEntity.ok(authService.signin(signinRequest))
    }
}