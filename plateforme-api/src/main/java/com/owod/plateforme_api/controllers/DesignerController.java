package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.DesignerEvent;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.UserRepository;
import com.owod.plateforme_api.services.DesignerService;
import com.owod.plateforme_api.services.ImageStorageService;
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

@RestController
@RequestMapping("/designers")
public class DesignerController {

    @Autowired
    private DesignerService designerService;

    @Autowired
    private UserService userService;

    @Autowired(required = false)
    private ImageStorageService imageStorageService;
    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint to retrieve all designers
     *
     * @return a list of all designers
     */
    @GetMapping("/all")
    public List<Designer> getAllDesigners() {
        return designerService.getAll();
    }

    /**
     * Endpoint to get a specific designer
     *
     * @param userId
     * @return
     */
    @GetMapping("/designer/{userId}")
    public ResponseEntity<?> getDesignerByUserId(@PathVariable String userId) {
        Optional<User> optionalUser = userService.findByUserId(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = optionalUser.get();

        if (user.getDesignerId() == null) {
            return ResponseEntity.status(404).body("No designer account associated with this user.");
        }

        Optional<Designer> optionalDesigner = designerService.findById(user.getDesignerId());

        if (optionalDesigner.isEmpty()) {
            return ResponseEntity.status(404).body("Designer not found.");
        }

        return ResponseEntity.ok(optionalDesigner.get());
    }

    /**
     * @param userId
     * @param designerId
     * @param principal
     * @return
     */
    @DeleteMapping("/delete/{userId}/{designerId}")
    public ResponseEntity<?> deleteDesigner(@PathVariable String userId, @PathVariable String designerId, Principal principal) {
        try {
            // Récupérer l'utilisateur en cours de session à partir du principal
            String currentUserId = principal.getName(); // Récupère l'email de l'utilisateur authentifié

            if (!userId.equals(currentUserId)) {
                return ResponseEntity.status(401).body("Request user id is different than current authenticated user");
            }

            Optional<User> optionalUser = userService.findByUserId(userId);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }

            User user = optionalUser.get();

            if (!user.getDesignerId().equals(designerId)) {
                return ResponseEntity.status(401).body("Designer not paired to current user");
            }

            userService.deleteDesigner(userId, designerId);

            return ResponseEntity.status(200).body("Designer deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error deleting designer: " + e.getMessage());
        }
    }


    /**
     * Endpoint to retrieve all designers that have a specific specialty
     *
     * @param specialty
     * @return a List of designers
     */
    @GetMapping("/specialty")
    public ResponseEntity<List<Designer>> getDesignersBySpecialty(@RequestParam String specialty) {
        List<Designer> designers = designerService.findBySpecialty(specialty);
        if (designers.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(designers);
        }
    }

    /**
     * Endpoint to create a new designer when user create designer for himself
     *
     * @param designer
     * @return 201 and the designer if success, 400 with error message if failure
     */
    @PostMapping("/new")
    public ResponseEntity<?> newDesigner(@RequestBody Designer designer, Principal principal) {
        try {
            // 1. Récupérer l'utilisateur en cours de session à partir du principal
            String userId = principal.getName(); // Récupère l'email de l'utilisateur authentifié
            User user = userService.findByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User not found : " + userId
                    ));

            designer.setEmail(user.getEmail());
            designer.setFirstname(user.getFirstname());
            designer.setLastname(user.getLastname());

            // 2. Sauvegarder le designer
            Designer newDesigner = designerService.save(designer);

            // 3. Mettre à jour le champ designerId de l'utilisateur
            user.setDesignerId(newDesigner.getId());
            userService.save(user); // Sauvegarder les modifications de l'utilisateur

