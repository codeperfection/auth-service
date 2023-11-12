package com.codeperfection.authservice.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "auth_user")
data class User(
    @Id
    val id: UUID,
    val email: String,
    val password: String,
    val name: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    @ManyToMany
    @JoinTable(
        name = "user_role", joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: Set<Role>
)
