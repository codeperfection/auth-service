package com.codeperfection.authservice.controller

import com.codeperfection.authservice.dto.CreateUserDto
import com.codeperfection.authservice.dto.UserDto
import com.codeperfection.authservice.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun signUpUser(@Valid @RequestBody createUserDto: CreateUserDto): ResponseEntity<UserDto> =
        ResponseEntity.ok(userService.createUser(createUserDto))

    @GetMapping("/{userId}")
    fun getCurrentUser(@PathVariable userId: UUID): ResponseEntity<UserDto> =
        ResponseEntity.ok(userService.getUser(userId))
}
