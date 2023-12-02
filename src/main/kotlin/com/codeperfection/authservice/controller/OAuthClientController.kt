package com.codeperfection.authservice.controller

import com.codeperfection.authservice.dto.CreateOAuthClientDto
import com.codeperfection.authservice.dto.OAuthClientDto
import com.codeperfection.authservice.service.OAuthClientService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/oauth-clients")
class OAuthClientController(private val oauthClientService: OAuthClientService) {

    @PostMapping
    fun createOauthClient(
        @Valid @RequestBody createOauthClientDto: CreateOAuthClientDto
    ): ResponseEntity<OAuthClientDto> = ResponseEntity.ok(oauthClientService.createOAuthClient(createOauthClientDto))
}
