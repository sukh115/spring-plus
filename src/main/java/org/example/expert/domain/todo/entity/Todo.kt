package org.example.expert.domain.todo.entity

import jakarta.persistence.*
import org.example.expert.domain.comment.entity.Comment
import org.example.expert.domain.common.entity.Timestamped
import org.example.expert.domain.manager.entity.Manager
import org.example.expert.domain.user.entity.User

@Entity
@Table(name = "todos")
class Todo(
    @Column(nullable = false) var title: String,
    @Column(nullable = false) var contents: String,
    @Column(nullable = false) var weather: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) var user: User
) : Timestamped() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val comments: MutableList<Comment> = mutableListOf()

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.PERSIST])
    val managers: MutableList<Manager> = mutableListOf()

    init {
        managers.add(Manager(user, this))
    }
}
