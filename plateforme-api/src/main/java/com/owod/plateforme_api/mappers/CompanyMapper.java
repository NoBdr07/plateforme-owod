package com.owod.plateforme_api.mappers;

import com.owod.plateforme_api.models.dtos.CompanyDTO;
import com.owod.plateforme_api.models.entities.Company;

public class CompanyMapper {

    public static CompanyDTO toDTO(Company company) {
        if (company == null) {
            return null;
        }

        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setEmail(company.getEmail());
        dto.setRaisonSociale(company.getRaisonSociale());
        dto.setSiretNumber(company.getSiretNumber());
        dto.setPhoneNumber(company.getPhoneNumber());
        dto.setSector(company.getSector());
        dto.setStage(company.getStage());
        dto.setType(company.getType());
        dto.setCountry(company.getCountry());
        dto.setCity(company.getCity());
        dto.setLogoUrl(company.getLogoUrl());
        dto.setWebsiteUrl(company.getWebsiteUrl());
        dto.setPhotosUrl(company.getPhotosUrl());
        dto.setWorksUrl(company.getWorksUrl());
        dto.setEmployeesId(company.getEmployeesId());
        return dto;
    }

    public static Company toEntity(CompanyDTO dto) {
        if (dto == null) {
            return null;
        }

        Company company = new Company();
        company.setId(dto.getId());
        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setEmail(dto.getEmail());
        company.setRaisonSociale(dto.getRaisonSociale());
        company.setSiretNumber(dto.getSiretNumber());
        company.setPhoneNumber(dto.getPhoneNumber());
        company.setSector(dto.getSector());
        company.setStage(dto.getStage());
        company.setType(dto.getType());
        company.setCountry(dto.getCountry());
        company.setCity(dto.getCity());
        company.setLogoUrl(dto.getLogoUrl());
        company.setWebsiteUrl(dto.getWebsiteUrl());
        company.setPhotosUrl(dto.getPhotosUrl());
        company.setWorksUrl(dto.getWorksUrl());
        company.setEmployeesId(dto.getEmployeesId());

        // revenue et financialSupport ne sont pas dans le DTO, tu peux les gérer ailleurs
        return company;
    }
}
