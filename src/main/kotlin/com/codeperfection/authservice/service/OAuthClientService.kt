package com.codeperfection.authservice.service

import com.codeperfection.authservice.dto.CreateOAuthClientDto
import com.codeperfection.authservice.dto.OAuthClientDto
import com.codeperfection.authservice.exception.clienterror.OAuthClientIdTakenException
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant
import java.util.*

@Service
class OAuthClientService(
    private val registeredClientRepository: RegisteredClientRepository,
    private val passwordEncoder: PasswordEncoder,
    private val clock: Clock
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun createOAuthClient(clientDto: CreateOAuthClientDto): OAuthClientDto {
        logger.info("Creating OAuth client with Client ID ${clientDto.clientId}")
        if (registeredClientRepository.findByClientId(clientDto.clientId) != null) {
            throw OAuthClientIdTakenException(clientDto.clientId)
        }
        val oauthClientId = UUID.randomUUID()
        val registeredClient = RegisteredClient.withId(oauthClientId.toString())
            .clientId(clientDto.clientId)
            .clientIdIssuedAt(Instant.now(clock))
            .clientSecret(passwordEncoder.encode(clientDto.clientSecret))
            .clientName(clientDto.clientName)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri(clientDto.redirectUri)
            .scopes { it.addAll(clientDto.scopes) }
            .build()

        registeredClientRepository.save(registeredClient)

        return OAuthClientDto(
            id = oauthClientId,
            clientName = clientDto.clientName,
            redirectUri = clientDto.redirectUri,
            scopes = clientDto.scopes
        )
    }
}
