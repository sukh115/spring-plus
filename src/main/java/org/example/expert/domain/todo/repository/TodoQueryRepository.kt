package org.example.expert.domain.todo.repository

import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.entity.Todo
import org.springframework.data.domain.Page
import org.springframework.stereotype.Repository

@Repository
interface TodoQueryRepository {
    fun findByIdWithUser(todoId: Long): Todo?

    fun search(condition: TodoSearchCondition): Page<TodoSearchResponse>
}
