package com.codeperfection.authservice.exception.dto

enum class ErrorType {
    EMAIL_ALREADY_TAKEN,
    OAUTH_CLIENT_ID_ALREADY_TAKEN,
    USER_NOT_FOUND,
    AUTHORIZATION_ERROR,
    INVALID_REQUEST,
    INTERNAL_SERVER_ERROR
}
