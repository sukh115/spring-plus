package org.example.expert.domain.todoPartition.controller

import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todoPartition.service.TodoPartitionedService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/todos-partitioned")
class TodoPartitionedController(
    private val todoPartitionedService: TodoPartitionedService
) {
    @GetMapping("/search")
    fun search(
        @ModelAttribute condition: TodoSearchCondition
    ): ResponseEntity<List<TodoSearchResponse>> {
        val results = todoPartitionedService.searchTodos(condition)
        return ResponseEntity.ok(results)
    }
}
