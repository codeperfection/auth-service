package com.codeperfection.authservice.exception.clienterror

import com.codeperfection.authservice.exception.dto.ErrorType
import org.springframework.http.HttpStatus

object UnauthorizedException : ClientErrorException(
    status = HttpStatus.FORBIDDEN,
    errorType = ErrorType.AUTHORIZATION_ERROR,
    message = "Authorization error"
)
