package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.DesignerEvent;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.UserRepository;
import com.owod.plateforme_api.services.DesignerService;
import com.owod.plateforme_api.services.ImageStorageService;
import com.owod.plateforme_api.services.TransferService;
import com.owod.plateforme_api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Designer entities and related operations.
 */
@RestController
@RequestMapping("/designers")
public class DesignerController {

    @Autowired
    private DesignerService designerService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransferService transferService;

    @Autowired(required = false)
    private ImageStorageService imageStorageService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all designers.
     *
     * @return a list of all Designer entities
     */
    @GetMapping("/all")
    public List<Designer> getAllDesigners() {
        return designerService.getAll();
    }

    /**
     * Retrieves a specific designer by the associated user ID.
     *
     * @param userId the ID of the user whose designer record is requested
     * @return ResponseEntity containing the Designer or an error message
     */
    @GetMapping("/designer/{userId}")
    public ResponseEntity<?> getDesignerByUserId(@PathVariable String userId) {
        Optional<User> optionalUser = userService.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = optionalUser.get();
        if (user.getDesignerId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No designer account associated with this user.");
        }
        Optional<Designer> optionalDesigner = designerService.findById(user.getDesignerId());
        if (optionalDesigner.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Designer not found.");
        }
        return ResponseEntity.ok(optionalDesigner.get());
    }

