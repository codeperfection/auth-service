package com.codeperfection.authservice.exception.clienterror

import com.codeperfection.authservice.exception.dto.ErrorType
import org.springframework.http.HttpStatus

abstract class ClientErrorException(
    val status: HttpStatus,
    val errorType: ErrorType,
    message: String
) : RuntimeException(message)
