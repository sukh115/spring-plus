package org.example.expert.domain.user.enums

import org.example.expert.domain.common.exception.InvalidRequestException
import java.util.*

enum class UserRole {
    ADMIN, USER;

    companion object {
        @JvmStatic
        fun of(role: String?): UserRole {
            return Arrays.stream(entries.toTypedArray())
                .filter { r: UserRole -> r.name.equals(role, ignoreCase = true) }
                .findFirst()
                .orElseThrow { InvalidRequestException("유효하지 않은 UerRole") }
        }
    }
}
