package org.example.expert.domain.manager.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.expert.domain.manager.entity.QManager.manager;

@Repository
@RequiredArgsConstructor
public class ManagerQueryRepositoryImpl implements ManagerQueryRepository{

    private final JPAQueryFactory queryFactory;

    public Map<Long, Long> countManagersByTodoIds(List<Long> todoIds) {
        return queryFactory
                .select(manager.todo.id, manager.id.countDistinct())
                .from(manager)
                .where(manager.todo.id.in(todoIds))
                .groupBy(manager.todo.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),
                        tuple -> tuple.get(1, Long.class)
                ));
    }

}
