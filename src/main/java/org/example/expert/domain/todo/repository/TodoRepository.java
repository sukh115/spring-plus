package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>,TodoQueryRepository {

    @EntityGraph(attributePaths = {"user"})
    @Query("    SELECT t\n" +
            "    FROM Todo t\n" +
            "    WHERE (:weather IS NULL OR t.weather = :weather)\n" +
            "      AND (:start IS NULL OR t.modifiedAt >= :start)\n" +
            "      AND (:end IS NULL OR t.modifiedAt <= :end)\n" +
            "    ORDER BY t.modifiedAt DESC")
    Page<Todo> searchByWeatherAndDateRange(@Param("weather") String weather, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

}
