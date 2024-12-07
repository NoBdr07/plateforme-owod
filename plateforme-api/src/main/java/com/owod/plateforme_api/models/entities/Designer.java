package com.owod.plateforme_api.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "designers")
public class Designer {

    @Id
    private String id;

    private String email; // Adresse email
    private String profilePicture; // URL de la photo de profil
    private String firstName; // Prénom
    private String lastName; // Nom
    private String biography; // Biographie
    private String phoneNumber; // Numéro de téléphone
    private String profession; // Profession
    private List<String> specialties; // Liste des spécialités
    private List<String> spheresOfInfluence; // Liste des sphères d'influence
    private List<String> favoriteSectors; // Liste des secteurs favoris
    private String countryOfOrigin; // Pays d'origine
    private String countryOfResidence; // Pays de résidence
    private String professionalLevel; // Niveau professionnel (par exemple : junior, senior, expert)
    private List<String> majorWorks; // URL des photos des réalisations majeures (<= 5)
    private String portfolioUrl; // URL du portfolio

    // Getters et Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public List<String> getSpheresOfInfluence() {
        return spheresOfInfluence;
    }

    public void setSpheresOfInfluence(List<String> spheresOfInfluence) {
        this.spheresOfInfluence = spheresOfInfluence;
    }

    public List<String> getFavoriteSectors() {
        return favoriteSectors;
    }

    public void setFavoriteSectors(List<String> favoriteSectors) {
        this.favoriteSectors = favoriteSectors;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public String getProfessionalLevel() {
        return professionalLevel;
    }

    public void setProfessionalLevel(String professionalLevel) {
        this.professionalLevel = professionalLevel;
    }

    public List<String> getMajorWorks() {
        return majorWorks;
    }

    public void setMajorWorks(List<String> majorWorks) {
        this.majorWorks = majorWorks;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }
}
