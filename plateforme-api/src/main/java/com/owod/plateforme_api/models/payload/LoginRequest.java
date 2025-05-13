package com.owod.plateforme_api.models.payload;

/**
 * Data Transfer Object for user login requests.
 * Contains the user's email and password credentials.
 */
public class LoginRequest {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
