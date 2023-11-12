package com.codeperfection.authservice.exception.clienterror

import com.codeperfection.authservice.exception.dto.ErrorType
import org.springframework.http.HttpStatus

data class OAuthClientIdTakenException(val oauthClientId: String) : ClientErrorException(
    status = HttpStatus.CONFLICT,
    errorType = ErrorType.OAUTH_CLIENT_ID_ALREADY_TAKEN,
    message = "OAuth client ID $oauthClientId is already taken"
)
