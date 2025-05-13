package com.owod.plateforme_api.configurations;

import com.owod.plateforme_api.services.ImageStorageService;
import com.owod.plateforme_api.services.AwsImageStorageService;
import com.owod.plateforme_api.services.LocalImageStorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Spring configuration for selecting the appropriate ImageStorageService
 * implementation based on the active profile.
 */
@Configuration
public class ImageStorageConfig {

    /**
     * Registers a LocalImageStorageService bean when the "dev" profile is active.
     * This implementation stores images locally for development purposes.
     *
     * @return an instance of LocalImageStorageService
     */
    @Bean
    @Profile("dev")
    public ImageStorageService localImageStorageService() {
        return new LocalImageStorageService();
    }

    /**
     * Registers an AwsImageStorageService bean when the "prod" profile is active.
     * This implementation stores images in AWS S3 for production environments.
     *
     * @return an instance of AwsImageStorageService
     */
    @Bean
    @Profile("prod")
    public ImageStorageService awsImageStorageService() {
        return new AwsImageStorageService();
    }
}
