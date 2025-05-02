package org.example.expert.domain.todo.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoServiceTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Transactional
    @Commit
    void insertFastMillionTodos() {
        int total = 1_000_000;
        int batchSize = 5000;

        List<User> users = userRepository.findAll();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 1; i <= total; i++) {
            User randomUser = users.get(ThreadLocalRandom.current().nextInt(users.size()));

            Todo todo = new Todo(
                    "할 일 " + i,
                    "내용 " + i,
                    pickRandomWeather(),
                    randomUser
            );
            setCreatedAtViaReflection(todo, randomDateBetween(
                    LocalDateTime.of(2023, 1, 1, 0, 0),
                    LocalDateTime.of(2025, 12, 31, 23, 59)
            ));

            em.persist(todo);

            if (i % batchSize == 0) {
                em.flush();
                em.clear();
                System.out.println(i + "건 완료");
            }
        }

        em.flush();
        em.clear();

        stopWatch.stop();
        System.out.println("✅ 총 100만 건 삽입 완료 / 소요 시간: " + stopWatch.getTotalTimeSeconds() + "초");
    }

    private LocalDateTime randomDateBetween(LocalDateTime start, LocalDateTime end) {
        long startEpoch = start.toEpochSecond(ZoneOffset.UTC);
        long endEpoch = end.toEpochSecond(ZoneOffset.UTC);
        long randomEpoch = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch);
        return LocalDateTime.ofEpochSecond(randomEpoch, 0, ZoneOffset.UTC);
    }

    private String pickRandomWeather() {
        String[] weathers = {"맑음", "흐림", "비", "눈", "번개"};
        return weathers[ThreadLocalRandom.current().nextInt(weathers.length)];
    }

    private void setCreatedAtViaReflection(Todo todo, LocalDateTime createdAt) {
        try {
            Field field = Todo.class.getSuperclass().getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(todo, createdAt);
        } catch (Exception e) {
            throw new RuntimeException("createdAt 필드 설정 실패", e);
        }
    }
}

