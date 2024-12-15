package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.DesignerRepository;
import com.owod.plateforme_api.services.DesignerService;
import com.owod.plateforme_api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/designers")
public class DesignerController {

    @Autowired
    private DesignerService designerService;

    @Autowired
    private UserService userService;

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
     *
     * @param designerId
     * @param updatedDesigner
     * @param principal
     * @return
     */
    @PutMapping("/{designerId}")
    public ResponseEntity<?> updateDesigner(@PathVariable String designerId, @RequestBody Designer updatedDesigner, Principal principal) {
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



}
