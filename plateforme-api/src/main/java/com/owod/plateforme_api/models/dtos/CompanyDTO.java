package com.owod.plateforme_api.models.dtos;

import org.springframework.data.annotation.Id;

import java.util.List;

public class CompanyDTO {

    private String id;
    private String name;
    private String description;
    private String email;
    private String raisonSociale;
    private String siretNumber;
    private String phoneNumber;
    private String sector;
    private String stage;
    private String type;
    private String country;
    private String city;
    private String logoUrl;
    private String websiteUrl;
    private List<String> photosUrl;
    private List<String> worksUrl;
    private List<String> employeesId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getSiretNumber() {
        return siretNumber;
    }

    public void setSiretNumber(String siretNumber) {
        this.siretNumber = siretNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public List<String> getPhotosUrl() {
        return photosUrl;
    }

    public void setPhotosUrl(List<String> photosUrl) {
        this.photosUrl = photosUrl;
    }

    public List<String> getWorksUrl() {
        return worksUrl;
    }

    public void setWorksUrl(List<String> worksUrl) {
        this.worksUrl = worksUrl;
    }

    public List<String> getEmployeesId() {
        return employeesId;
    }

    public void setEmployeesId(List<String> employeesId) {
        this.employeesId = employeesId;
    }
}
