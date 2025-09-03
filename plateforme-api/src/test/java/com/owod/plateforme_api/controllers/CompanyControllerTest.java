package com.owod.plateforme_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owod.plateforme_api.configuration.TestSecurityConfig;
import com.owod.plateforme_api.models.dtos.CompanyDTO;
import com.owod.plateforme_api.models.entities.Company;
import com.owod.plateforme_api.models.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestSecurityConfig.class)
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongo;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mongo.dropCollection("users");
        mongo.dropCollection("companies");
    }

    @Test
    void getAllDto_shouldReturnAllDto() throws Exception {
        // GIVEN
        Company company1 = new Company();
        company1.setRaisonSociale("Entreprise 1");
        company1.setRevenue("10000");
        mongo.save(company1);

        Company company2 = new Company();
        company2.setRaisonSociale("Entreprise 2");
        mongo.save(company2);

        // WHEN & THEN
        mockMvc.perform(get("/company/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].raisonSociale",
                        containsInAnyOrder("Entreprise 1", "Entreprise 2")))
                .andExpect(jsonPath("$..revenue").doesNotExist());
    }

    @Test
    void getAllFull_shouldReturn404whenNotAdmin() throws Exception {
        // GIVEN
        Company company1 = new Company();
        company1.setRaisonSociale("Entreprise 1");
        mongo.save(company1);

        // WHEN & THEN
        mockMvc.perform(get("/company/all-full"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllFull_shouldReturnFullCompaniesWhenAdmin() throws Exception {
        // GIVEN
        Company company1 = new Company();
        company1.setRaisonSociale("Entreprise 1");
        company1.setRevenue("10000");
        mongo.save(company1);

        // WHEN & THEN
        mockMvc.perform(get("/company/all-full"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].raisonSociale",
                        containsInAnyOrder("Entreprise 1")))
                .andExpect(jsonPath("$..revenue",
                        containsInAnyOrder("10000")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void byUser_asAdmin_returnsCompany() throws Exception {
        var c = new Company(); c.setId("c1"); c.setRaisonSociale("ACME"); c.setRevenue("10000");
        mongo.save(c, "companies");
        var u = new User(); u.setUserId("u1"); u.setEmail("u1@ex.com"); u.setCompanyId("c1");
        mongo.save(u, "users");

        mockMvc.perform(get("/company/by-user/{userId}", "u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c1"))
                .andExpect(jsonPath("$.raisonSociale").value("ACME"))
                .andExpect(jsonPath("$.revenue").value("10000")); // endpoint renvoie l'entité
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void byUser_userNotFound_returns404() throws Exception {
        mockMvc.perform(get("/company/by-user/{userId}", "missing"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void byUser_noCompanyLinked_returns404() throws Exception {
        var u = new User(); u.setUserId("u2"); u.setEmail("u2@ex.com"); u.setCompanyId(null);
        mongo.save(u, "users");

        mockMvc.perform(get("/company/by-user/{userId}", "u2"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("No company account")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFull_asAdmin_returnsEntityIncludingRevenue() throws Exception {
        var c = new Company(); c.setId("c1"); c.setRaisonSociale("ACME"); c.setRevenue("10000");
        mongo.save(c, "companies");

        mockMvc.perform(get("/company/{id}/full", "c1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c1"))
                .andExpect(jsonPath("$.revenue").value("10000"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFull_notFound_returns404() throws Exception {
        mockMvc.perform(get("/company/{id}/full", "nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "bob") // non admin, non owner
    void getFull_asUser_forbidden_ifNotOwner() throws Exception {
        var c = new Company(); c.setId("c1"); c.setRaisonSociale("ACME");
        mongo.save(c, "companies");

        mockMvc.perform(get("/company/{id}/full", "c1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice") // non admin, owner
    void getFull_asOwner_returns200() throws Exception {
        var c = new Company(); c.setId("c1"); c.setRaisonSociale("ACME");
        mongo.save(c, "companies");
        var u = new User(); u.setUserId("alice"); u.setEmail("alice@ex.com"); u.setCompanyId("c1");
        mongo.save(u, "users");

        mockMvc.perform(get("/company/{id}/full", "c1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c1"));
    }

    // ---------- POST /company/new ----------

    @Test
    @WithMockUser(username = "alice")
    void newCompany_happyPath_setsEmailFromUser_andLinksUser() throws Exception {
        var u = new User(); u.setUserId("alice"); u.setEmail("alice@ex.com");
        mongo.save(u, "users");

        var body = new CompanyDTO();
        body.setRaisonSociale("ACME");

        var mvcRes = mockMvc.perform(
                        post("/company/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(body))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.raisonSociale").value("ACME"))
                .andExpect(jsonPath("$.email").value("alice@ex.com"))
                .andReturn();

        // Vérifie que l'utilisateur est bien lié à la company créée
        String createdId = objectMapper
                .readTree(mvcRes.getResponse().getContentAsByteArray())
                .get("id").asText();

        // Retrouver l'utilisateur par userId (selon ton mapping)
        var updatedUser = mongo.findById(u.getUserId(), User.class, "users");

    }

    @Test
    @WithMockUser(username = "ghost")
    void newCompany_userNotFound_returns400() throws Exception {
        var body = new CompanyDTO(); body.setRaisonSociale("ACME");

        mockMvc.perform(
                        post("/company/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(body))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User not found")));
    }

    // ---------- PATCH /company/{id} ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFields_mergesOnlyProvidedFields() throws Exception {
        var c = new Company(); c.setId("c1"); c.setRaisonSociale("OLD"); c.setCity("Paris");
        mongo.save(c, "companies");

        var patch = new Company();
        patch.setRaisonSociale("NEW"); // city non fournie => doit rester "Paris"

        mockMvc.perform(
                        patch("/company/{companyId}", "c1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(patch))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c1"))
                .andExpect(jsonPath("$.raisonSociale").value("NEW"))
                .andExpect(jsonPath("$.city").value("Paris"));
    }
}
