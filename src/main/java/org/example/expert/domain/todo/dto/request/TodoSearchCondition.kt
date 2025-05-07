package org.example.expert.domain.todo.dto.request

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class TodoSearchCondition(
    val title: String,
    val nickname: String,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val startDate: LocalDateTime,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val endDate: LocalDateTime,

    val page: Int = 1,
    val size: Int = 10
)