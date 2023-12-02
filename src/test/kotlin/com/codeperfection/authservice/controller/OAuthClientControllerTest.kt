package com.codeperfection.authservice.controller

import com.codeperfection.authservice.dto.CreateOAuthClientDto
import com.codeperfection.authservice.dto.OAuthClientDto
import com.codeperfection.authservice.exception.dto.ErrorType
import com.codeperfection.authservice.service.OAuthClientService
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.json.JSONArray
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
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
    fun `GIVEN valid request, WHEN creating oauth client, THAN expected successful response is given`() {
        val expectedRequestDto = CreateOAuthClientDto(
            clientId = "someClient",
            clientSecret = "someSecret",
            clientName = "someName",
            redirectUri = "someUri",
            scopes = listOf("users:read", "users:write")
        )
        val responseDto = OAuthClientDto(
            id = UUID.fromString("28fafb5b-ab73-45a8-aba8-4268d6ff159f"),
            clientName = "someClient",
            redirectUri = "someUri",
            scopes = listOf("users:read", "users:write")
        )
        doReturn(responseDto).`when`(oauthClientService).createOAuthClient(expectedRequestDto)

        mockMvc.perform(
            post("/api/v1/oauth-clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "clientId": "someClient",
                        "clientSecret": "someSecret",
                        "clientName": "someName",
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
    fun `GIVEN invalid request, WHEN creating oauth client, THAN request fails with client error and expected error details`() {
        mockMvc.perform(
            post("/api/v1/oauth-clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "clientId": "aaa",
                        "clientSecret": "aaa",
                        "clientName": "aaa",
                        "redirectUri": "aaa",
                        "scopes": [
                        ]
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("errorType", `is`(ErrorType.INVALID_REQUEST.name)))
            .andExpect(jsonPath("fieldErrors", hasSize<JSONArray>(5)))
    }
}
