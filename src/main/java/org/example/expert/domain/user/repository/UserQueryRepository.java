package org.example.expert.domain.user.repository;

import org.example.expert.domain.user.dto.response.UserResponse;

import java.util.List;

public interface UserQueryRepository {
    public List<UserResponse> findUserByNickname(String nickname);
}
