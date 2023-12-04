package com.codeperfection.authservice.service

import com.codeperfection.authservice.dto.AuthGrantType
import com.codeperfection.authservice.dto.CreateOAuthClientDto
import com.codeperfection.authservice.dto.OAuthClientDto
import com.codeperfection.authservice.exception.clienterror.OAuthClientIdTakenException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import java.time.Clock
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class OAuthClientServiceTest {

    @Mock
    private lateinit var registeredClientRepository: RegisteredClientRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var clock: Clock

    @InjectMocks
    private lateinit var underTest: OAuthClientService

    @AfterEach
    fun tearDown() {
        verifyNoMoreInteractions(registeredClientRepository, passwordEncoder, clock)
    }

    @Test
    fun `GIVEN client with id already exist, WHEN creating oauth client, THEN expected exception is thrown`() {
        val clientId = "b6b760f4-fa63-402f-8ab7-acfac1225f95"
        val existingClient = RegisteredClient
            .withId("06f0695e-314c-49ca-a324-5b8588443a9f")
            .clientId(clientId)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("someUri")
            .build()
        doReturn(existingClient).`when`(registeredClientRepository).findByClientId(clientId)

        assertThrows<OAuthClientIdTakenException> {
            underTest.createOAuthClient(
                CreateOAuthClientDto(
                    clientId = clientId,
                    clientSecret = "someSecret",
                    clientName = "someName",
                    authorizationGrantTypes = listOf(AuthGrantType.AUTHORIZATION_CODE, AuthGrantType.REFRESH_TOKEN),
                    redirectUri = "someUri",
                    scopes = listOf("users:write", "users:read")
                )
            )
        }

        verify(registeredClientRepository).findByClientId(clientId)
    }

    @Test
    fun `GIVEN no client with given id exist, WHEN creating oauth client, THEN client is created and returned`() {
        val clientId = "b6b760f4-fa63-402f-8ab7-acfac1225f95"
        doReturn(null).`when`(registeredClientRepository).findByClientId(clientId)

        val clientSecret = "someSecret"
        val encodedSecret = "encodedSecret"
        doReturn(encodedSecret).`when`(passwordEncoder).encode(clientSecret)

        val now = Instant.parse("2023-01-01T09:00:00.00Z")
        doReturn(now).`when`(clock).instant()

        val clientName = "someName"
        val redirectUri = "someUri"
        val scopes = listOf("users:write", "users:read")
        val authorizationGrantTypes = listOf(AuthGrantType.AUTHORIZATION_CODE, AuthGrantType.REFRESH_TOKEN)
        val result = underTest.createOAuthClient(
            CreateOAuthClientDto(
                clientId = clientId,
                clientSecret = clientSecret,
                clientName = clientName,
                authorizationGrantTypes = authorizationGrantTypes,
                redirectUri = redirectUri,
                scopes = scopes)
        )

        val registeredClientArgumentCaptor = ArgumentCaptor.forClass(RegisteredClient::class.java)
        verify(registeredClientRepository).save(registeredClientArgumentCaptor.capture())
        val savedRegisteredClient = registeredClientArgumentCaptor.value

        val generatedId = savedRegisteredClient.id
        val expectedRegisteredClient = RegisteredClient
            .withId(generatedId)
            .clientId(clientId)
            .clientIdIssuedAt(now)
            .clientSecret(encodedSecret)
            .clientName(clientName)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("someUri")
            .scope("users:write")
            .scope("users:read")
            .build()
        assertEquals(expectedRegisteredClient, savedRegisteredClient)
        val expected = OAuthClientDto(
            UUID.fromString(generatedId),
            clientName,
            authorizationGrantTypes,
            redirectUri,
            scopes
        )
        assertEquals(expected, result)

        verify(registeredClientRepository).findByClientId(clientId)
        verify(clock).instant()
        verify(passwordEncoder).encode(clientSecret)
    }
}
