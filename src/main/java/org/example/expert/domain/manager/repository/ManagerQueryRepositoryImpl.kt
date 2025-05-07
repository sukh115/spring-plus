package org.example.expert.domain.manager.repository

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.expert.domain.manager.entity.QManager
import org.springframework.stereotype.Repository
import java.util.function.Function
import java.util.stream.Collectors

@Repository
class ManagerQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ManagerQueryRepository {

    override fun countManagersByTodoIds(todoIds: List<Long>): Map<Long, Long> {
        if (todoIds.isEmpty()) return emptyMap()

        val manager = QManager.manager

        return queryFactory
            .select(manager.todo.id, manager.id.countDistinct())
            .from(manager)
            .where(manager.todo.id.`in`(todoIds))
            .groupBy(manager.todo.id)
            .fetch()
            .associate { tuple ->
                tuple[manager.todo.id]!! to tuple[manager.id.countDistinct()]!!
            }
    }

}
