package com.owod.plateforme_api.repositories;

import com.owod.plateforme_api.models.entities.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompanyRepository extends MongoRepository<Company, String> {
}
