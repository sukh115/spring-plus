package org.example.expert.domain.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor

data class SigninRequest (

    @field:NotBlank @field:Email
    val email: String,

    @field:NotBlank
    val password:String
)
