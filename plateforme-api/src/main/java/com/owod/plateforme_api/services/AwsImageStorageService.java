package com.owod.plateforme_api.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Service implementation of ImageStorageService that uploads images to AWS S3.
 * <p>
 * Initializes an S3 client using configured AWS region and bucket name, and
 * provides methods to convert multipart files and upload them to S3.
 */
@Service
@Profile("prod")
public class AwsImageStorageService implements ImageStorageService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region:eu-west-3}")
    private String region;

    private S3Client s3Client;

    /**
     * Initializes the S3 client after the bean is constructed.
     */
    @PostConstruct
    public void init() {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    /**
     * Uploads a MultipartFile to the configured S3 bucket with a random UUID prefix.
     * <p>
     * Converts the multipart file to a local File, uploads it, and returns the public URL.
     * The temporary file is deleted after upload.
     *
     * @param file the multipart file to upload
     * @return the public URL of the uploaded image
     * @throws IOException if an error occurs during file conversion or upload
     */
    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File convertedFile = convertMultipartFileToFile(file);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.putObject(putObjectRequest, convertedFile.toPath());

        // Delete the local temporary file
        convertedFile.delete();

        // Return the public URL of the uploaded object
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    /**
     * Converts a MultipartFile to a temporary File on the local filesystem.
     *
     * @param file the multipart file to convert
     * @return a File containing the multipart file's data
     * @throws IOException if an error occurs during file writing
     */
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }
}
