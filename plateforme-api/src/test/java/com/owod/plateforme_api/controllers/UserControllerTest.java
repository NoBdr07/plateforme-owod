package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.configuration.TestSecurityConfig;
import com.owod.plateforme_api.models.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection("users");
    }

    @Test
    @WithMockUser
    void hasDesignerId_shouldReturnTrue() throws Exception {
        // GIVEN
        User user = new User();
        user.setUserId("user123");
        user.setDesignerId("designer123");
        mongoTemplate.save(user);

        // WHEN & THEN
        mockMvc.perform(get("/users/user123/has-designer"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser
    void hasDesignerId_shouldReturnFalse() throws Exception {
        // GIVEN
        User user = new User();
        user.setUserId("user123");
        mongoTemplate.save(user);

        // WHEN & THEN
        mockMvc.perform(get("/users/user123/has-designer"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @WithMockUser
    void getUser_shouldReturnUser() throws Exception {
        // GIVEN
        User user = new User();
        user.setUserId("user123");
        user.setDesignerId("designer123");
        user.setEmail("test@mail.com");
        user.setFirstname("John");
        user.setLastname("Doe");
        mongoTemplate.save(user);

        // WHEN & THEN
        mockMvc.perform(get("/users/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123")) // Vérifie l'ID utilisateur
                .andExpect(jsonPath("$.designerId").value("designer123")) // Vérifie l'ID designer
                .andExpect(jsonPath("$.email").value("test@mail.com")) // Vérifie l'email
                .andExpect(jsonPath("$.firstname").value("John")) // Vérifie le prénom
                .andExpect(jsonPath("$.lastname").value("Doe")); // Vérifie le nom de famille
    }

    @Test
    @WithMockUser
    void getUser_shouldReturnNotFound() throws Exception {

        // WHEN & THEN
        mockMvc.perform(get("/users/user123"))
                .andExpect(status().isNotFound());
    }
}