package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.payload.ResetPasswordRequest;
import com.owod.plateforme_api.services.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;


/**
 * REST controller for handling password reset requests.
 */
@RestController
@RequestMapping("/password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Initiates a password reset workflow by sending a reset token to the provided email address.
     *
     * @param email the email address of the user requesting a password reset
     * @return HTTP 200 OK if the request is accepted
     */
    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@Valid @RequestBody String email) {
        passwordResetService.initiatePasswordReset(email);
        return ResponseEntity.ok().build();
    }

    /**
     * Resets the user's password using a valid reset token and the new password provided.
     *
     * @param request Payload containing the reset token and the new password
     * @return HTTP 200 OK if the password was reset successfully
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
