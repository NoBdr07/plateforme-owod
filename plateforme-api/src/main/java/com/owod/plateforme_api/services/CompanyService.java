package com.owod.plateforme_api.services;

import com.owod.plateforme_api.mappers.CompanyMapper;
import com.owod.plateforme_api.models.dtos.CompanyDTO;
import com.owod.plateforme_api.models.entities.Company;
import com.owod.plateforme_api.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired(required = false)
    private ImageStorageService imageStorageService;

    public List<CompanyDTO> getAllDto() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toDTO)
                .toList();
    }

    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    public CompanyDTO getDtoById(String id) {
        return companyRepository.findById(id)
                .map(companyMapper::toDTO)
                .orElseThrow();
    }

    public Optional<Company> getById(String id) {
        return companyRepository.findById(id);
    }

    public CompanyDTO saveDto(CompanyDTO companyDTO) {
        return companyMapper.toDTO(companyRepository.save(companyMapper.toEntity(companyDTO)));
    }

    public void deleteById(String id) {
        companyRepository.deleteById(id);
    }

    public Company updateFields(String id, Company patch) {
        var entity = companyRepository.findById(id).orElseThrow();

        if (patch.getDescription() != null) entity.setDescription(patch.getDescription());
        if (patch.getRaisonSociale() != null) entity.setRaisonSociale(patch.getRaisonSociale());
        if (patch.getSiretNumber() != null) entity.setSiretNumber(patch.getSiretNumber());
        if (patch.getPhoneNumber() != null) entity.setPhoneNumber(patch.getPhoneNumber());
        if (patch.getEmail() != null) entity.setEmail(patch.getEmail());
        if (patch.getSectors() != null) entity.setSectors(patch.getSectors());
        if (patch.getStage() != null) entity.setStage(patch.getStage());
        if (patch.getRevenue() != null) entity.setRevenue(patch.getRevenue());
        if (patch.getType() != null) entity.setType(patch.getType());
        if (patch.getCountry() != null) entity.setCountry(patch.getCountry());
        if (patch.getCity() != null) entity.setCity(patch.getCity());
        if (patch.getWebsiteUrl() != null) entity.setWebsiteUrl(patch.getWebsiteUrl());
        if (patch.getFinancialSupport() != null) entity.setFinancialSupport(patch.getFinancialSupport());

        return companyRepository.save(entity);
    }

    public Company updateLogo(String id, MultipartFile file) {
        try {
            var entity = companyRepository.findById(id).orElseThrow();

            String url = imageStorageService.uploadImage(file);
            entity.setLogoUrl(url);
            return companyRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file", e);
        }
    }

    public Company updateTeamPhoto(String id, MultipartFile file) {
        try {
            var entity = companyRepository.findById(id).orElseThrow();
            String url = imageStorageService.uploadImage(file);
            entity.setTeamPhotoUrl(url);
            return companyRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file", e);
        }
    }

    public Company addWorks(String id, List<MultipartFile> files) {
        try {
            var entity = companyRepository.findById(id).orElseThrow();
            var urls = new java.util.ArrayList<>(entity.getWorksUrl() != null ? entity.getWorksUrl() : List.of());
            for (var f : files) {
                String url = imageStorageService.uploadImage(f);
                urls.add(url);
            }
            entity.setWorksUrl(urls);
            return companyRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Company deleteWork(String id, String url) {
        var entity = companyRepository.findById(id).orElseThrow();
        if (entity.getWorksUrl() != null) {
            entity.setWorksUrl(entity.getWorksUrl().stream()
                    .filter(u -> !u.equals(url))
                    .toList());
        }
        // Optionnel: imageStorage.delete(url);
        return companyRepository.save(entity);
    }


}
