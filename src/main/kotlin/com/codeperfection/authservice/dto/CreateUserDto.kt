package com.codeperfection.authservice.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUserDto(
    @field:NotBlank
    @field:Email
    val email: String,
    @field:Size(min = 4, max = 128)
    val password: String,
    @field:NotBlank
    @field:Size(max = 256)
    val name: String
)
