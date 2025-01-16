package com.owod.plateforme_api.configurations;

import com.owod.plateforme_api.services.ImageStorageService;
import com.owod.plateforme_api.services.AwsImageStorageService;
import com.owod.plateforme_api.services.LocalImageStorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ImageStorageConfig {

    @Bean
    @Profile("dev")
    public ImageStorageService localImageStorageService() {
        return new LocalImageStorageService();
    }

    @Bean
    @Profile("prod")
    public ImageStorageService ionosImageStorageService() {
        return new AwsImageStorageService();
    }
}
