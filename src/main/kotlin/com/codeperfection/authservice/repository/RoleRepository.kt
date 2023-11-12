package com.codeperfection.authservice.repository

import com.codeperfection.authservice.entity.Role
import com.codeperfection.authservice.entity.RoleName
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface RoleRepository : JpaRepository<Role, UUID?> {
    fun findByName(roleName: RoleName): Role?
}
