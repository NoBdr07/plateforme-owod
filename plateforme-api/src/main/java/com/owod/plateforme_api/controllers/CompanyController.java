package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.dtos.CompanyDTO;
import com.owod.plateforme_api.models.entities.Company;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.services.CompanyService;
import com.owod.plateforme_api.services.ImageStorageService;
import com.owod.plateforme_api.services.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * CompanyController is responsible for handling HTTP requests related to company entities.
 * It interacts with the CompanyService to retrieve data and respond to client requests.
 */
@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired(required = false)
    private ImageStorageService imageStorageService;
    @Autowired
    private UserService userService;

    /**
     * Retrieves all company data as a list of CompanyDTO objects.
     *
     * @return a ResponseEntity containing a list of CompanyDTO objects if companies are found,
     *         or a 404 Not Found response if the list is empty.
     */
    @GetMapping("/all")
    public ResponseEntity<List<CompanyDTO>> getAllDto() {
        List<CompanyDTO> companies = companyService.getAllDto();

        if (companies.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(companies);
    }

    /**
     * Creates a new company and associates it with the authenticated user.
     * The authenticated user's email is set as the company's email, and the user's information is updated
     * to associate them with the created company.
     *
     * @param companyDto the data for the new company, provided as a CompanyDTO object
     * @param principal the authenticated user's principal information
     * @return a ResponseEntity containing the newly created CompanyDTO object with a status of CREATED (201),
     *         or an error message with a status of BAD_REQUEST (400) if creation fails
     */
    @PostMapping("/new")
    public ResponseEntity<?> newCompany(@RequestBody CompanyDTO companyDto, Principal principal) {
        try {
            String userId = principal.getName();
            User user = userService.findByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User not found : " + userId
                    ));

            companyDto.setEmail(user.getEmail());
            CompanyDTO newCompany = companyService.saveDto(companyDto);

            user.setCompanyId(newCompany.getId());
            userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating company: " + e.getMessage());
        }
    }
}
