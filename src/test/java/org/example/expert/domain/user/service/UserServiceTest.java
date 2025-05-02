package org.example.expert.domain.user.service;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void generateMillionUsers() {
        int total = 1_000_000;
        int batchSize = 2000;
        List<User> users = new ArrayList<>();

        for (int i = 0; i < total; i++) {
            String email = "test" + i + "@example.com";
            String nickname = "user_" + UUID.randomUUID().toString().substring(0, 8);
            String rawPassword = "Test1234!";
            String encodedPassword = "encoded-pass";

            users.add(User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .nickname(nickname)
                    .build());

            if (users.size() == batchSize) {
                userRepository.saveAll(users);
                users.clear();
                System.out.println((i + 1) + " users inserted...");
            }
        }

        if (!users.isEmpty()) {
            userRepository.saveAll(users);
        }

        System.out.println("✅ 총 " + total + "명의 유저 삽입 완료");
    }

}