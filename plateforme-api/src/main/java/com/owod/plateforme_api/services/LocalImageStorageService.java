package com.owod.plateforme_api.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Service implementation that stores images locally on the filesystem.
 * <p>
 * Used typically in development environments to save uploaded files under
 * a static resources directory and serve them via a local URL.
 */
@Service
@Profile("dev")
public class LocalImageStorageService implements ImageStorageService {

    /**
     * Uploads the given multipart file to a local directory. Creates the upload
     * directory if it does not exist, saves the file, and returns a development URL.
     *
     * @param file the multipart file to upload
     * @return the local URL to access the uploaded file
     * @throws IOException if an error occurs during file transfer
     */
    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        // Directory for storing uploads
        String uploadDir = "D:/plateforme-owod/plateforme-api/src/main/resources/static/uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the file locally
        String filePath = uploadDir + file.getOriginalFilename();
        File localFile = new File(filePath);
        file.transferTo(localFile);

        String relativePath = "uploads/" + file.getOriginalFilename();

        // Return the local development URL
        return "http://localhost:8080/" + relativePath;
    }
}
