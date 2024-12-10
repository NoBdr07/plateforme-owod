package com.owod.plateforme_api.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {
    String uploadImage(MultipartFile file) throws IOException; // Retourne l'URL ou le chemin local
}
