package org.example.expert.domain.todoPartition.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todoPartition.service.TodoPartitionedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todos-partitioned")
public class TodoPartitionedController {

    private final TodoPartitionedService todoPartitionedService;

    @GetMapping("/search")
    public ResponseEntity<List<TodoSearchResponse>> search(
            @ModelAttribute TodoSearchCondition condition
    ) {
        List<TodoSearchResponse> results = todoPartitionedService.searchTodos(condition);
        return ResponseEntity.ok(results);
    }
}
