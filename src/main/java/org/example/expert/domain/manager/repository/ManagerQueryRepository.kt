package org.example.expert.domain.manager.repository

interface ManagerQueryRepository {
    fun countManagersByTodoIds(todoIds: List<Long>): Map<Long, Long>
}
