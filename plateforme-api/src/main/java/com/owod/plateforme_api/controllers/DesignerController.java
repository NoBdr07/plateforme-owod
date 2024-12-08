package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.repositories.DesignerRepository;
import com.owod.plateforme_api.services.DesignerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/designers")
public class DesignerController {

    @Autowired
    private DesignerService designerService;

    /**
     * Endpoint to retrieve all designers
     * @return a list of all designers
     */
    @GetMapping("/all")
    public List<Designer> getAllDesigners() {
        return designerService.getAll();
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
    public ResponseEntity<?> newDesigner(@RequestBody Designer designer) {
        try {
            // Sauvegarde le designer en utilisant le service
            Designer newDesigner = designerService.save(designer);

            // Retourne un statut 201 Created avec le designer sauvegardé
            return ResponseEntity.status(201).body(newDesigner);
        } catch (Exception e) {
            // Retourne un statut 400 Bad Request en cas d'erreur
            return ResponseEntity.status(400).body("Error creating designer: " + e.getMessage());
        }
    }
}
