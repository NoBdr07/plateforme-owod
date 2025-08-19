package com.owod.plateforme_api.services;

import com.owod.plateforme_api.mappers.CompanyMapper;
import com.owod.plateforme_api.models.dtos.CompanyDTO;
import com.owod.plateforme_api.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    public List<CompanyDTO> getAllDto() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toDTO)
                .toList();
    }

    public CompanyDTO getDtoById(String id) {
        return companyRepository.findById(id)
                .map(companyMapper::toDTO)
                .orElseThrow();
    }

    public CompanyDTO saveDto(CompanyDTO companyDTO) {
        return companyMapper.toDTO(companyRepository.save(companyMapper.toEntity(companyDTO)));
    }

    public void deleteById(String id) {
        companyRepository.deleteById(id);
    }

}
