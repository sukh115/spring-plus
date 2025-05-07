package org.example.expert.domain.todoPartition.repository

import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todoPartition.entity.TodoPartitioned
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface TodoPartitionedQueryRepository {
    fun searchPartitioned(condition: TodoSearchCondition): List<TodoSearchResponse>

    fun findTodos(condition: TodoSearchCondition, pageable: Pageable): List<TodoPartitioned>
}
