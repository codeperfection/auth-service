package com.codeperfection.authservice.dto

import org.springframework.security.oauth2.core.AuthorizationGrantType

enum class AuthGrantType(val springType: AuthorizationGrantType) {
    AUTHORIZATION_CODE(AuthorizationGrantType.AUTHORIZATION_CODE),
    REFRESH_TOKEN(AuthorizationGrantType.REFRESH_TOKEN),
    CLIENT_CREDENTIALS(AuthorizationGrantType.CLIENT_CREDENTIALS)
}
