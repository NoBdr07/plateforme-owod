package com.owod.plateforme_api.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service interface for storing images. Implementations may upload images to cloud storage or save them locally.
 */
public interface ImageStorageService {

    /**
     * Uploads the given multipart file to a storage location and returns its accessible URL or path.
     *
     * @param file the multipart file to be uploaded
     * @return the public URL or local path of the uploaded image
     * @throws IOException if an error occurs during file upload or conversion
     */
    String uploadImage(MultipartFile file) throws IOException;
}
