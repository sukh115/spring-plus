package org.example.expert.domain.comment.repository;

import java.util.List;
import java.util.Map;

public interface CommentQueryRepository {
    public Map<Long, Long> countCommentsByTodoIds(List<Long> todoIds);
}
