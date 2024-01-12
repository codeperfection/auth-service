package com.codeperfection.authservice.service

import com.codeperfection.authservice.dto.CreateUserDto
import com.codeperfection.authservice.dto.UserDto
import com.codeperfection.authservice.entity.RoleName
import com.codeperfection.authservice.entity.User
import com.codeperfection.authservice.exception.InternalServerErrorException
import com.codeperfection.authservice.exception.clienterror.EmailAlreadyTakenException
import com.codeperfection.authservice.exception.clienterror.UserNotFoundException
import com.codeperfection.authservice.repository.RoleRepository
import com.codeperfection.authservice.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationService: AuthenticationService,
    private val clock: Clock
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun createUser(createUserDto: CreateUserDto): UserDto {
        if (userRepository.existsByEmail(createUserDto.email)) {
            throw EmailAlreadyTakenException(createUserDto.email)
        }

        val role = roleRepository.findByName(RoleName.ROLE_USER)
            ?: throw InternalServerErrorException("User role not found")
        val now = OffsetDateTime.now(clock)
        val user = User(
            id = UUID.randomUUID(),
            email = createUserDto.email,
            password = passwordEncoder.encode(createUserDto.password),
            name = createUserDto.name,
            createdAt = now,
            updatedAt = now,
            roles = setOf(role)
        )
        val savedUser = userRepository.save(user)
        logger.info("Created user with ID ${savedUser.id}")
        return mapToDto(savedUser)
    }

    @Transactional(readOnly = true)
    fun getUser(userId: UUID): UserDto {
        authenticationService.checkReadAccess(userId)
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException(userId)
        return mapToDto(user)
    }

    private fun mapToDto(user: User) =
        UserDto(
            id = user.id,
            email = user.email,
            name = user.name
        )
}
