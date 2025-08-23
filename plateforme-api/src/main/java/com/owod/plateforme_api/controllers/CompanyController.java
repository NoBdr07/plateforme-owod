package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.dtos.CompanyDTO;
import com.owod.plateforme_api.models.entities.Company;
import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.services.CompanyService;
import com.owod.plateforme_api.services.ImageStorageService;
import com.owod.plateforme_api.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * CompanyController is responsible for handling HTTP requests related to company entities.
 * It interacts with the CompanyService to retrieve data and respond to client requests.
 */
@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

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
        return ResponseEntity.ok(companies);

    }

    /**
     * Get one company by its id,
     * public endpoint that retrieve only non confidential data
     *
     * @param id of the company to retrieve
     * @return http 200 if ok.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(companyService.getDtoById(id));
    }

    /**
     * Retrieves a company associated with a given user ID.
     * If the user or associated company is not found, returns an appropriate HTTP status.
     *
     * @param userId the unique identifier of the user whose associated company is to be retrieved
     * @return a ResponseEntity containing the company if found (HTTP 200),
     *         an error message with HTTP 404 if the user or company is not found
     *         or an error message if no company is linked to the user
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isCompanyOwnerByUserId(#userId, authentication.principal)")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> getByUserId(@PathVariable String userId) {
        Optional<User> optionalUser = userService.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = optionalUser.get();
        if (user.getCompanyId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No company account associated with this user.");
        }
        Optional<Company> optionalCompany = companyService.getById(user.getCompanyId());
        if (optionalCompany.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found.");
        }
        return ResponseEntity.ok(optionalCompany.get());
    }

    /**
     * Get one company by its id,
     * private endpoint that retrieve all company info including confidential ones.
     * Only for admin or user linked to this company.
     *
     * @param id of the company to retrieve
     * @return http 200 if ok, 404 if the company isn't found.
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isCompanyOwner(#id, authentication.principal)")
    @GetMapping("/{id}/full")
    public ResponseEntity<Company> getFullById(@PathVariable String id) {
        Company company = companyService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(company);
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

    /**
     * Update any fields of one company, expects files, team and history.
     *
     * @param companyId of company to update.
     * @param patch contains all data.
     * @return the new company updated and saved.
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isCompanyOwner(#companyId, authentication.principal)")
    @PatchMapping("/{companyId}")
    public ResponseEntity<Company> updateFields(@PathVariable String companyId,
                                                   @RequestBody Company patch) {
        Company updated = companyService.updateFields(companyId, patch);
        return ResponseEntity.ok(updated);
    }

    /**
     * Uploads and updates the logo of a company.
     * This operation is restricted to users with the ADMIN role or users who own the specified company.
     *
     * @param companyId the unique identifier of the company whose logo is being updated
     * @param file the MultipartFile containing the logo to be uploaded
     * @return a ResponseEntity containing the updated Company object
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isCompanyOwner(#companyId, authentication.principal)")
    @PostMapping(path = "/{companyId}/logo", consumes = "multipart/form-data")
    public ResponseEntity<Company> uploadLogo(@PathVariable String companyId,
                                                 @RequestPart("file") MultipartFile file) {
        Company updated = companyService.updateLogo(companyId, file);
        return ResponseEntity.ok(updated);
    }

    /**
     * Uploads and updates the team photo of a company.
     * This operation is restricted to users with the ADMIN role or users who own the specified company.
     *
     * @param companyId the unique identifier of the company whose team photo is being updated
     * @param file the MultipartFile containing the team photo to be uploaded
     * @return a ResponseEntity containing the updated Company object
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isCompanyOwner(#companyId, authentication.principal)")
    @PostMapping(path = "/{companyId}/team-photo", consumes = "multipart/form-data")
    public ResponseEntity<Company> uploadTeamPhoto(@PathVariable String companyId,
                                                      @RequestPart("file") MultipartFile file) {
        Company updated = companyService.updateTeamPhoto(companyId, file);
        return ResponseEntity.ok(updated);
    }

    /**
     * Adds new works (files) to the specified company.
     * This operation is restricted to users with the ADMIN role or users who own the specified company.
     *
     * @param companyId the unique identifier of the company to which the works will be added
     * @param files a list of MultipartFile objects representing the works to be uploaded
     * @return a ResponseEntity containing the updated Company object after the works have been added
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isCompanyOwner(#companyId, authentication.principal)")
    @PostMapping(path = "/{companyId}/works", consumes = "multipart/form-data")
    public ResponseEntity<Company> addWorks(@PathVariable String companyId,
                                               @RequestPart("files") List<MultipartFile> files) {
        Company updated = companyService.addWorks(companyId, files);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    /**
     * Deletes a work file associated with a specified company.
     * The operation is restricted to users with the ADMIN role or users who own the specified company.
     *
     * @param companyId the unique identifier of the company whose work file is to be deleted
     * @param url the URL of the work file to be removed
     * @return a ResponseEntity containing the updated Company object after the work file is removed
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isCompanyOwner(#companyId, authentication.principal)")
    @DeleteMapping("/{companyId}/works")
    public ResponseEntity<Company> deleteWork(@PathVariable String companyId,
                                                 @RequestParam("url") String url) {
        Company updated = companyService.deleteWork(companyId, url);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a company by its unique identifier.
     * The method is secured and requires either an admin role or the user
     * to be the owner of the company being deleted.
     *
     * @param companyId the unique identifier of the company to be deleted
     * @return a ResponseEntity containing a message indicating successful deletion
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isCompanyOwner(#companyId, authentication.principal)")
    @DeleteMapping("/{companyId}")
    public ResponseEntity<String> deleteCompany(@PathVariable String companyId, Principal principal) {
        companyService.deleteById(companyId);
        String userId = principal.getName();
        userService.deleteAccount(userId, companyId);
        return ResponseEntity.ok("Company deleted successfully");
    }
}
