package org.example.expert.domain.comment.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.expert.domain.comment.entity.QComment
import org.springframework.stereotype.Repository

@Repository
class CommentQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CommentQueryRepository {
    override fun countCommentsByTodoIds(todoIds: List<Long>): Map<Long, Long> {
        val comment = QComment.comment

        return queryFactory
            .select(comment.todo.id, comment.id.countDistinct())
            .from(comment)
            .where(comment.todo.id.`in`(todoIds))
            .groupBy(comment.todo.id)
            .fetch()
            .associate { tuple ->
                val todoId = tuple.get(comment.todo.id)!!
                val count = tuple.get(comment.id.countDistinct())!!
                todoId to count
            }
    }
}
