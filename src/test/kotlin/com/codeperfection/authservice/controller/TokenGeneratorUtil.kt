package com.codeperfection.authservice.controller

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWKMatcher
import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenGeneratorUtil(private val jwkSource: JWKSource<SecurityContext>) {

    fun generate(userId: UUID): String {
        val rsaKey = (jwkSource.get(JWKSelector(JWKMatcher.Builder().build()), null)[0] as RSAKey)
        val signer: JWSSigner = RSASSASigner(rsaKey)

        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.keyID).build(),
            JWTClaimsSet.Builder()
                .subject(userId.toString())
                .issuer("some-issuer")
                .expirationTime(Date(System.currentTimeMillis() + 3600 * 1000))
                .issueTime(Date())
                .jwtID(UUID.randomUUID().toString())
                .build()
        )

        signedJWT.sign(signer)
        return signedJWT.serialize()
    }
}
