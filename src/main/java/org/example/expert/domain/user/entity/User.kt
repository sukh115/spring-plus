package org.example.expert.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.user.enums.UserRole;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(unique = true)
    private String nickname;
    private String profileImageUrl;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public User(String email, String password, String nickname, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    private User(Long id, String email, String nickname, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    public void updateProfileImage(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }


    public static User fromAuthUser(AuthUser authUser) {
        return new User(authUser.getId(), authUser.getEmail(), authUser.getNickname(), authUser.getUserRole());
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void updateRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
