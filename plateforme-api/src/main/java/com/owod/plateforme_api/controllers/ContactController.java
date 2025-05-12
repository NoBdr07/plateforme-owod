package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.payload.ContactRequest;
import com.owod.plateforme_api.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling contact form submissions.
 * <ul>
 *   <li>Receives contact requests via POST</li>
 *   <li>Delegates email sending to {@link EmailService}</li>
 * </ul>
 */
@RestController
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private EmailService emailService;

    /**
     * Accepts a contact request payload and sends an email.
     *
     * @param request the contact form data, including name, email, subject, and message
     * @return 200 OK if the email was successfully queued or sent
     */
    @PostMapping
    public ResponseEntity<?> sendContactEmail(@RequestBody ContactRequest request) {
        emailService.sendContactEmail(request);
        return ResponseEntity.ok().build();
    }
}
