package com.codeperfection.authservice.security

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jdbcTemplate: JdbcTemplate
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http
            // Redirect to the login page from the authorization endpoint
            .exceptionHandling {
                it.defaultAuthenticationEntryPointFor(
                    LoginUrlAuthenticationEntryPoint("/login"),
                    MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            }
        return http.build()
    }

    @Bean
    fun jwtAccessTokenCustomizer(
        @Value("\${auth-server.issuer-name}") issuerName: String
    ): OAuth2TokenCustomizer<JwtEncodingContext> = OAuth2TokenCustomizer { context ->
        if (context.tokenType.equals(OAuth2TokenType.ACCESS_TOKEN)) {
            // Set a custom issuer instead of using localhost in the name
            context.claims.claim("iss", issuerName)

            // The "sub" field of the access token is set as the client ID for client credentials flow.
            // This creates ambiguity between tokens issued for authorization code flow (where a user is involved and
            // user ID is in the token's "sub" field) and client credentials flow (where no user is involved). Hence, we
            // add a custom "flow" claim to later distinguish and resolve the ambiguity.
            context.claims.claim("flow", context.authorizationGrantType.value)
        }
    }

    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Accept JWT tokens for endpoints like getting user details
            .oauth2ResourceServer {
                it.jwt {}
            }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    EndpointRequest.toAnyEndpoint(),
                    antMatcher(HttpMethod.POST, "/**/users"),
                    antMatcher(HttpMethod.POST, "/**/oauth-clients"),
                    antMatcher("/error")
                )
                    .permitAll()
                    .anyRequest().authenticated()
            }
            // Form login handles the redirect to the login page
            .formLogin(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository = JdbcRegisteredClientRepository(jdbcTemplate)

    @Bean
    fun oauth2AuthorizationService(
        jdbcOperations: JdbcOperations,
        registeredClientRepository: RegisteredClientRepository
    ): OAuth2AuthorizationService =
        JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository)

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder =
        OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings =
        AuthorizationServerSettings.builder().build()
}
