package org.example.expert.domain.todo.controller

import jakarta.validation.Valid
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.todo.dto.request.TodoSaveRequest
import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoResponse
import org.example.expert.domain.todo.dto.response.TodoSaveResponse
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.service.TodoService
import org.springframework.data.domain.Page
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
class TodoController(
    private val todoService: TodoService
) {
    @PostMapping("/todos")
    fun saveTodo(
        @AuthenticationPrincipal authUser: AuthUser,
        @RequestBody todoSaveRequest: @Valid TodoSaveRequest
    ): ResponseEntity<TodoSaveResponse> {
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest))
    }

    @GetMapping("/todos")
    fun getTodos(
        @RequestParam(required = false) weather: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) start: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) end: LocalDateTime?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<TodoResponse>> {
        return ResponseEntity.ok(todoService.getTodos(weather, start, end, page, size))
    }

    @GetMapping("/todos/{todoId}")
    fun getTodo(@PathVariable todoId: Long): ResponseEntity<TodoResponse> {
        return ResponseEntity.ok(todoService.getTodo(todoId))
    }

    @GetMapping("/todos/search")
    fun searchTodo(@ModelAttribute todoSearchCondition: TodoSearchCondition): ResponseEntity<Page<TodoSearchResponse>> {
        return ResponseEntity.ok(todoService.searchTodos(todoSearchCondition))
    }
}
