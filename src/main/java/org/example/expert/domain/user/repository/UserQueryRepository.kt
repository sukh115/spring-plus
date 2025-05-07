package org.example.expert.domain.user.repository

import org.example.expert.domain.user.dto.response.UserResponse

interface UserQueryRepository {
    fun findUserByNickname(nickname: String): List<UserResponse>
}
