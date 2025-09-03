package com.owod.plateforme_api.configuration;

import com.owod.plateforme_api.services.ImageStorageService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.multipart.MultipartFile;

@TestConfiguration
public class MongoTestConfig {
    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory) {
        return new MongoTemplate(mongoDbFactory);
    }

    @Bean
    @Primary // au cas où un bean prod existerait
    public ImageStorageService imageStorageService() {
        return new ImageStorageService() {
            @Override
            public String uploadImage(MultipartFile file) {
                String name = file.getOriginalFilename();
                return "http://fake.local/" + (name == null ? "file" : name);
            }
        };
    }
}