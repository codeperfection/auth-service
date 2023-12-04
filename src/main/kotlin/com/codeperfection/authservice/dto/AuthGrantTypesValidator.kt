package com.codeperfection.authservice.dto

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [AuthGrantTypesValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidAuthGrantTypes(
    val message: String = "Invalid authorization grant types",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class AuthGrantTypesValidator : ConstraintValidator<ValidAuthGrantTypes, List<AuthGrantType>> {

    override fun isValid(authGrantTypes: List<AuthGrantType>?, context: ConstraintValidatorContext): Boolean {
        if (authGrantTypes.isNullOrEmpty()) {
            return false
        }

        val uniqueGrants = authGrantTypes.toSet()
        if (uniqueGrants.size != authGrantTypes.size) {
            return false // Contains duplicate values
        }

        // refresh token grant type cannot be provided standalone
        return !(uniqueGrants.size == 1 && uniqueGrants.contains(AuthGrantType.REFRESH_TOKEN))
    }
}
