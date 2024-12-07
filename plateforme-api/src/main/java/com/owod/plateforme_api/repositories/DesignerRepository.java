package com.owod.plateforme_api.repositories;

import com.owod.plateforme_api.models.entities.Designer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DesignerRepository extends MongoRepository<Designer, String> {

    List<Designer> findBySpecialtiesContaining(String specialty);
}
