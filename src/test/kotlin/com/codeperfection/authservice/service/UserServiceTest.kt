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
        doReturn(true).`when`(userRepository).existsByEmail(email)

        assertThrows<EmailAlreadyTakenException> {
            underTest.createUser(
                CreateUserDto(
                    email = email,
                    password = "somePassword",
                    name = "someName"
                )
            )
        }

        verify(userRepository).existsByEmail(email)
    }

    @Test
    fun `GIVEN db doesn't have pre-configured role, WHEN creating user, THEN expected exception is thrown`() {
        val email = "some@email.com"
        doReturn(false).`when`(userRepository).existsByEmail(email)
        doReturn(null).`when`(roleRepository).findByName(RoleName.ROLE_USER)

        assertThrows<InternalServerErrorException> {
            underTest.createUser(
                CreateUserDto(
                    email = email,
                    password = "somePassword",
                    name = "someName"
                )
            )
        }

        verify(userRepository).existsByEmail(email)
        verify(roleRepository).findByName(RoleName.ROLE_USER)
    }

    @Test
    fun `GIVEN non-existing user creation request, WHEN creating user, THEN user created and dto returned`() {
        val email = "some@email.com"
        doReturn(false).`when`(userRepository).existsByEmail(email)
        val role = Role(UUID.fromString("291f0f30-ff56-4d67-9417-5ad43d7001d0"), RoleName.ROLE_USER)
        doReturn(role).`when`(roleRepository).findByName(RoleName.ROLE_USER)

        val now = Instant.parse("2023-01-01T09:00:00.00Z")
        doReturn(now).`when`(clock).instant()
        doReturn(ZoneId.of("UTC")).`when`(clock).zone

        val password = "somePassword"
        val encodedPassword = "encodedPassword"
        doReturn(encodedPassword).`when`(passwordEncoder).encode(password)

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
        doReturn(savedUser).`when`(userRepository).save(any()) // actual argument value will be checked wit captor

        val result = underTest.createUser(CreateUserDto(email, password, name))
        assertEquals(UserDto(mockedUserId, email, name), result)

        verify(userRepository).existsByEmail(email)
        verify(roleRepository).findByName(RoleName.ROLE_USER)
        verify(clock).instant()
        verify(clock).zone
        verify(passwordEncoder).encode(password)

        val userArgumentCaptor = ArgumentCaptor.forClass(User::class.java)
        verify(userRepository).save(userArgumentCaptor.capture())
        val actualSavedUser = userArgumentCaptor.value
        assertEquals(savedUser.copy(id = actualSavedUser.id), actualSavedUser)
    }

    @Test
    fun `GIVEN missing user entity in db, WHEN getting user, THEN expected exception is thrown`() {
        val userId = UUID.fromString("5c557f7e-1849-435a-a3c7-cf342fb8c380")
        doReturn(Optional.empty<User>()).`when`(userRepository).findById(userId)
        assertThrows<UserNotFoundException> {
            underTest.getUser(userId)
        }
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
        doReturn(Optional.of(user)).`when`(userRepository).findById(userId)
        assertEquals(UserDto(userId, email, name), underTest.getUser(userId))
        verify(userRepository).findById(userId)
    }
}
