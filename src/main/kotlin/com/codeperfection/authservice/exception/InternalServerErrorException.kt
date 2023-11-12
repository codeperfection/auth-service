package com.codeperfection.authservice.exception

data class InternalServerErrorException(val errorMessage: String) : RuntimeException(errorMessage)
