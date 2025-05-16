package org.example.expert.domain.todoPartition.repository;

import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todoPartition.entity.TodoPartitioned;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoPartitionedQueryRepository {
    List<TodoSearchResponse> searchPartitioned(TodoSearchCondition condition);

    List<TodoPartitioned> findTodos(String title, String nickname, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Long> findTodosIds(String title, String nickname, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<TodoPartitioned> findTodosByIds(List<Long> ids);
}
