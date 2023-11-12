package com.codeperfection.authservice.dto

import java.util.*

data class UserDto(
    val id: UUID,
    val email: String,
    val name: String
)
