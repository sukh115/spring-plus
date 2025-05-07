package org.example.expert.domain.comment.repository

interface CommentQueryRepository {
    fun countCommentsByTodoIds(todoIds: List<Long>): Map<Long, Long>
}
