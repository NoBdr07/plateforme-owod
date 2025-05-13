package com.owod.plateforme_api.repositories;


import com.owod.plateforme_api.models.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on User entities in MongoDB.
 * Provides methods to find users by email, check email existence, retrieve by userId, and find by reset token.
 */
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findByUserId(String id);

    Optional<User> findByResetToken(String token);
}
