package com.owod.plateforme_api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class IonosImageStorageService implements ImageStorageService {

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        // Implémentation pour téléverser sur IONOS Cloud
        return "URL de l'image sur IONOS";
    }
}
