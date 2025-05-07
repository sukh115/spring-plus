package org.example.expert.domain.user.service

import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.s3.S3Uploader
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val s3Uploader: S3Uploader
) {

    fun getUser(userId: Long): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { InvalidRequestException("User not found") }
        return UserResponse(user.id!!, user.email, user.nickname)
    }

    @Transactional
    fun changePassword(userId: Long, request: UserChangePasswordRequest) {
        validateNewPassword(request)

        val user = userRepository.findById(userId)
            .orElseThrow { InvalidRequestException("User not found") }

        if (passwordEncoder.matches(request.newPassword, user.password)) {
            throw InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.")
        }

        if (!passwordEncoder.matches(request.oldPassword, user.password)) {
            throw InvalidRequestException("잘못된 비밀번호입니다.")
        }

        user.changePassword(passwordEncoder.encode(request.newPassword))
    }

    private fun validateNewPassword(request: UserChangePasswordRequest) {
        val pw = request.newPassword
        if (pw.length < 8 || !pw.any { it.isDigit() } || !pw.any { it.isUpperCase() }) {
            throw InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.")
        }
    }

    @Transactional
    fun updateProfileImage(userId: Long, key: String) {
        val user = userRepository.findById(userId)
            .orElseThrow { InvalidRequestException("USER_NOT_FOUND") }
        user.updateProfileImage(key)
    }

    @Transactional
    fun deleteProfileImage(userId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { InvalidRequestException("USER_NOT_FOUND") }

        val key = user.profileImageUrl
        if (!key.isNullOrBlank()) {
            s3Uploader.deleteProfileImage(userId, key)
            user.updateProfileImage(null)
        }
    }

    fun findUsersByNickname(nickname: String): List<UserResponse> {
        return userRepository.findUserByNickname(nickname)
    }
}