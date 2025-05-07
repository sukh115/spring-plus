package org.example.expert.domain.manager.entity

import jakarta.persistence.*
import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.user.entity.User

@Entity
@Table(name = "managers")
class Manager(// 일정 만든 사람 id
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var user: User, // 일정 id
    @field:JoinColumn(name = "todo_id", nullable = false)
    @field:ManyToOne(fetch = FetchType.LAZY)
    var todo: Todo
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
