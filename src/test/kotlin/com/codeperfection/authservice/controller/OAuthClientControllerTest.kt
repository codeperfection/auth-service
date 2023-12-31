package com.codeperfection.authservice.controller

import com.codeperfection.authservice.dto.AuthGrantType
import com.codeperfection.authservice.dto.CreateOAuthClientDto
import com.codeperfection.authservice.dto.OAuthClientDto
import com.codeperfection.authservice.exception.dto.ErrorType
import com.codeperfection.authservice.service.OAuthClientService
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.json.JSONArray
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

class OAuthClientControllerTest : ControllerTestBase() {

    @MockBean
    private lateinit var oauthClientService: OAuthClientService

    @AfterEach
    fun tearDown() {
        verifyNoMoreInteractions(oauthClientService)
    }

    @Test
    fun `GIVEN valid request, WHEN creating oauth client, THEN expected successful response is given`() {
        val authorizationGrantTypes = listOf(
            AuthGrantType.AUTHORIZATION_CODE,
            AuthGrantType.REFRESH_TOKEN,
            AuthGrantType.CLIENT_CREDENTIALS
        )
        val expectedRequestDto = CreateOAuthClientDto(
            clientId = "someClient",
            clientSecret = "someSecret",
            clientName = "someName",
            authorizationGrantTypes = authorizationGrantTypes,
            redirectUri = "someUri",
            scopes = listOf("users:read", "users:write"),
        )
        val responseDto = OAuthClientDto(
            id = UUID.fromString("28fafb5b-ab73-45a8-aba8-4268d6ff159f"),
            clientName = "someClient",
            authorizationGrantTypes = authorizationGrantTypes,
            redirectUri = "someUri",
            scopes = listOf("users:read", "users:write")
        )
        whenever(oauthClientService.createOAuthClient(expectedRequestDto)).thenReturn(responseDto)

        mockMvc.perform(
            post("/api/v1/oauth-clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "clientId": "someClient",
                        "clientSecret": "someSecret",
                        "clientName": "someName",
                        "authorizationGrantTypes": [
                            "AUTHORIZATION_CODE",
                            "REFRESH_TOKEN",
                            "CLIENT_CREDENTIALS"
                        ],
                        "redirectUri": "someUri",
                        "scopes": [
                            "users:read",
                            "users:write"
                        ]
                    }
                """.trimIndent()
                )
        )
            .andExpect(status().is2xxSuccessful)
            .andExpect(
                content().json(
                    """
                    {
                        "id": "28fafb5b-ab73-45a8-aba8-4268d6ff159f",
                        "clientName": "someClient",
                        "authorizationGrantTypes": [
                            "AUTHORIZATION_CODE",
                            "REFRESH_TOKEN",
                            "CLIENT_CREDENTIALS"
                        ],
                        "redirectUri": "someUri",
                        "scopes": [
                            "users:read",
                            "users:write"
                        ]
                    }
                    """.trimIndent(), true
                )
            )

        verify(oauthClientService).createOAuthClient(expectedRequestDto)
    }

    @Test
    fun `GIVEN invalid request, WHEN creating oauth client, THEN request fails with client error and expected error details`() {
        mockMvc.perform(
            post("/api/v1/oauth-clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "clientId": "aaa",
                        "clientSecret": "aaa",
                        "clientName": "aaa",
                        "authorizationGrantTypes": [
                            "REFRESH_TOKEN"
                        ],
                        "redirectUri": "aaa",
                        "scopes": [
                        ]
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("errorType", `is`(ErrorType.INVALID_REQUEST.name)))
            .andExpect(jsonPath("fieldErrors", hasSize<JSONArray>(6)))
    }

    @Test
    fun `GIVEN empty authorization grant types provided, WHEN creating oauth client, THEN request fails with client error`() {
        mockMvc.perform(
            post("/api/v1/oauth-clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "clientId": "someClient",
                        "clientSecret": "someSecret",
                        "clientName": "someName",
                        "authorizationGrantTypes": [
                        ],
                        "redirectUri": "someUri",
                        "scopes": [
                            "users:read",
                            "users:write"
                        ]
                    }
                """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
    }
}
