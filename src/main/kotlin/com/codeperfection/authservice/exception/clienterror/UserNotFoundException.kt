package com.codeperfection.authservice.exception.clienterror

import com.codeperfection.authservice.exception.dto.ErrorType
import org.springframework.http.HttpStatus
import java.util.*

data class UserNotFoundException(val userId: UUID) : ClientErrorException(
    status = HttpStatus.NOT_FOUND,
    errorType = ErrorType.USER_NOT_FOUND,
    message = "User with ID '$userId' not found"
)
