package org.example.expert.domain.todoPartition.service

import org.example.expert.domain.comment.repository.CommentRepository
import org.example.expert.domain.manager.repository.ManagerRepository
import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todoPartition.repository.TodoPartitionedRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TodoPartitionedService(
    val todoPartitionedRepository: TodoPartitionedRepository,
    val commentRepository: CommentRepository,
    val managerRepository: ManagerRepository
) {

    fun searchTodos(condition: TodoSearchCondition): List<TodoSearchResponse> {
        val pageable: Pageable = PageRequest.of(condition.page - 1, condition.size)

        // 1. 파티셔닝 테이블 기반 Todo 목록 조회
        val todos = todoPartitionedRepository.findTodos(condition, pageable)

        // 2. Todo ID 목록 추출
        val todoIds = todos.map { it.id }.toList()


        // 3. 댓글, 매니저 수 조회
        val commentCountMap = commentRepository.countCommentsByTodoIds(todoIds)
        val managerCountMap = managerRepository.countManagersByTodoIds(todoIds)

        // 4. 응답 DTO 조립
        return todos.map { todo ->
            TodoSearchResponse(
                title = todo.title,
                managerCount = managerCountMap[todo.id] ?: 0L,
                commentCount = commentCountMap[todo.id] ?: 0L
            )
        }
    }
}

