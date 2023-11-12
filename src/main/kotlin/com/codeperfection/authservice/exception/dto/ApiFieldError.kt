package com.codeperfection.authservice.exception.dto

import org.springframework.http.HttpStatus
import java.time.OffsetDateTime

data class ApiFieldError(
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    val status: Int = HttpStatus.BAD_REQUEST.value(),
    val errorType: ErrorType,
    val message: String,
    val fieldErrors: List<FieldErrorInfo>
) {
    data class FieldErrorInfo(
        val fieldName: String,
        val errorMessage: String
    )
}
