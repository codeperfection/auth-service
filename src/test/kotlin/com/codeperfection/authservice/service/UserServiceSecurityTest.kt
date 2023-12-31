package com.codeperfection.authservice.service

import com.codeperfection.authservice.AuthServiceApplication
import com.codeperfection.authservice.repository.UserRepository
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [AuthServiceApplication::class])
class UserServiceSecurityTest {

    @MockBean
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var underTest: UserService

    @Test
    fun `GIVEN not authenticated user, WHEN getting user, THEN expected exception is thrown`() {
        val userId = UUID.fromString("5c557f7e-1849-435a-a3c7-cf342fb8c380")
        assertThatThrownBy {
            underTest.getUser(userId)
        }.isInstanceOf(AuthenticationCredentialsNotFoundException::class.java)
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_other"])
    fun `GIVEN authenticated user doesn't have needed rights, WHEN getting user, THEN expected exception is thrown`() {
        val userId = UUID.fromString("5c557f7e-1849-435a-a3c7-cf342fb8c380")
        assertThatThrownBy {
            underTest.getUser(userId)
        }.isInstanceOf(AccessDeniedException::class.java)
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_users:read"])
    fun `GIVEN correctly authenticated user, WHEN getting user, THEN authorization check is successful`() {
        val userId = UUID.fromString("5c557f7e-1849-435a-a3c7-cf342fb8c380")
        whenever(userRepository.findById(userId)).thenReturn(Optional.empty())
        try {
            underTest.getUser(userId)
        } catch (_: Exception) {
            // We don't care what kind of exception is thrown (if any)
            // We just need to make sure afterword that authorization layer check passed
        }
        // as this call happened, we can be sure that authorization check passed
        verify(userRepository).findById(userId)
    }
}
