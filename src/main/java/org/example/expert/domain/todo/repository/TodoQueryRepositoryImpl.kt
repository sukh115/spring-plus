package org.example.expert.domain.todo.repository

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.expert.domain.comment.entity.QComment
import org.example.expert.domain.manager.entity.QManager
import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.entity.QTodo
import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.user.entity.QUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TodoQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : TodoQueryRepository {

    override fun findByIdWithUser(todoId: Long): Todo? {
        val todo = QTodo.todo
        val user = QUser.user

        val result = queryFactory
            .selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .where(todo.id.eq(todoId))
            .fetchOne()

        return result
    }

    override fun search(condition: TodoSearchCondition): Page<TodoSearchResponse> {
        val todo = QTodo.todo
        val user = QUser.user
        val manager = QManager.manager
        val comment = QComment.comment

        val results = queryFactory
            .select(
                Projections.constructor(
                    TodoSearchResponse::class.java,
                    todo.title,
                    manager.id.countDistinct(),
                    comment.id.countDistinct()
                )
            )
            .from(todo)
            .leftJoin(todo.managers, manager)
            .leftJoin(todo.comments, comment)
            .leftJoin(todo.user, user)
            .where(
                containsTitle(condition.title),
                containsNickname(condition.nickname),
                betweenCreatedAt(condition.startDate, condition.endDate)
            )
            .groupBy(todo.id)
            .orderBy(todo.createdAt.desc())
            .offset(((condition.page - 1).toLong()) * condition.size)
            .limit(condition.size.toLong())
            .fetch()

        val count = queryFactory
            .select(todo.countDistinct())
            .from(todo)
            .leftJoin(todo.user, user)
            .where(
                containsTitle(condition.title),
                containsNickname(condition.nickname),
                betweenCreatedAt(condition.startDate, condition.endDate)
            )
            .fetchOne() ?: 0L

        return PageImpl(results, PageRequest.of(condition.page - 1, condition.size), count)
    }

    private fun containsTitle(title: String?): BooleanExpression? =
        if (!title.isNullOrBlank()) QTodo.todo.title.contains(title) else null

    private fun containsNickname(nickname: String?): BooleanExpression? =
        if (!nickname.isNullOrBlank()) QTodo.todo.user.nickname.contains(nickname) else null

    private fun betweenCreatedAt(start: LocalDateTime?, end: LocalDateTime?): BooleanExpression? =
        when {
            start != null && end != null -> QTodo.todo.createdAt.between(start, end)
            start != null -> QTodo.todo.createdAt.goe(start)
            end != null -> QTodo.todo.createdAt.loe(end)
            else -> null
        }
}