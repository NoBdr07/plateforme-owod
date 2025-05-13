package com.owod.plateforme_api.models.entities;

import java.time.LocalDate;

/**
 * Entity representing a Designer stored in the "designers" MongoDB collection.
 * Contains personal information, specialties, major works, and related events.
 */
public class DesignerEvent {
    private String id;           // Identifiant unique de l'événement
    private String title;        // Titre de l'événement
    private String description;  // Description détaillée
    private LocalDate startDate;      // Date de début
    private LocalDate endDate;        // Date de fin (pour les événements sur plusieurs jours)
    private String color;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
