package com.owod.plateforme_api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class LocalImageStorageService implements ImageStorageService {

    private final String uploadDir = "uploads/";

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        // Créer le dossier si nécessaire
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Sauvegarder le fichier localement
        String filePath = uploadDir + file.getOriginalFilename();
        File localFile = new File(filePath);
        file.transferTo(localFile);

        // Retourne le chemin relatif ou l'URL de développement
        return "http://localhost:8080/" + filePath;
    }
}
