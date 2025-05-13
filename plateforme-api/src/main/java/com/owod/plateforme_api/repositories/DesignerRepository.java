package com.owod.plateforme_api.repositories;

import com.owod.plateforme_api.models.entities.Designer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on Designer entities in MongoDB.
 * Provides methods to find designers by specialty and by creator user ID.
 */
public interface DesignerRepository extends MongoRepository<Designer, String> {

    List<Designer> findBySpecialtiesContaining(String specialty);

    List<Designer> findByCreatedBy(String userId);
}
