package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.WeeklyDesigner;
import com.owod.plateforme_api.services.DesignerService;
import com.owod.plateforme_api.services.WeeklyDesignerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class WeeklyDesignerController {

    @Autowired
    private WeeklyDesignerService weeklyDesignerService;

    @Autowired
    private DesignerService designerService;

    @GetMapping("/weekly")
    public ResponseEntity<?> getWeeklyDesigner() {
        try {
            WeeklyDesigner weeklyDesigner = weeklyDesignerService.getCurrentWeeklyDesigner();
            if(weeklyDesigner == null) {
                return ResponseEntity.notFound().build();
            }

            Optional<Designer> designer = designerService.findById(weeklyDesigner.getDesignerId());
            return designer.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving weekly designer : " + e.getMessage());
        }
    }
}
