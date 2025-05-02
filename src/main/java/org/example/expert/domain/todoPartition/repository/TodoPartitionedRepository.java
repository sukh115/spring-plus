package org.example.expert.domain.todoPartition.repository;

import org.example.expert.domain.todoPartition.entity.TodoPartitioned;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoPartitionedRepository extends JpaRepository<TodoPartitioned, Long>, TodoPartitionedQueryRepository {
}
