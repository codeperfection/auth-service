package com.codeperfection.authservice.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreateOAuthClientDto(
    @field:Size(min = 4, max = 128)
    val clientId: String,
    @field:Size(min = 4, max = 128)
    val clientSecret: String,
    @field:Size(min = 4, max = 128)
    val clientName: String,
    @field:ValidAuthGrantTypes
    val authorizationGrantTypes: List<AuthGrantType>,
    @field:Size(min = 4)
    val redirectUri: String,
    @field:NotEmpty
    val scopes: List<String>
)
