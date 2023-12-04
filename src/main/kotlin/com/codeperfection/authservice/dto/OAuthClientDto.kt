package com.codeperfection.authservice.dto

import java.util.*

data class OAuthClientDto(
    val id: UUID,
    val clientName: String,
    val authorizationGrantTypes: List<AuthGrantType>,
    val redirectUri: String,
    val scopes: List<String>
)
