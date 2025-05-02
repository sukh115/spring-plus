package org.example.expert.domain.todoPartition.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.repository.CommentQueryRepository;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.manager.repository.ManagerQueryRepository;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todoPartition.entity.TodoPartitioned;
import org.example.expert.domain.todoPartition.repository.TodoPartitionedQueryRepository;
import org.example.expert.domain.todoPartition.repository.TodoPartitionedRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TodoPartitionedService {

    private final TodoPartitionedRepository todoPartitionedRepository;
    private final CommentRepository commentRepository;
    private final ManagerRepository managerRepository;

    public List<TodoSearchResponse> searchTodos(TodoSearchCondition condition) {
        Pageable pageable = PageRequest.of(condition.getPage() - 1, condition.getSize());

        // 1. 파티셔닝 테이블 기반 Todo 목록 조회
        List<TodoPartitioned> todos = todoPartitionedRepository.findTodos(
                condition.getTitle(),
                condition.getNickname(),
                condition.getStartDate(),
                condition.getEndDate(),
                pageable
        );

        // 2. Todo ID 목록 추출
        List<Long> todoIds = todos.stream()
                .map(TodoPartitioned::getId)
                .toList();

        // 3. 댓글, 매니저 수 조회
        Map<Long, Long> commentCountMap = commentRepository.countCommentsByTodoIds(todoIds);
        Map<Long, Long> managerCountMap = managerRepository.countManagersByTodoIds(todoIds);

        // 4. 응답 DTO 조립
        return todos.stream()
                .map(todo -> new TodoSearchResponse(
                        todo.getTitle(),
                        managerCountMap.getOrDefault(todo.getId(), 0L),
                        commentCountMap.getOrDefault(todo.getId(), 0L)
                ))
                .toList();
    }
}

