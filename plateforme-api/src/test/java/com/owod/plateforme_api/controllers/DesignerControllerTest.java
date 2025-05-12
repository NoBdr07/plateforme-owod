package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.configuration.TestSecurityConfig;
import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestSecurityConfig.class)
class DesignerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection("designers"); // Réinitialise la collection pour chaque test
        mongoTemplate.dropCollection("users");
    }

    @Test
    @WithMockUser
    void getAllDesigners_shouldReturnListOfDesigners() throws Exception {
        // GIVEN
        Designer designer1 = new Designer();
        designer1.setId("1");
        designer1.setFirstname("John");
        mongoTemplate.save(designer1);

        Designer designer2 = new Designer();
        designer2.setId("2");
        designer2.setFirstname("Jane");
        mongoTemplate.save(designer2);

        // WHEN & THEN
        mockMvc.perform(get("/designers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].firstname").value("John"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].firstname").value("Jane"));
    }

    @Test
    @WithMockUser
    void getDesignerByUserId_shouldReturnDesigner() throws Exception {
        // GIVEN
        User user = new User();
        user.setUserId("user123");
        user.setDesignerId("designer123");
        mongoTemplate.save(user);

        Designer designer = new Designer();
        designer.setId("designer123");
        designer.setFirstname("John");
        mongoTemplate.save(designer);

        // WHEN & THEN
        mockMvc.perform(get("/designers/designer/{userId}", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("designer123"))
                .andExpect(jsonPath("$.firstname").value("John"));
    }

    @Test
    @WithMockUser
    void getDesignersBySpecialty_shouldReturnDesigners() throws Exception {
        // GIVEN
        Designer designer1 = new Designer();
        designer1.setLastname("Bob");
        designer1.setSpecialties(List.of("Specialty1", "Specialty2"));
        mongoTemplate.save(designer1);

        Designer designer2 = new Designer();
        designer2.setLastname("John");
        designer2.setSpecialties(List.of("Specialty2"));
        mongoTemplate.save(designer2);

        Designer designer3 = new Designer();
        designer3.setLastname("James");
        designer3.setSpecialties(List.of("Specialty1", "Specialty3"));
        mongoTemplate.save(designer3);

        // WHEN & THEN
        mockMvc.perform(get("/designers/specialty").param("specialty", "Specialty1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].lastname").value("Bob"))
                .andExpect(jsonPath("$[1].lastname").value("James"));

    }

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    void createNewDesigner() throws Exception {
        // GIVEN
        Designer designer = new Designer();
        designer.setProfession("graphiste");

        User user = new User();
        user.setUserId("user123");
        user.setEmail("test@mail.com");
        user.setFirstname("Jane");
        user.setLastname("Doe");
        mongoTemplate.save(user);

        // Sérialisation de l'objet designer en JSON
        String designerJson = """
        {
            "profession": "graphiste"
        }
    """;

        // WHEN & THEN
        mockMvc.perform(post("/designers/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(designerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("Jane"));

        List<Designer> savedDesigners = mongoTemplate.findAll(Designer.class);
        assertEquals("Jane", savedDesigners.getFirst().getFirstname());
        assertEquals("graphiste", savedDesigners.getFirst().getProfession());
        String newDesignerId = savedDesigners.getFirst().getId();

        List<User> savedUsers = mongoTemplate.findAll(User.class);
        assertEquals(newDesignerId, savedUsers.getFirst().getDesignerId());
    }

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    void updateDesignerFields_shouldUpdateDesigner() throws Exception {
        // GIVEN
        Designer existingDesigner = new Designer();
        existingDesigner.setId("designer123");
        existingDesigner.setFirstname("OldFirstName");
        mongoTemplate.save(existingDesigner);

        User user = new User();
        user.setUserId("user123");
        user.setDesignerId("designer123");
        mongoTemplate.save(user);

        String updatedDesignerJson = """
        {
            "firstname": "NewFirstName",
            "lastname": "NewLastName",
            "specialties": ["Specialty1", "Specialty2"]
        }
    """;

        // WHEN & THEN
        mockMvc.perform(put("/designers/designer123/update-fields")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDesignerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("NewFirstName"))
                .andExpect(jsonPath("$.lastname").value("NewLastName"))
                .andExpect(jsonPath("$.specialties.length()").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateDesignerFields_shouldReturnNotFound() throws Exception {
        String updatedDesignerJson = """
        {
            "firstname": "NewFirstName"
        }
    """;

        mockMvc.perform(put("/designers/nonexistent-designer/update-fields")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDesignerJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error updating designer: 404 NOT_FOUND \"Designer not found\""));
    }

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    void updateDesignerFields_shouldReturnForbidden() throws Exception {
        Designer existingDesigner = new Designer();
        existingDesigner.setId("designer123");
        mongoTemplate.save(existingDesigner);

        String updatedDesignerJson = """
        {
            "firstname": "NewFirstName"
        }
    """;

        mockMvc.perform(put("/designers/designer123/update-fields")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDesignerJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    void deleteMajorWork_shouldRemoveWork() throws Exception {
        // GIVEN
        Designer designer = new Designer();
        designer.setId("designer123");
        designer.setMajorWorks(List.of("http://example.com/work1.jpg", "http://example.com/work2.jpg"));
        mongoTemplate.save(designer);

        User user = new User();
        user.setUserId("user123");
        user.setDesignerId("designer123");
        mongoTemplate.save(user);

        // WHEN & THEN
        mockMvc.perform(delete("/designers/designer123/delete-major-work")
                        .param("url", "http://example.com/work1.jpg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.majorWorks.length()").value(1))
                .andExpect(jsonPath("$.majorWorks[0]").value("http://example.com/work2.jpg"));
    }


}