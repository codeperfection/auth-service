package com.codeperfection.authservice.security

import com.codeperfection.authservice.entity.Role
import com.codeperfection.authservice.entity.RoleName
import com.codeperfection.authservice.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.time.OffsetDateTime
import java.util.*


@ExtendWith(MockitoExtension::class)
class SecurityUserDetailsServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var underTest: SecurityUserDetailsService

    @AfterEach
    fun tearDown() {
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `GIVEN user with email doesn't exist, WHEN loading user by username (email), THEN expected exception is thrown `() {
        val email = "nonExistingEmailAddress"
        whenever(userRepository.findByEmail(email)).thenReturn(null)

        assertThatThrownBy {
            underTest.loadUserByUsername(email)
        }.isInstanceOf(UsernameNotFoundException::class.java)

        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `GIVEN user with email exists, WHEN loading user by username (email), THEN expected result returned`() {
        val email = "nonExistingEmailAddress"
        val password = "someEncodedPassword"
        val user = com.codeperfection.authservice.entity.User(
            id = UUID.fromString("1dbe1be6-d64f-4a99-bf2e-184a81419f47"),
            email = email,
            password = password,
            name = "someName",
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now(),
            roles = setOf(Role(UUID.fromString("5c557f7e-1849-435a-a3c7-cf342fb8c380"), RoleName.ROLE_USER))
        )
        whenever(userRepository.findByEmail(email)).thenReturn(user)

        val expected = User(
            "1dbe1be6-d64f-4a99-bf2e-184a81419f47",
            password,
            setOf(SimpleGrantedAuthority("ROLE_USER"))
        )
        assertThat(underTest.loadUserByUsername(email)).isEqualTo(expected)
        verify(userRepository).findByEmail(email)
    }
}
