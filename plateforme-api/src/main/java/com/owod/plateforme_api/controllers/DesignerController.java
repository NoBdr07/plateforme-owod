package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.DesignerRepository;
import com.owod.plateforme_api.services.DesignerService;
import com.owod.plateforme_api.services.ImageStorageService;
import com.owod.plateforme_api.services.LocalImageStorageService;
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

    @Autowired
    private ImageStorageService imageStorageService;

    /**
     * Endpoint to retrieve all designers
     * @return a list of all designers
     */
    @GetMapping("/all")
    public List<Designer> getAllDesigners() {
        return designerService.getAll();
    }

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
     * Endpoint pour la requête de mise à jour du designer (tous les champs sauf profilePicture et majorWorks)
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
            String userId = principal.getName(); // Récupérer l'userId via Principal
            Optional<User> optionalUser = userService.findByUserId(userId);

            if (optionalUser.isEmpty() || !optionalUser.get().getDesignerId().equals(designerId)) {
                return ResponseEntity.status(403).body("You are not authorized to update this designer.");
            }

            // 3. Mettre à jour directement avec `save()`
            updatedDesigner.setId(designerId); // Assurez-vous que l'id est défini
            Designer savedDesigner = designerService.save(updatedDesigner);

            // 4. Retourner le designer mis à jour
            return ResponseEntity.ok(savedDesigner);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error updating designer: " + e.getMessage());
        }
    }

    @PutMapping("/{designerId}/update-picture")
    public ResponseEntity<?> updateDesignerPicture(@PathVariable String designerId, @RequestPart("profilePicture") MultipartFile profilePicture, Principal principal) {
        try {
            // Vérifier que l'utilisateur est bien autorisé
            String userId = principal.getName();
            Optional<User> user = userService.findByUserId(userId);
            if (user.isEmpty() || !user.get().getDesignerId().equals(designerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
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

    @PutMapping("/{designerId}/update-major-works")
    public ResponseEntity<?> updateMajorWorks(
            @PathVariable String designerId,
            @RequestPart("realisations") List<MultipartFile> realisations,
            Principal principal
    ) {
        try {
            // Vérifier que l'utilisateur est bien autorisé
            String userId = principal.getName();
            Optional<User> user = userService.findByUserId(userId);
            if (user.isEmpty() || !user.get().getDesignerId().equals(designerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
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

            // Mettre à jour les majorWorks
            Designer designer = optionalDesigner.get();
            designer.setMajorWorks(uploadedUrls);

            // Sauvegarder
            Designer savedDesigner = designerService.save(designer);
            return ResponseEntity.ok(savedDesigner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating major works: " + e.getMessage());
        }
    }




}
