package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.utils.JwtUtils;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection("users");
    }

    @Test
    void login_shouldReturnSuccessAndSetCookie() throws Exception {
        // GIVEN
        User user = new User();
        user.setUserId("user123");
        user.setEmail("test@mail.com");
        user.setPassword(new BCryptPasswordEncoder().encode("password123"));
        mongoTemplate.save(user);

        String loginRequestJson = """
        {
            "email": "test@mail.com",
            "password": "password123"
        }
    """;

        // WHEN & THEN
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful !"))
                .andExpect(cookie().exists("jwt"));
    }

    @Test
    void logout_shouldSetCookieAgeTo0() throws Exception{
        // GIVEN
        User user = new User();
        user.setUserId("user123");
        user.setEmail("test@mail.com");
        user.setPassword(new BCryptPasswordEncoder().encode("password123"));
        mongoTemplate.save(user);

        String loginRequestJson = """
        {
            "email": "test@mail.com",
            "password": "password123"
        }
    """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequestJson));

        // WHEN & THEN
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("jwt", 0));


    }

    @Test
    void register_shouldReturnSuccessAndSaveUser() throws Exception {
        // GIVEN
        String registerRequestJson = """
        {
            "email": "test@mail.com",
            "password": "password123",
            "firstname": "Jane",
            "lastname": "Doe",
            "admin": "false"
        }
    """;

        // WHEN & THEN
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        List<User> users = mongoTemplate.findAll(User.class);
        assertEquals(users.get(0).getFirstname(), "Jane");


    }

    @Test
    void register_shouldReturnBadRequest() throws Exception {
        // GIVEN
        User user = new User();
        user.setUserId("user123");
        user.setEmail("test@mail.com");
        user.setPassword(new BCryptPasswordEncoder().encode("password123"));
        mongoTemplate.save(user);

        String registerRequestJson = """
        {
            "email": "test@mail.com",
            "password": "password123",
            "firstname": "Jane",
            "lastname": "Doe"
        }
    """;

        // WHEN & THEN
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cet email est déjà utilisé."));

    }
}