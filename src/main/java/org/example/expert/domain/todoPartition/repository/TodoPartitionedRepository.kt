package org.example.expert.domain.todoPartition.repository

import org.example.expert.domain.todoPartition.entity.TodoPartitioned
import org.springframework.data.jpa.repository.JpaRepository

interface TodoPartitionedRepository : JpaRepository<TodoPartitioned, Long>, TodoPartitionedQueryRepository