            // 4. Retourner une réponse
            return ResponseEntity.status(201).body(newDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error creating designer: " + e.getMessage());
        }
    }

    /**
     * Endpoint to create a new designer when admin create designer that is not him
     *
     * @param designer that has to have a createdBy attribute not null
     * @return 201 and the designer if success, 400 with error message if failure
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/designers")
    public ResponseEntity<?> createDesignerAsAdmin(@RequestBody Designer designer, Principal principal) {
        try {
            // Récupérer l'utilisateur en cours de session à partir du principal
            String userId = principal.getName(); // Récupère l'email de l'utilisateur authentifié
            User authenticatedUser = userService.findByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Admin non trouvé : " + userId
                    ));

            designer.setCreatedBy(authenticatedUser.getUserId());

            // Sauvegarder le designer
            Designer newDesigner = designerService.save(designer);

            // Retourner une réponse
            return ResponseEntity.status(201).body(newDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error creating designer: " + e.getMessage());
        }
    }

    /**
     * Endpoint for admin to get designers they created for other people than themselves
     *
     * @param principal
     * @return list of designers, empty if no designer created by this admin
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/adminCreatedDesigners")
    public ResponseEntity<?> getDesignersCreatedAsAdmin(Principal principal) {
        // Récupérer l'utilisateur en cours de session à partir du principal
        String userId = principal.getName(); // Récupère l'email de l'utilisateur authentifié

        User authenticatedUser = userService.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Admin not found : " + userId
                ));

        List<Designer> designers = designerService.getDesignersCreatedByAdmin(authenticatedUser.getUserId());

        return ResponseEntity.status(200).body(designers);
        
    }

    /**
     * Endpoint to update info about designer (all fields except profilePicture and majorWorks)
     *
     * @param designerId      designer id of designer to modify
     * @param updatedDesigner object designer with modified info
     * @param principal       include user currently connected
     * @return 200 when modification successful, 400 when modification didn't happen
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isDesignerOwner(#designerId, principal)")
    @PutMapping("/{designerId}/update-fields")
    public ResponseEntity<?> updateDesignerFields(@PathVariable String designerId, @RequestBody Designer updatedDesigner, Principal principal) {
        try {
            // 1. Récupérer le designer existant
            Designer existing = designerService.findById(designerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Designer not found"));

            // 3. Mettre à jour directement avec save
            updatedDesigner.setId(designerId); // Assurez-vous que l'id est défini
            Designer savedDesigner = designerService.save(updatedDesigner);

            // 4. Retourner le designer mis à jour
            return ResponseEntity.ok(savedDesigner);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error updating designer: " + e.getMessage());
        }
    }

    /**
     * Endpoint to change the designer profile picture
     *
     * @param designerId     id of designer to modify
     * @param profilePicture new picture to upload
     * @return 404 if designer not found, 400 if error during uploading, 200 if modification ok
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isDesignerOwner(#designerId, principal)")
    @PutMapping("/{designerId}/update-picture")
    public ResponseEntity<?> updateDesignerPicture(@PathVariable String designerId, @RequestPart("profilePicture") MultipartFile profilePicture) {
        try {

            // Charger le designer existant
            Designer existingDesigner = designerService.findById(designerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "designer not found"));

            // Upload de la photo de profil
            String uploadedUrl = imageStorageService.uploadImage(profilePicture);
            existingDesigner.setProfilePicture(uploadedUrl);

            // Sauvegarder
            Designer savedDesigner = designerService.save(existingDesigner);
            return ResponseEntity.ok(savedDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating profile picture: " + e.getMessage());
        }
    }

    /**
     * Endpoint to add one or several picture of major works
     *
     * @param designerId   id of designer to modify
     * @param realisations to add to existing realisation if there is already some
     * @return 404 if designer not found, 400 if error during uploading, 200 if modification ok
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isDesignerOwner(#designerId, principal)")
    @PutMapping("/{designerId}/update-major-works")
    public ResponseEntity<?> updateMajorWorks(
            @PathVariable String designerId,
            @RequestPart("realisations") List<MultipartFile> realisations
    ) {
        try {
            // Charger le designer existant
            Designer existingDesigner = designerService.findById(designerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Designer not found"));

            // Upload des photos
            List<String> uploadedUrls = new ArrayList<>();
            for (MultipartFile file : realisations) {
                String url = imageStorageService.uploadImage(file);
                uploadedUrls.add(url);
            }

            // Vérifier qu'il n'y pas plus de 3 réalisations
            if (uploadedUrls.size() > 3) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("3 majorWorks maximum");
            }

            // Mettre à jour les majorWorks
            List<String> existingWorks = existingDesigner.getMajorWorks();

            // Initialiser la liste majorWorks si elle est nulle
            if (existingDesigner.getMajorWorks() == null) {
                existingDesigner.setMajorWorks(new ArrayList<>());
            }
            existingWorks.addAll(uploadedUrls);
            existingDesigner.setMajorWorks(existingWorks);

            // Sauvegarder
            Designer savedDesigner = designerService.save(existingDesigner);
            return ResponseEntity.ok(savedDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating major works: " + e.getMessage());
        }
    }

    /**
     * Endpoint to delete one of the major works picture
     *
     * @param designerId id of designer concerned
     * @param workUrl    url of realisation to delete
     * @return 404 if designer not found, 400 if error during uploading, 200 if modification ok
     */
    @PreAuthorize("hasRole('ADMIN') or @userService.isDesignerOwner(#designerId, principal)")
    @DeleteMapping("/{designerId}/delete-major-work")
    public ResponseEntity<?> deleteMajorWork(
            @PathVariable String designerId,
            @RequestParam("url") String workUrl
    ) {
        try {
            // Charger le designer existant
            Designer existingDesigner = designerService.findById(designerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Designer not found"));

            // Supprimer la réalisation de la liste
            List<String> currentMajorWorks = existingDesigner.getMajorWorks();
            if (currentMajorWorks == null || !currentMajorWorks.remove(workUrl)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Realisation not found in the designer's major works.");
            }

            // Sauvegarder les modifications
            existingDesigner.setMajorWorks(currentMajorWorks);
            Designer savedDesigner = designerService.save(existingDesigner);

            return ResponseEntity.ok(savedDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting major work: " + e.getMessage());
        }
    }

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
