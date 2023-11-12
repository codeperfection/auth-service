package com.codeperfection.authservice.exception.clienterror

import com.codeperfection.authservice.exception.dto.ErrorType
import org.springframework.http.HttpStatus

data class EmailAlreadyTakenException(val email: String) : ClientErrorException(
    status = HttpStatus.CONFLICT,
    errorType = ErrorType.EMAIL_ALREADY_TAKEN,
    message = "Email $email is already taken"
)
