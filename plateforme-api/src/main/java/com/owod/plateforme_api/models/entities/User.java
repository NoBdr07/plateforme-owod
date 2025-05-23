package com.owod.plateforme_api.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entity representing a User stored in the "users" MongoDB collection.
 * Includes identification, authentication credentials, roles, password reset details, and friend relationships.
 */
@Document(collection = "users")
public class User {

    @Id
    private String userId;

    @Indexed(unique = true)
    private String email;

    private String firstname;
    private String lastname;
    private String password;

    private Set<Role> roles;
    private String designerId;

    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    private Set<String> friendsId;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getDesignerId() {
        return designerId;
    }

    public void setDesignerId(String designerId) {
        this.designerId = designerId;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public Set<String> getFriendsId() {
        return friendsId;
    }

    public void setFriendsId(Set<String> friendsId) {
        this.friendsId = friendsId;
    }
}
