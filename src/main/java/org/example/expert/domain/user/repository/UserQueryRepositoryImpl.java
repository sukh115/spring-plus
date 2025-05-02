package org.example.expert.domain.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.QUser;

import java.util.List;

@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository{

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    @Override
    public List<UserResponse> findUserByNickname(String nickname) {
        return queryFactory
                .select(Projections.constructor(UserResponse.class,
                        user.id,
                        user.email,
                        user.nickname
                ))
                .from(user)
                .where(user.nickname.eq(nickname))
                .fetch();
    }
}