    /**
     * Deletes the designer associated with the given user, if the authenticated user matches.
     *
     * @param userId      the ID of the user requesting deletion
     * @param designerId  the ID of the designer to delete
     * @param principal   security principal of the authenticated user
     * @return ResponseEntity with status and message
     */
    @DeleteMapping("/delete/{userId}/{designerId}")
    public ResponseEntity<?> deleteDesigner(@PathVariable String userId,
                                            @PathVariable String designerId,
                                            Principal principal) {
        try {
            String currentUserId = principal.getName();
            if (!userId.equals(currentUserId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Request user id is different than current authenticated user");
            }
            Optional<User> optionalUser = userService.findByUserId(userId);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            User user = optionalUser.get();
            if (!user.getDesignerId().equals(designerId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Designer not paired to current user");
            }
            userService.deleteDesigner(userId, designerId);
            return ResponseEntity.ok("Designer deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting designer: " + e.getMessage());
        }
    }

    /**
     * Admin endpoint to delete any designer by ID.
     * Requires ADMIN role.
     *
     * @param designerId the ID of the designer to delete
     * @return ResponseEntity with status and message
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete/{designerId}")
    public ResponseEntity<?> deleteDesigner(@PathVariable String designerId) {
        try {
            designerService.delete(designerId);
            return ResponseEntity.ok("Designer deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting designer: " + e.getMessage());
        }
    }

    /**
     * Admin endpoint to transfer a designer to a user.
     * Requires ADMIN role.
     *
     * @param userId     the ID of the user to transfer the designer to
     * @param designerId the ID of the designer to transfer
     * @return ResponseEntity with status and message
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/transfer/{userId}/{designerId}")
    public ResponseEntity<?> transferDesigner(@PathVariable String userId,
                                              @PathVariable String designerId) {
        try {
            transferService.transferDesigner(userId, designerId);
            return ResponseEntity.ok("Designer successfully transferred");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error transferring designer: " + e.getMessage());
        }
    }

    /**
     * Retrieves all designers having a specific specialty.
     *
     * @param specialty the specialty to filter designers by
     * @return ResponseEntity containing list of designers or no content
     */
    @GetMapping("/specialty")
    public ResponseEntity<List<Designer>> getDesignersBySpecialty(@RequestParam String specialty) {
        List<Designer> designers = designerService.findBySpecialty(specialty);
        if (designers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(designers);
    }

    /**
     * Creates a new designer for the authenticated user.
     *
     * @param designer  Designer object from request body
     * @param principal security principal of the authenticated user
     * @return ResponseEntity with created Designer or error message
     */
    @PostMapping("/new")
    public ResponseEntity<?> newDesigner(@RequestBody Designer designer, Principal principal) {
        try {
            String userId = principal.getName();
            User user = userService.findByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User not found : " + userId
                    ));
            designer.setEmail(user.getEmail());
            designer.setFirstname(user.getFirstname());
            designer.setLastname(user.getLastname());
            Designer newDesigner = designerService.save(designer);
            user.setDesignerId(newDesigner.getId());
            userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating designer: " + e.getMessage());
        }
    }

    /**
     * Admin endpoint to create a new designer on behalf of another user.
     * Requires ADMIN role.
     *
     * @param designer  Designer object with createdBy set
     * @param principal security principal of the authenticated admin
     * @return ResponseEntity with created Designer or error message
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/designers")
    public ResponseEntity<?> createDesignerAsAdmin(@RequestBody Designer designer, Principal principal) {
        try {
            String userId = principal.getName();
            User authenticatedUser = userService.findByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Admin not found : " + userId
                    ));
            designer.setCreatedBy(authenticatedUser.getUserId());
            Designer newDesigner = designerService.save(designer);
            return ResponseEntity.status(HttpStatus.CREATED).body(newDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating designer: " + e.getMessage());
        }
    }

    /**
     * Admin endpoint to list designers created by the authenticated admin.
     * Requires ADMIN role.
     *
     * @param principal security principal of the authenticated admin
     * @return ResponseEntity containing list of Designer entities
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/adminCreatedDesigners")
    public ResponseEntity<?> getDesignersCreatedAsAdmin(Principal principal) {
        String userId = principal.getName();
        User authenticatedUser = userService.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Admin not found : " + userId
                ));
        List<Designer> designers = designerService.getDesignersCreatedByAdmin(authenticatedUser.getUserId());
        return ResponseEntity.ok(designers);
    }

    /**
     * Updates designer fields except profile picture and major works.
     * Requires ADMIN role or ownership.
     *
     * @param designerId       ID of the designer to update
     * @param updatedDesigner  Designer object with updated fields
     * @return ResponseEntity with updated Designer or error message
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isDesignerOwner(#designerId, authentication.principal)")
    @PutMapping("/{designerId}/update-fields")
    public ResponseEntity<?> updateDesignerFields(@PathVariable String designerId,
                                                  @RequestBody Designer updatedDesigner) {
        try {
            Designer existing = designerService.findById(designerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Designer not found"));
            existing.setFirstname(updatedDesigner.getFirstname());
            existing.setLastname(updatedDesigner.getLastname());
            existing.setBiography(updatedDesigner.getBiography());
            existing.setPhoneNumber(updatedDesigner.getPhoneNumber());
            existing.setEmail(updatedDesigner.getEmail());
            existing.setProfession(updatedDesigner.getProfession());
            existing.setSpecialties(updatedDesigner.getSpecialties());
            existing.setSpheresOfInfluence(updatedDesigner.getSpheresOfInfluence());
            existing.setFavoriteSectors(updatedDesigner.getFavoriteSectors());
            existing.setCountryOfOrigin(updatedDesigner.getCountryOfOrigin());
            existing.setCountryOfResidence(updatedDesigner.getCountryOfResidence());
            existing.setProfessionalLevel(updatedDesigner.getProfessionalLevel());
            existing.setPortfolioUrl(updatedDesigner.getPortfolioUrl());
            Designer savedDesigner = designerService.save(existing);
            return ResponseEntity.ok(savedDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating designer: " + e.getMessage());
        }
    }

    /**
     * Updates the designer's profile picture.
     * Requires ADMIN role or ownership.
     *
     * @param designerId     ID of the designer to modify
     * @param profilePicture new profile picture file
     * @return ResponseEntity with updated Designer or error message
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isDesignerOwner(#designerId, authentication.principal)")
    @PutMapping("/{designerId}/update-picture")
    public ResponseEntity<?> updateDesignerPicture(@PathVariable String designerId,
                                                   @RequestPart("profilePicture") MultipartFile profilePicture) {
        try {
            Designer existingDesigner = designerService.findById(designerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Designer not found"));
            String uploadedUrl = imageStorageService.uploadImage(profilePicture);
            existingDesigner.setProfilePicture(uploadedUrl);
            Designer savedDesigner = designerService.save(existingDesigner);
            return ResponseEntity.ok(savedDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating profile picture: " + e.getMessage());
        }
    }

    /**
     * Adds or updates major works images for a designer.
     * Requires ADMIN role or ownership.
     *
     * @param designerId   ID of the designer to modify
     * @param realisations list of image files to add
     * @return ResponseEntity with updated Designer or error message
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isDesignerOwner(#designerId, authentication.principal)")
    @PutMapping("/{designerId}/update-major-works")
    public ResponseEntity<?> updateMajorWorks(
            @PathVariable String designerId,
            @RequestPart("realisations") List<MultipartFile> realisations) {
        try {
            Designer existingDesigner = designerService.findById(designerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Designer not found"));
            List<String> uploadedUrls = new ArrayList<>();
            for (MultipartFile file : realisations) {
                String url = imageStorageService.uploadImage(file);
                uploadedUrls.add(url);
            }
            if (uploadedUrls.size() > 3) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("3 majorWorks maximum");
            }
            if (existingDesigner.getMajorWorks() == null) {
                existingDesigner.setMajorWorks(new ArrayList<>());
            }
            existingDesigner.getMajorWorks().addAll(uploadedUrls);
            Designer savedDesigner = designerService.save(existingDesigner);
            return ResponseEntity.ok(savedDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating major works: " + e.getMessage());
        }
    }

    /**
     * Deletes a specific image from the designer's major works.
     * Requires ADMIN role or ownership.
     *
     * @param designerId ID of the designer whose work is to be deleted
     * @param workUrl    URL of the work image to delete
     * @return ResponseEntity with updated Designer or error message
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isDesignerOwner(#designerId, authentication.principal)")
    @DeleteMapping("/{designerId}/delete-major-work")
    public ResponseEntity<?> deleteMajorWork(
            @PathVariable String designerId,
            @RequestParam("url") String workUrl) {
        try {
            Designer existingDesigner = designerService.findById(designerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Designer not found"));
            List<String> currentMajorWorks = existingDesigner.getMajorWorks();
            if (currentMajorWorks == null || !currentMajorWorks.remove(workUrl)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Realisation not found in the designer's major works.");
            }
            existingDesigner.setMajorWorks(currentMajorWorks);
            Designer savedDesigner = designerService.save(existingDesigner);
            return ResponseEntity.ok(savedDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting major work: " + e.getMessage());
        }
    }

    /**
     * Adds an event to the authenticated designer's schedule.
     *
     * @param principal security principal of the authenticated user
     * @param event     DesignerEvent object from request body
     * @return ResponseEntity with updated Designer or 404 if user not found
     */
    @PostMapping("/events/add")
    public ResponseEntity<Designer> addEvent(Principal principal, @RequestBody DesignerEvent event) {
        String currentUserId = principal.getName();
        Optional<User> optUser = userRepository.findByUserId(currentUserId);
        if (optUser.isPresent()) {
            User currentUser = optUser.get();
            String designerId = currentUser.getDesignerId();
            Designer updatedDesigner = designerService.addEvent(designerId, event);
            return ResponseEntity.ok(updatedDesigner);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Modifies an existing event for the authenticated designer.
     *
     * @param principal security principal of the authenticated user
     * @param event     DesignerEvent object from request body
     * @return ResponseEntity with updated Designer or 404 if user not found
     */
    @PostMapping("/events/modify")
    public ResponseEntity<Designer> modifyEvent(Principal principal, @RequestBody DesignerEvent event) {
        String currentUserId = principal.getName();
        Optional<User> optUser = userRepository.findByUserId(currentUserId);
        if (optUser.isPresent()) {
            User currentUser = optUser.get();
            String designerId = currentUser.getDesignerId();
            Designer updatedDesigner = designerService.modifyEvent(designerId, event);
            return ResponseEntity.ok(updatedDesigner);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes an existing event from the authenticated designer's schedule.
     *
     * @param principal security principal of the authenticated user
     * @param event     DesignerEvent object from request body
     * @return ResponseEntity with updated Designer or 404 if user not found
     */
    @PostMapping("/events/delete")
    public ResponseEntity<Designer> deleteEvent(Principal principal, @RequestBody DesignerEvent event) {
        String currentUserId = principal.getName();
        Optional<User> optUser = userRepository.findByUserId(currentUserId);
        if (optUser.isPresent()) {
            User currentUser = optUser.get();
            String designerId = currentUser.getDesignerId();
            Designer updatedDesigner = designerService.deleteEvent(designerId, event);
            return ResponseEntity.ok(updatedDesigner);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
