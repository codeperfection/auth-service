package com.codeperfection.authservice.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.util.*

@Entity
data class Role(
    @Id
    val id: UUID,
    @Enumerated(EnumType.STRING)
    val name: RoleName
)
