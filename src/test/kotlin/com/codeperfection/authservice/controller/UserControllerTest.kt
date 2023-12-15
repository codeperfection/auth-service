package com.codeperfection.authservice.controller

import com.codeperfection.authservice.dto.CreateUserDto
import com.codeperfection.authservice.dto.UserDto
import com.codeperfection.authservice.exception.dto.ErrorType
import com.codeperfection.authservice.service.UserService
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.json.JSONArray
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

class UserControllerTest : ControllerTestBase() {

    @MockBean
    private lateinit var userService: UserService

    @AfterEach
    fun tearDown() {
        verifyNoMoreInteractions(userService)
    }

    @Test
    fun `GIVEN valid request, WHEN signing up user, THEN expected successful response is given`() {
        val expectedRequestDto = CreateUserDto(
            email = "test@test.com",
            password = "verySecret",
            name = "someName"
        )
        val responseDto = UserDto(
            id = UUID.fromString("759a7cbc-06fd-4d7e-a0b2-c50eb78509d7"),
            email = "test@test.com",
            name = "someName"
        )
        whenever(userService.createUser(expectedRequestDto)).thenReturn(responseDto)

        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "email": "test@test.com",
                        "password": "verySecret",
                        "name": "someName"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().is2xxSuccessful)
            .andExpect(
                content().json(
                    """
                    {
                        "id": "759a7cbc-06fd-4d7e-a0b2-c50eb78509d7",
                        "email": "test@test.com",
                        "name": "someName"
                    }
                    """.trimIndent()
                )
            )

        verify(userService).createUser(expectedRequestDto)
    }

    @Test
    fun `GIVEN request with wrong email string, WHEN signing up user, THEN request fails with client error and expected error details`() {
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "email": "wrongEmailAddress",
                        "password": "verySecret",
                        "name": "someName"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("errorType", `is`(ErrorType.INVALID_REQUEST.name)))
            .andExpect(jsonPath("fieldErrors", Matchers.hasSize<JSONArray>(1)))
    }

    @Test
    fun `GIVEN invalid request, WHEN signing up user, THEN request fails with client error and expected error details`() {
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "email": "",
                        "password": "smo",
                        "name": ""
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("errorType", `is`(ErrorType.INVALID_REQUEST.name)))
            .andExpect(jsonPath("fieldErrors", Matchers.hasSize<JSONArray>(3)))
    }

    @Test
    @WithMockUser
    fun `GIVEN valid authorization header, WHEN getting current user, THEN expected response is returned`() {
        val userId = UUID.fromString("759a7cbc-06fd-4d7e-a0b2-c50eb78509d7")
        val responseDto = UserDto(
            id = userId,
            email = "test@test.com",
            name = "someName"
        )
        whenever(userService.getUser(userId)).thenReturn(responseDto)

        mockMvc.perform(get("/api/v1/users/$userId"))
            .andExpect(status().is2xxSuccessful)
            .andExpect(
                content().json(
                    """
                    {
                        "id": "759a7cbc-06fd-4d7e-a0b2-c50eb78509d7",
                        "email": "test@test.com",
                        "name": "someName"
                    }
                    """.trimIndent()
                )
            )

        verify(userService).getUser(userId)
    }

    @Test
    fun `GIVEN request without valid authorization token, WHEN getting current user, THEN 401 is returned`() {
        mockMvc.perform(get("/api/v1/users/me"))
            .andExpect(status().isUnauthorized)
    }
}
