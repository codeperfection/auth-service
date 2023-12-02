package com.codeperfection.authservice.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.util.*

@Configuration
class TimeConfig {

    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Bean
    fun clock(): Clock = Clock.systemUTC()
}
