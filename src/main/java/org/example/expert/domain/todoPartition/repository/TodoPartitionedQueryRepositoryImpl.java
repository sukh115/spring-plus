package org.example.expert.domain.todoPartition.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todoPartition.entity.QTodoPartitioned;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TodoPartitionedQueryRepositoryImpl implements TodoPartitionedQueryRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TodoSearchResponse> searchPartitioned(TodoSearchCondition condition) {
        QTodoPartitioned todo = QTodoPartitioned.todoPartitioned;
        QUser user = QUser.user;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        return queryFactory
                .select(Projections.constructor(
                        TodoSearchResponse.class,
                        todo.title,
                        manager.id.countDistinct(),
                        comment.id.countDistinct()
                ))
                .from(todo)
                .leftJoin(manager).on(manager.todo.id.eq(todo.id))
                .leftJoin(comment).on(comment.todo.id.eq(todo.id))
                .leftJoin(todo.user, user)
                .where(
                        containsTitle(condition.getTitle()),
                        containsNickname(condition.getNickname()),
                        betweenCreatedAt(condition.getStartDate(), condition.getEndDate())
                )
                .groupBy(todo.id, todo.title, todo.createdAt)
                .orderBy(todo.createdAt.desc())
                .offset((long) (condition.getPage() - 1) * condition.getSize())
                .limit(condition.getSize())
                .fetch();
    }

    private BooleanExpression containsTitle(String title) {
        return title != null && !title.isBlank() ? QTodoPartitioned.todoPartitioned.title.contains(title) : null;
    }

    private BooleanExpression containsNickname(String nickname) {
        return nickname != null && !nickname.isBlank() ? QUser.user.nickname.contains(nickname) : null;
    }

    private BooleanExpression betweenCreatedAt(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return QTodoPartitioned.todoPartitioned.createdAt.between(start, end);
        } else if (start != null) {
            return QTodoPartitioned.todoPartitioned.createdAt.goe(start);
        } else if (end != null) {
            return QTodoPartitioned.todoPartitioned.createdAt.loe(end);
        }
        return null;
    }

}