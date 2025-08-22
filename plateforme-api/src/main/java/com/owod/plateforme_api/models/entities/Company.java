package com.owod.plateforme_api.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "companies")
public class Company {

    @Id
    private String id;

    private String description;
    private String email;
    private String raisonSociale;
    private String siretNumber;
    private String phoneNumber;
    private List<String> sectors;
    private String stage;
    private String type;
    private String revenue;
    private String country;
    private String city;
    private String logoUrl;
    private String websiteUrl;
    private String teamPhotoUrl;
    private List<String> worksUrl;
    private List<String> employeesId;
    private Boolean financialSupport;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getSectors() {
        return sectors;
    }

    public void setSectors(List<String> sectors) {
        this.sectors = sectors;
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

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
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

    public String getTeamPhotoUrl() {
        return teamPhotoUrl;
    }

    public void setTeamPhotoUrl(String teamPhotoUrl) {
        this.teamPhotoUrl = teamPhotoUrl;
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

    public String getSiretNumber() {
        return siretNumber;
    }

    public void setSiretNumber(String siretNumber) {
        this.siretNumber = siretNumber;
    }

    public Boolean getFinancialSupport() {
        return financialSupport;
    }

    public void setFinancialSupport(Boolean financialSupport) {
        this.financialSupport = financialSupport;
    }
}
