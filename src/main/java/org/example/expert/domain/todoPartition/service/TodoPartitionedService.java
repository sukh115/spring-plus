package org.example.expert.domain.todoPartition.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todoPartition.repository.TodoPartitionedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoPartitionedService {

    private final TodoPartitionedRepository todoPartitionedRepository;

    public List<TodoSearchResponse> searchTodos(TodoSearchCondition condition) {
        return todoPartitionedRepository.searchPartitioned(condition);
    }
}
