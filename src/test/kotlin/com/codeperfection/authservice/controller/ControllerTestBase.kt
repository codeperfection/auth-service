package com.codeperfection.authservice.controller

import com.codeperfection.authservice.AuthServiceApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(classes = [TokenGeneratorUtil::class])
@AutoConfigureMockMvc
@ContextConfiguration(classes = [AuthServiceApplication::class])
class ControllerTestBase {

    @Autowired
    protected lateinit var mockMvc: MockMvc
}
