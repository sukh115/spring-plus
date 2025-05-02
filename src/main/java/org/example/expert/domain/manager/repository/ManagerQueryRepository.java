package org.example.expert.domain.manager.repository;

import java.util.List;
import java.util.Map;

public interface ManagerQueryRepository {
    public Map<Long, Long> countManagersByTodoIds(List<Long> todoIds);
}
