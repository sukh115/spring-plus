package org.example.expert.domain.manager.dto.response

import org.example.expert.domain.user.dto.response.UserResponse

data class ManagerSaveResponse(private val id: Long, private val user: UserResponse)
