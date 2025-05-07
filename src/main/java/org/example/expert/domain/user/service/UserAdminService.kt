package org.example.expert.domain.user.service

import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest
import org.example.expert.domain.user.enums.UserRole
import org.example.expert.domain.user.enums.UserRole.Companion.of
import org.example.expert.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAdminService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun changeUserRole(userId: Long, request: UserRoleChangeRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { InvalidRequestException("User not found") }

        user.updateRole(UserRole.of(request.role))
    }
}