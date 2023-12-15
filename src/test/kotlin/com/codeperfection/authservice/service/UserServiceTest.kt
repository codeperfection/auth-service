package com.codeperfection.authservice.service

import com.codeperfection.authservice.dto.CreateUserDto
import com.codeperfection.authservice.dto.UserDto
import com.codeperfection.authservice.entity.Role
import com.codeperfection.authservice.entity.RoleName
import com.codeperfection.authservice.entity.User
import com.codeperfection.authservice.exception.InternalServerErrorException
import com.codeperfection.authservice.exception.clienterror.EmailAlreadyTakenException
import com.codeperfection.authservice.exception.clienterror.UserNotFoundException
import com.codeperfection.authservice.repository.RoleRepository
import com.codeperfection.authservice.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.*
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var roleRepository: RoleRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var clock: Clock

    @InjectMocks
    private lateinit var underTest: UserService

    @AfterEach
    fun tearDown() {
        verifyNoMoreInteractions(userRepository, roleRepository, passwordEncoder)
    }

    @Test
    fun `GIVEN email already exist, WHEN creating user, THEN expected exception is thrown`() {
        val email = "some@email.com"
        whenever(userRepository.existsByEmail(email)).thenReturn(true)

        assertThatThrownBy {
            underTest.createUser(
                CreateUserDto(
                    email = email,
                    password = "somePassword",
                    name = "someName"
                )
            )
        }.isInstanceOf(EmailAlreadyTakenException::class.java)

        verify(userRepository).existsByEmail(email)
    }

    @Test
    fun `GIVEN db doesn't have pre-configured role, WHEN creating user, THEN expected exception is thrown`() {
        val email = "some@email.com"
        whenever(userRepository.existsByEmail(email)).thenReturn(false)
        whenever(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(null)

        assertThatThrownBy {
            underTest.createUser(
                CreateUserDto(
                    email = email,
                    password = "somePassword",
                    name = "someName"
                )
            )
        }.isInstanceOf(InternalServerErrorException::class.java)

        verify(userRepository).existsByEmail(email)
        verify(roleRepository).findByName(RoleName.ROLE_USER)
    }

    @Test
    fun `GIVEN non-existing user creation request, WHEN creating user, THEN user created and dto returned`() {
        val email = "some@email.com"
        whenever(userRepository.existsByEmail(email)).thenReturn(false)
        val role = Role(UUID.fromString("291f0f30-ff56-4d67-9417-5ad43d7001d0"), RoleName.ROLE_USER)
        whenever(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(role)

        val now = Instant.parse("2023-01-01T09:00:00.00Z")
        whenever(clock.instant()).thenReturn(now)
        whenever(clock.zone).thenReturn(ZoneId.of("UTC"))

        val password = "somePassword"
        val encodedPassword = "encodedPassword"
        whenever(passwordEncoder.encode(password)).thenReturn(encodedPassword)

        val mockedUserId = UUID.fromString("e8c2545d-d991-4adb-892a-2123ae05e334")
        val name = "someName"
        val savedUser = User(
            id = mockedUserId,
            email = email,
            password = encodedPassword,
            name = name,
            createdAt = now.atOffset(ZoneOffset.UTC),
            updatedAt = now.atOffset(ZoneOffset.UTC),
            roles = setOf(role)
        )
        whenever(userRepository.save(any())).thenReturn(savedUser) // actual argument value will be checked wit captor

        val result = underTest.createUser(CreateUserDto(email, password, name))
        assertThat(result).isEqualTo(UserDto(mockedUserId, email, name))

        verify(userRepository).existsByEmail(email)
        verify(roleRepository).findByName(RoleName.ROLE_USER)
        verify(clock).instant()
        verify(clock).zone
        verify(passwordEncoder).encode(password)

        val userArgumentCaptor = argumentCaptor<User>()
        verify(userRepository).save(userArgumentCaptor.capture())
        val actualSavedUser = userArgumentCaptor.firstValue
        assertThat(actualSavedUser).isEqualTo(savedUser.copy(id = actualSavedUser.id))
    }

    @Test
    fun `GIVEN missing user entity in db, WHEN getting user, THEN expected exception is thrown`() {
        val userId = UUID.fromString("5c557f7e-1849-435a-a3c7-cf342fb8c380")
        whenever(userRepository.findById(userId)).thenReturn(Optional.empty())
        assertThatThrownBy {
            underTest.getUser(userId)
        }.isInstanceOf(UserNotFoundException::class.java)
        verify(userRepository).findById(userId)
    }

    @Test
    fun `GIVEN existing entity in db, WHEN getting user, THEN expected result is returned`() {
        val userId = UUID.fromString("5c557f7e-1849-435a-a3c7-cf342fb8c380")
        val email = "someEmail"
        val name = "someName"
        val user = User(
            id = userId,
            email = email,
            password = "someEncodedPassword",
            name = name,
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now(),
            roles = setOf(Role(UUID.fromString("5c557f7e-1849-435a-a3c7-cf342fb8c380"), RoleName.ROLE_USER))
        )
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        assertThat(underTest.getUser(userId)).isEqualTo(UserDto(userId, email, name))
        verify(userRepository).findById(userId)
    }
}
