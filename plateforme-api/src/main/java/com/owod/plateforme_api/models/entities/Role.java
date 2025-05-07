package com.owod.plateforme_api.models.entities;

public enum Role {
    USER,
    ADMIN;

    /**
     * Par défaut, Spring Security attend des autorités préfixées "ROLE_".
     * Cette méthode facilite la conversion vers SimpleGrantedAuthority.
     */
    public String authority() {
        return "ROLE_" + name();
    }
}
