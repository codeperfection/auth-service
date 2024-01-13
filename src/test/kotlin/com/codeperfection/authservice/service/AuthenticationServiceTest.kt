package com.codeperfection.authservice.service

import com.codeperfection.authservice.exception.clienterror.UnauthorizedException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.*

class AuthenticationServiceTest {

    private val underTest = AuthenticationService()

    private val tokenClaims = mutableMapOf<String, Any>()

    private val userId = UUID.fromString("5c557f7e-1849-435a-a3c7-cf342fb8c380")

    @BeforeEach
    fun setUp() {
        val jwt = mock<Jwt>()
        whenever(jwt.claims).thenReturn(tokenClaims)

        val jwtAuthenticationToken = mock<JwtAuthenticationToken>()
        whenever(jwtAuthenticationToken.token).thenReturn(jwt)

        val securityContext = mock<SecurityContext>()
        whenever(securityContext.authentication).thenReturn(jwtAuthenticationToken)

        SecurityContextHolder.setContext(securityContext)
    }

    @Test
    fun `GIVEN non-JWT authentication, WHEN checking access, THEN unauthorized exception is thrown`() {
        val securityContext = mock<SecurityContext>()
        whenever(securityContext.authentication).thenReturn(mock<UsernamePasswordAuthenticationToken>())
        SecurityContextHolder.setContext(securityContext)

        assertThrows<UnauthorizedException> { underTest.checkReadAccess(userId) }
    }

    @Test
    fun `GIVEN missing scope in access token, WHEN checking access, THEN unauthorized exception is thrown`() {
        tokenClaims["scope"] = listOf("invalidScope")
        assertThrows<UnauthorizedException> { underTest.checkReadAccess(userId) }
    }

    @Test
    fun `GIVEN flow claim is not set in access token, WHEN checking access, THEN unauthorized exception is thrown`() {
        tokenClaims["scope"] = listOf("users:read")
        tokenClaims["sub"] = userId.toString()
        assertThrows<UnauthorizedException> { underTest.checkReadAccess(userId) }
    }

    @Test
    fun `GIVEN token with authorization code flow, WHEN checking access for different user ID, THEN unauthorized exception is thrown`() {
        tokenClaims["scope"] = listOf("users:read")
        tokenClaims["sub"] = userId.toString()
        tokenClaims["flow"] = AuthorizationGrantType.AUTHORIZATION_CODE.value

        val differentUserId = UUID.fromString("4705e910-0a8f-4387-849a-abb30bfa3205")
        assertThrows<UnauthorizedException> { underTest.checkReadAccess(differentUserId) }
    }

    @Test
    fun `GIVEN token with authorization code flow, WHEN checking access for same user ID, THEN no exception is thrown`() {
        tokenClaims["scope"] = listOf("users:read")
        tokenClaims["sub"] = userId.toString()
        tokenClaims["flow"] = AuthorizationGrantType.AUTHORIZATION_CODE.value

        assertDoesNotThrow { underTest.checkReadAccess(userId) }
    }

    @Test
    fun `GIVEN token with client credentials flow, WHEN checking access for any user ID, THEN no exception is thrown`() {
        tokenClaims["scope"] = listOf("users:read")
        tokenClaims["sub"] = "clientId"
        tokenClaims["flow"] = AuthorizationGrantType.CLIENT_CREDENTIALS.value

        assertDoesNotThrow { underTest.checkReadAccess(userId) }
    }
}
