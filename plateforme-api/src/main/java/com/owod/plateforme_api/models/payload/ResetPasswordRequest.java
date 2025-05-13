package com.owod.plateforme_api.models.payload;

/**
 * Data Transfer Object for password reset operations.
 * Contains the reset token and the new password to set.
 */
public class ResetPasswordRequest {
    private String token;
    private String newPassword;

    // Getters et Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}