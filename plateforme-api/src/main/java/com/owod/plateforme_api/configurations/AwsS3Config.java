package com.owod.plateforme_api.configurations;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Spring configuration for creating and initializing the AWS S3 client.
 */
@Configuration
public class AwsS3Config {

    /**
     * AWS region used to instantiate the S3 client. Defaults to "eu-west-3".
     */
    @Value("${aws.s3.region:eu-west-3}")
    private String region;

    /**
     * Instance of the S3 client initialized after the configuration is constructed.
     */
    private S3Client s3Client;

    /**
     * Initializes the S3 client with the configured region.
     * This method is automatically invoked after the bean is constructed.
     */
    @PostConstruct
    public void init() {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    /**
     * Exposes the S3 client as a Spring bean for dependency injection in other components.
     *
     * @return the configured S3 client instance
     */
    @Bean
    public S3Client s3Client() {
        return s3Client;
    }
}
