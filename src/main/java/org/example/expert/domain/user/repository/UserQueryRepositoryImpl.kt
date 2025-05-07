package org.example.expert.domain.user.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.entity.QUser
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : UserQueryRepository {
    private val user = QUser.user

    override fun findUserByNickname(nickname: String): List<UserResponse> {
        return queryFactory
            .select(
                Projections.constructor(
                    UserResponse::class.java,
                    user.id,
                    user.email,
                    user.nickname
                )
            )
            .from(user)
            .where(user.nickname.eq(nickname))
            .fetch()
    }
}
