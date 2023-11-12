package com.codeperfection.authservice.exception

import com.codeperfection.authservice.exception.clienterror.ClientErrorException
import com.codeperfection.authservice.exception.dto.ApiError
import com.codeperfection.authservice.exception.dto.ApiFieldError
import com.codeperfection.authservice.exception.dto.ErrorType
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ClientErrorException::class)
    fun clientErrorException(e: ClientErrorException): ResponseEntity<ApiError> {
        logger.warn(e.message)
        return ResponseEntity.status(e.status).contentType(MediaType.APPLICATION_JSON)
            .body(ApiError(status = e.status.value(), errorType = e.errorType, message = e.message ?: ""))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun httpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ApiError> {
        logger.warn(e.message)
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
            .body(ApiError(errorType = ErrorType.INVALID_REQUEST, message = e.message ?: ""))
    }

    @ExceptionHandler(BindException::class)
    fun bindException(e: BindException): ResponseEntity<ApiFieldError> {
        logger.warn(e.message)
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
            .body(
                ApiFieldError(errorType = ErrorType.INVALID_REQUEST, message = "Field validation failed",
                    fieldErrors = e.fieldErrors.map {
                        ApiFieldError.FieldErrorInfo(
                            fieldName = it.field,
                            errorMessage = it.defaultMessage ?: ""
                        )
                    })
            )
    }

    @ExceptionHandler(InternalServerErrorException::class)
    fun internalServerErrorException(e: InternalServerErrorException): ResponseEntity<ApiError> {
        logger.error(e.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
            .body(ApiError(errorType = ErrorType.INTERNAL_SERVER_ERROR, message = e.message ?: ""))
    }
}
