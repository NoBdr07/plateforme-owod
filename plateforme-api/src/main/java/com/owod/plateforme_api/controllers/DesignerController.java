package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.services.DesignerService;
import com.owod.plateforme_api.services.ImageStorageService;
import com.owod.plateforme_api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Endpoint to retrieve all designers
     * @return a list of all designers
     */
    @GetMapping("/all")
    public List<Designer> getAllDesigners() {
        return designerService.getAll();
    }

    /**
     * Endpoint to get a specific designer
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
     * Endpoint to retrieve all designers that have a specific specialty
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
     * Endpoint to create a new designer
     * @param designer
     * @return 201 and the designer if success, 400 with error message if failure
     */
    @PostMapping("/new")
    public ResponseEntity<?> newDesigner(@RequestBody Designer designer, Principal principal) {
        try {
            // 1. Récupérer l'utilisateur en cours de session à partir du principal
            String userId = principal.getName(); // Récupère l'email de l'utilisateur authentifié
            Optional<User> optionalUser = userService.findByUserId(userId);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }

            User user = optionalUser.get();

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
     * Endpoint to update info about designer (all fields except profilePicture and majorWorks)
     * @param designerId
     * @param updatedDesigner
     * @param principal
     * @return
     */
    @PutMapping("/{designerId}/update-fields")
    public ResponseEntity<?> updateDesignerFields(@PathVariable String designerId, @RequestBody Designer updatedDesigner, Principal principal) {
        try {
            // 1. Récupérer le designer existant
            Optional<Designer> optionalDesigner = designerService.findById(designerId);

            if (optionalDesigner.isEmpty()) {
                return ResponseEntity.status(404).body("Designer not found");
            }

            // 2. Vérifier que l'utilisateur est bien propriétaire du designer
            if (!this.userService.isDesignerOwner(designerId, principal)) {
                return ResponseEntity.status(403).body("You are not authorized to update this designer.");
            }

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
     * @param designerId
     * @param profilePicture
     * @param principal
     * @return
     */
    @PutMapping("/{designerId}/update-picture")
    public ResponseEntity<?> updateDesignerPicture(@PathVariable String designerId, @RequestPart("profilePicture") MultipartFile profilePicture, Principal principal) {
        try {
            // Vérifier que l'utilisateur est bien autorisé
            if (!this.userService.isDesignerOwner(designerId, principal)) {
                return ResponseEntity.status(403).body("You are not authorized to update this designer.");
            }

            // Charger le designer existant
            Optional<Designer> optionalDesigner = designerService.findById(designerId);
            if (optionalDesigner.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Designer not found");
            }

            // Upload de la photo de profil
            String uploadedUrl = imageStorageService.uploadImage(profilePicture);
            Designer designer = optionalDesigner.get();
            designer.setProfilePicture(uploadedUrl);

            // Sauvegarder
            Designer savedDesigner = designerService.save(designer);
            return ResponseEntity.ok(savedDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating profile picture: " + e.getMessage());
        }
    }

    /**
     * Endpoint to add one or several picture of major works
     * @param designerId
     * @param realisations
     * @param principal
     * @return
     */
    @PutMapping("/{designerId}/update-major-works")
    public ResponseEntity<?> updateMajorWorks(
            @PathVariable String designerId,
            @RequestPart("realisations") List<MultipartFile> realisations,
            Principal principal
    ) {
        try {
            // Vérifier que l'utilisateur est bien autorisé
            if (!this.userService.isDesignerOwner(designerId, principal)) {
                return ResponseEntity.status(403).body("You are not authorized to update this designer.");
            }

            // Charger le designer existant
            Optional<Designer> optionalDesigner = designerService.findById(designerId);
            if (optionalDesigner.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Designer not found");
            }

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
            Designer designer = optionalDesigner.get();
            List<String> existingWorks = designer.getMajorWorks();

            // Initialiser la liste majorWorks si elle est nulle
            if (designer.getMajorWorks() == null) {
                designer.setMajorWorks(new ArrayList<>());
            }
            existingWorks.addAll(uploadedUrls);
            designer.setMajorWorks(existingWorks);

            // Sauvegarder
            Designer savedDesigner = designerService.save(designer);
            return ResponseEntity.ok(savedDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating major works: " + e.getMessage());
        }
    }

    /**
     * Endpoint to delete one of the major works picture
     * @param designerId
     * @param workUrl
     * @param principal
     * @return
     */
    @DeleteMapping("/{designerId}/delete-major-work")
    public ResponseEntity<?> deleteMajorWork(
            @PathVariable String designerId,
            @RequestParam("url") String workUrl,
            Principal principal
    ) {
        try {
            // Vérifier que l'utilisateur est bien autorisé
            if (!this.userService.isDesignerOwner(designerId, principal)) {
                return ResponseEntity.status(403).body("You are not authorized to update this designer.");
            }

            // Charger le designer existant
            Optional<Designer> optionalDesigner = designerService.findById(designerId);
            if (optionalDesigner.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Designer not found");
            }

            Designer designer = optionalDesigner.get();

            // Supprimer la réalisation de la liste
            List<String> currentMajorWorks = designer.getMajorWorks();
            if (currentMajorWorks == null || !currentMajorWorks.remove(workUrl)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Realisation not found in the designer's major works.");
            }

            // Sauvegarder les modifications
            designer.setMajorWorks(currentMajorWorks);
            Designer savedDesigner = designerService.save(designer);

            return ResponseEntity.ok(savedDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting major work: " + e.getMessage());
        }
    }





}
