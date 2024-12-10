package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.repositories.DesignerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DesignerService {

    @Autowired
    private DesignerRepository designerRepository;

    public List<Designer> getAll() {
        return designerRepository.findAll();
    }

    public Designer save(Designer designer) {
        return designerRepository.save(designer);
    }

    public Optional<Designer> findById(String id) {
        return designerRepository.findById(id);
    }

    public List<Designer> findBySpecialty(String specialty) {
        return designerRepository.findBySpecialtiesContaining(specialty);
    }


}
