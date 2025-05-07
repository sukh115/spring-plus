package org.example.expert.domain.todo.repository

import org.example.expert.domain.todo.entity.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TodoRepository : JpaRepository<Todo, Long>, TodoQueryRepository {
    @Query(
        """
    SELECT t
    FROM Todo t
    WHERE (:weather IS NULL OR t.weather = :weather)
      AND (:start IS NULL OR t.modifiedAt >= :start)
      AND (:end IS NULL OR t.modifiedAt <= :end)
    ORDER BY t.modifiedAt DESC
    """
    )
    @EntityGraph(attributePaths = ["user"])
    fun searchByWeatherAndDateRange(
        @Param("weather") weather: String?,
        @Param("start") start: LocalDateTime?,
        @Param("end") end: LocalDateTime?,
        pageable: Pageable
    ): Page<Todo>
}

