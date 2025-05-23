package com.owod.plateforme_api.models.dtos;

/**
 * Data Transfer Object for representing user information in API responses.
 * Contains userId, firstname, and lastname.
 */
public class UserDto {

    private String userId;
    private String firstname;
    private String lastname;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
