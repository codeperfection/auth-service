package com.codeperfection.authservice.exception.dto

enum class ErrorType {
    EMAIL_ALREADY_TAKEN,
    OAUTH_CLIENT_ID_ALREADY_TAKEN,
    USER_NOT_FOUND,
    INVALID_REQUEST,
    INTERNAL_SERVER_ERROR
}
