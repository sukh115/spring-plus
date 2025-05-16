package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        Todo result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoSearchResponse> search(TodoSearchCondition condition) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        List<TodoSearchResponse> results = queryFactory
                .select(Projections.constructor(
                        TodoSearchResponse.class,
                        todo.title,
                        manager.id.countDistinct(),
                        comment.id.countDistinct()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .leftJoin(todo.user, user)
                .where(
                        containsTitle(condition.getTitle()),
                        containsNickname(condition.getNickname()),
                        betweenCreatedAt(condition.getStartDate(), condition.getEndDate())
                )
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset((long) (condition.getPage() - 1) * condition.getSize())
                .limit(condition.getSize())
                .fetch();

        Long count = queryFactory
                .select(todo.countDistinct())
                .from(todo)
                .leftJoin(todo.user, user)
                .where(
                        containsTitle(condition.getTitle()),
                        containsNickname(condition.getNickname()),
                        betweenCreatedAt(condition.getStartDate(), condition.getEndDate())
                )
                .fetchOne();

        return new PageImpl<>(results, PageRequest.of(condition.getPage() - 1, condition.getSize()), count);
    }

    private BooleanExpression containsTitle(String title) {
        return StringUtils.hasText(title) ? QTodo.todo.title.contains(title) : null;
    }

    private BooleanExpression containsNickname(String nickname) {
        return StringUtils.hasText(nickname) ? QTodo.todo.user.nickname.contains(nickname) : null;
    }

    private BooleanExpression betweenCreatedAt(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return QTodo.todo.createdAt.between(start, end);
        } else if (start != null) {
            return QTodo.todo.createdAt.goe(start);
        } else if (end != null) {
            return QTodo.todo.createdAt.loe(end);
        } else {
            return null;
        }
    }
}