package org.example.expert.domain.user.dto.request

import jakarta.validation.constraints.NotBlank

data class UserChangePasswordRequest(
    @field:NotBlank
    val oldPassword: String,

    @field:NotBlank
    val newPassword: String
)