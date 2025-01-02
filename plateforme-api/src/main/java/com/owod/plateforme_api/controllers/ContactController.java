package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.payload.ContactRequest;
import com.owod.plateforme_api.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<?> sendContactEmail(@RequestBody ContactRequest request) {
        emailService.sendContactEmail(request);
        return ResponseEntity.ok().build();
    }
}
