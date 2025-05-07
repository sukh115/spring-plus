package org.example.expert.domain.user.entity

import jakarta.persistence.*
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.entity.Timestamped
import org.example.expert.domain.user.enums.UserRole

@Entity
@Table(name = "users")
class User protected constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true)
    var email: String,

    var password: String,

    @Column(unique = true)
    var nickname: String,

    var profileImageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    var userRole: UserRole

) : Timestamped() {
    constructor() : this(
        email = "",
        password = "",
        nickname = "",
        userRole = UserRole.USER
    )

    fun updateProfileImage(imageUrl: String?) {
        this.profileImageUrl = imageUrl
    }

    fun changePassword(newPassword: String) {
        this.password = newPassword
    }

    fun updateRole(newRole: UserRole) {
        this.userRole = newRole
    }

    companion object {
        fun fromAuthUser(authUser: AuthUser): User {
            return User(
                id = authUser.id,
                email = authUser.email,
                nickname = authUser.nickname,
                userRole = authUser.userRole,
                password = "unknown" // 기본 비밀번호 (보안상 무의미 처리)
            )
        }
    }
}
