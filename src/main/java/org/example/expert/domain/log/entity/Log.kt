package org.example.expert.domain.log.entity

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDateTime

@Entity
@Table(name = "log")
class Log(@Column(nullable = false)var message: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    @Column(nullable = false)
    private var createdAt: LocalDateTime = LocalDateTime.now()

    protected constructor() : this("")
}
