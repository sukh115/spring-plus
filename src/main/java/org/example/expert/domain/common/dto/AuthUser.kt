package org.example.expert.domain.common.dto

import lombok.Getter
import org.example.expert.domain.user.enums.UserRole

@Getter
data class AuthUser(
    val id: Long,
    val email: String,
    val nickname: String,
    val userRole: UserRole
)
