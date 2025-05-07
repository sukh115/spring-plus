package org.example.expert.domain.todo.service

import org.example.expert.client.WeatherClient
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.todo.dto.request.TodoSaveRequest
import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoResponse
import org.example.expert.domain.todo.dto.response.TodoSaveResponse
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.todo.repository.TodoRepository
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class TodoService(
    private val todoRepository: TodoRepository,
    private val weatherClient: WeatherClient
) {
    @Transactional
    fun saveTodo(authUser: AuthUser, todoSaveRequest: TodoSaveRequest): TodoSaveResponse {
        val user = User.fromAuthUser(authUser)

        val weather = weatherClient.todayWeather

        val newTodo = Todo(todoSaveRequest.title, todoSaveRequest.contents, weather, user)
        val savedTodo = todoRepository.save(newTodo)

        return TodoSaveResponse(
            savedTodo.id!!,
            savedTodo.title,
            savedTodo.contents,
            weather,
            UserResponse(user.id!!, user.email, user.nickname)
        )
    }

    fun getTodos(
        weather: String?,
        start: LocalDateTime?,
        end: LocalDateTime?,
        page: Int,
        size: Int
    ): Page<TodoResponse> {
        val pageable: Pageable = PageRequest.of(page - 1, size)

        val todos = todoRepository.searchByWeatherAndDateRange(weather, start, end, pageable)

        return todos.map { todo: Todo ->
            TodoResponse(
                todo.id!!,
                todo.title,
                todo.contents,
                todo.weather,
                UserResponse(todo.user.id!!, todo.user.email, todo.user.nickname),
                todo.createdAt,
                todo.modifiedAt
            )
        }
    }

    fun getTodo(todoId: Long): TodoResponse {
        val todo: Todo = todoRepository.findByIdWithUser(todoId)
            ?: throw InvalidRequestException("Todo not found")

        val user = todo.user

        return TodoResponse(
            todo.id!!,
            todo.title,
            todo.contents,
            todo.weather,
            UserResponse(user.id!!, user.email, user.nickname),
            todo.createdAt,
            todo.modifiedAt
        )
    }

    fun searchTodos(todoSearchCondition: TodoSearchCondition): Page<TodoSearchResponse> {
        return todoRepository.search(todoSearchCondition)
    }
}
