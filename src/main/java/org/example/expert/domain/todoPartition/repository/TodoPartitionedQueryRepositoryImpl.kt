package org.example.expert.domain.todoPartition.repository

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import lombok.RequiredArgsConstructor
import org.example.expert.domain.comment.entity.QComment
import org.example.expert.domain.manager.entity.QManager
import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todoPartition.entity.QTodoPartitioned
import org.example.expert.domain.todoPartition.entity.TodoPartitioned
import org.example.expert.domain.user.entity.QUser
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TodoPartitionedQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : TodoPartitionedQueryRepository {

    override fun searchPartitioned(condition: TodoSearchCondition): List<TodoSearchResponse> {
        val todo = QTodoPartitioned.todoPartitioned
        val user = QUser.user
        val manager = QManager.manager
        val comment = QComment.comment

        return queryFactory
            .select(
                Projections.constructor(
                    TodoSearchResponse::class.java,
                    todo.title,
                    manager.id.countDistinct(),
                    comment.id.countDistinct()
                )
            )
            .from(todo)
            .leftJoin(manager).on(manager.todo.id.eq(todo.id))
            .leftJoin(comment).on(comment.todo.id.eq(todo.id))
            .leftJoin(todo.user, user)
            .where(
                containsTitle(condition.title),
                containsNickname(condition.nickname),
                betweenCreatedAt(condition.startDate, condition.endDate)
            )
            .groupBy(todo.id, todo.title, todo.createdAt)
            .orderBy(todo.createdAt.desc())
            .offset((condition.page - 1).toLong() * condition.size)
            .limit(condition.size.toLong())
            .fetch()
    }

    override fun findTodos(condition: TodoSearchCondition, pageable: Pageable): List<TodoPartitioned> {
        val todo = QTodoPartitioned.todoPartitioned
        val user = QUser.user

        return queryFactory
            .selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .where(
                containsTitle(condition.title),
                containsNickname(condition.nickname),
                betweenCreatedAt(condition.startDate, condition.endDate)
            )
            .orderBy(todo.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }


    private fun containsTitle(title: String?): BooleanExpression? {
        return if (!title.isNullOrBlank()) QTodoPartitioned.todoPartitioned.title.contains(title) else null
    }

    private fun containsNickname(nickname: String?): BooleanExpression? {
        return if (!nickname.isNullOrBlank()) QUser.user.nickname.contains(nickname) else null
    }

    private fun betweenCreatedAt(start: LocalDateTime?, end: LocalDateTime?): BooleanExpression? {
        if (start != null && end != null) {
            return QTodoPartitioned.todoPartitioned.createdAt.between(start, end)
        } else if (start != null) {
            return QTodoPartitioned.todoPartitioned.createdAt.goe(start)
        } else if (end != null) {
            return QTodoPartitioned.todoPartitioned.createdAt.loe(end)
        }
        return null
    }
}
