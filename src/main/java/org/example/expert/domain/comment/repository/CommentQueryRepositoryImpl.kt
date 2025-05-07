package org.example.expert.domain.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static org.example.expert.domain.comment.entity.QComment.comment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Map<Long, Long> countCommentsByTodoIds(List<Long> todoIds) {
        return queryFactory
                .select(comment.todo.id, comment.id.countDistinct())
                .from(comment)
                .where(comment.todo.id.in(todoIds))
                .groupBy(comment.todo.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),
                        tuple -> tuple.get(1, Long.class)
                ));
    }

}
