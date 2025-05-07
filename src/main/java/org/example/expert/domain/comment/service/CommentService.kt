package org.example.expert.domain.comment.service

import lombok.RequiredArgsConstructor
import org.example.expert.domain.comment.dto.request.CommentSaveRequest
import org.example.expert.domain.comment.dto.response.CommentResponse
import org.example.expert.domain.comment.dto.response.CommentSaveResponse
import org.example.expert.domain.comment.entity.Comment
import org.example.expert.domain.comment.repository.CommentRepository
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.todo.repository.TodoRepository
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService (
    val todoRepository: TodoRepository,
    val commentRepository: CommentRepository
) {
    @Transactional
    fun saveComment(authUser: AuthUser?, todoId: Long, commentSaveRequest: CommentSaveRequest): CommentSaveResponse {
        val user = User.fromAuthUser(
            authUser!!
        )
        val todo = todoRepository.findById(todoId).orElseThrow { InvalidRequestException("Todo not found") }

        val newComment = Comment(
            commentSaveRequest.contents,
            user,
            todo
        )

        val savedComment = commentRepository.save(newComment)

        return CommentSaveResponse(
            savedComment.id!!,
            savedComment.contents,
            UserResponse(user.id!!, user.email, user.nickname)
        )
    }

    fun getComments(todoId: Long): List<CommentResponse> {
        val commentList = commentRepository.findByTodoIdWithUser(todoId)

        val dtoList: MutableList<CommentResponse> = ArrayList()
        for (comment in commentList) {
            val user = comment.user
            val dto = CommentResponse(
                comment.id!!,
                comment.contents,
                UserResponse(user.id!!, user.email, user.nickname)
            )
            dtoList.add(dto)
        }
        return dtoList
    }
}
