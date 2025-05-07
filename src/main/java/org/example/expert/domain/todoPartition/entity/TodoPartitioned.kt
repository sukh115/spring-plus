package org.example.expert.domain.todoPartition.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "todos_partitioned")
@Getter
@NoArgsConstructor
public class TodoPartitioned extends Timestamped {

    @Id
    private Long id;

    private String title;
    private String contents;
    private String weather;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 생성자
    public TodoPartitioned(Long id, String title, String contents, String weather, User user) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.weather = weather;
        this.user = user;
    }
}
