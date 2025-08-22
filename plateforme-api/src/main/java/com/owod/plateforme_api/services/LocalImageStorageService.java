package com.owod.plateforme_api.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

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

        // Chemin vers resources/static/uploads (portable)
        Path uploadPath = Paths.get(System.getProperty("user.home"), "owod-uploads");
        Files.createDirectories(uploadPath);

        // Nom de fichier sûr + unique
        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
        String clean = original.replaceAll("[^A-Za-z0-9._-]", "_");
        String filename = UUID.randomUUID() + "_" + clean;

        // Écriture réelle
        Path target = uploadPath.resolve(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        // Comme on écrit sous resources/static, Spring sert déjà /uploads/**
        // L’URL publique est donc:
        return ServletUriComponentsBuilder
                .fromCurrentContextPath() // -> http://localhost:8080
                .path("/uploads/")
                .path(filename)
                .toUriString();
    }
}
