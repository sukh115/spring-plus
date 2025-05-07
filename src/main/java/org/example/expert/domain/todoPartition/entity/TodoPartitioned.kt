package org.example.expert.domain.todoPartition.entity

import jakarta.persistence.*
import org.example.expert.domain.common.entity.Timestamped
import org.example.expert.domain.user.entity.User

@Entity
@Table(name = "todos_partitioned")
class TodoPartitioned(
    @Id
    var id: Long,
    var title: String,
    var contents: String,
    var weather: String,

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var user: User
) : Timestamped() {

    protected constructor() : this(
        0L,
        "",
        "",
        "",
        User()
    )
}

