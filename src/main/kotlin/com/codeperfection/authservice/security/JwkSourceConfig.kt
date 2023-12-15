package com.codeperfection.authservice.security

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder
import org.bouncycastle.operator.InputDecryptorProvider
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.StringReader
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Configuration
class JwkSourceConfig(@Value("\${auth-server.jwt-private-key-password}") private val jwtPrivateKeyPassword: String) {

    companion object {
        private const val KEY_ID = "9c994f2e-d040-4a04-941c-b4c54e2cecd8"
        private const val ENCODED_PRIVATE_KEY_LOCATION = "keys/jwt_private_key_encoded.pem"
        private const val PUBLIC_KEY_LOCATION = "keys/jwt_public_key.pem"
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> = ImmutableJWKSet(
        JWKSet(
            RSAKey.Builder(readPublicKey())
                .privateKey(readPrivateKey())
                .keyID(KEY_ID)
                .build()
        )
    )

    private fun readPrivateKey() = KeyFactory.getInstance("RSA").generatePrivate(
        // PKCS#8 stands for "Public-Key Cryptography Standards #8" and is a syntax for storing private key information
        PKCS8EncodedKeySpec(
            decryptPrivateKey(
                readFileContent(ENCODED_PRIVATE_KEY_LOCATION)
            )
        )
    ) as RSAPrivateKey

    private fun readPublicKey() = KeyFactory.getInstance("RSA").generatePublic(
        // X.509 is a standard defining the format of public key certificates
        X509EncodedKeySpec(
            Base64.getDecoder().decode(
                extractEncodedPartOfPublicKey(readFileContent(PUBLIC_KEY_LOCATION))
            )
        )
    ) as RSAPublicKey

    private fun readFileContent(location: String) = ClassPathResource(location)
        .inputStream.bufferedReader().use { it.readText() }

    private fun decryptPrivateKey(encryptedKey: String): ByteArray =
        toEncryptedPrivateKeyInfo(encryptedKey).decryptPrivateKeyInfo(decryptorProvider()).encoded

    private fun decryptorProvider(): InputDecryptorProvider = JceOpenSSLPKCS8DecryptorProviderBuilder()
        .setProvider(BouncyCastleProvider()).build(jwtPrivateKeyPassword.trim().toCharArray())

    private fun toEncryptedPrivateKeyInfo(encryptedKey: String) =
        PEMParser(StringReader(encryptedKey)).readObject() as PKCS8EncryptedPrivateKeyInfo

    private fun extractEncodedPartOfPublicKey(publicKeyBase64WithHeaders: String) = publicKeyBase64WithHeaders
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replace("\r\n", "")
        .replace("\n", "")
        .trim() // Remove the PEM headers/footers and newlines
}
