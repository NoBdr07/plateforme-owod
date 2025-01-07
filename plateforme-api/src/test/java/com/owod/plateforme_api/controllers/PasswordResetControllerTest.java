package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class PasswordResetControllerTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection("users");
    }

    @Test
    void resetPassword_shouldReturnSuccess() throws Exception {
        // GIVEN

        String requestJson = """
                {
                    "token": "token123",
                    "newPassword": "newPassword"
                }
                """;

        User user = new User();
        user.setUserId("user123");
        user.setPassword("oldPassword");
        user.setResetToken("token123");
        LocalDateTime time = LocalDateTime.now().plusHours(1);
        user.setResetTokenExpiry(time);
        mongoTemplate.save(user);

        // WHEN & THEN
        mockMvc.perform(post("/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

        User resultUser = mongoTemplate.findById("user123", User.class);
        assertNotNull(resultUser); // Assurez-vous que l'utilisateur existe
        assertTrue(new BCryptPasswordEncoder().matches("newPassword", resultUser.getPassword())); // Vérifiez le mot de passe encodé



    }
}