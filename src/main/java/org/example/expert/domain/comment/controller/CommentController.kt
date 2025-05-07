package org.example.expert.domain.comment.controller

import jakarta.validation.Valid
import org.example.expert.domain.comment.dto.request.CommentSaveRequest
import org.example.expert.domain.comment.dto.response.CommentResponse
import org.example.expert.domain.comment.dto.response.CommentSaveResponse
import org.example.expert.domain.comment.service.CommentService
import org.example.expert.domain.common.dto.AuthUser
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class CommentController(
    val commentService: CommentService
) {
    @PostMapping("/todos/{todoId}/comments")
    fun saveComment(
        @AuthenticationPrincipal authUser: AuthUser,
        @PathVariable todoId: Long,
        @Valid @RequestBody commentSaveRequest: CommentSaveRequest
    ): ResponseEntity<CommentSaveResponse> {
        return ResponseEntity.ok(commentService.saveComment(authUser, todoId, commentSaveRequest))
    }

    @GetMapping("/todos/{todoId}/comments")
    fun getComments(@PathVariable todoId: Long): ResponseEntity<List<CommentResponse>> {
        return ResponseEntity.ok(commentService.getComments(todoId))
    }
}
