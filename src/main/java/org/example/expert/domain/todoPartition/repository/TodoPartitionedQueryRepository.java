package org.example.expert.domain.todoPartition.repository;

import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;

import java.util.List;

public interface TodoPartitionedQueryRepository {
    List<TodoSearchResponse> searchPartitioned(TodoSearchCondition condition);
}
