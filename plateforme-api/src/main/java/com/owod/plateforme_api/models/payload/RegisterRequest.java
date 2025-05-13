package com.owod.plateforme_api.models.payload;

/**
 * Data Transfer Object for user registration requests.
 * Contains user's email, password, first name, and last name.
 */
public class RegisterRequest {

    String email;
    String password;
    String firstname;
    String lastname;

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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}
