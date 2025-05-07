package org.example.expert.domain.comment.dto.response

import org.example.expert.domain.user.dto.response.UserResponse

data class CommentSaveResponse(private val id: Long, private val contents: String, private val user: UserResponse)
