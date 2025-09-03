package com.owod.plateforme_api.services;

import com.owod.plateforme_api.mappers.CompanyMapper;
import com.owod.plateforme_api.models.dtos.CompanyDTO;
import com.owod.plateforme_api.models.entities.Company;
import com.owod.plateforme_api.repositories.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    CompanyRepository companyRepository;

    @Mock
    CompanyMapper companyMapper;

    @Mock
    ImageStorageService imageStorageService;

    @InjectMocks
    CompanyService companyService;

    @Test
    void shouldReturnAllCompaniesAsDto() {
        // GIVEN
        Company company1 = new Company();
        company1.setId("1L");
        company1.setRaisonSociale("Company A");
        Company company2 = new Company();
        company2.setId("2L");
        company2.setRaisonSociale("Company B");

        List<Company> companies = List.of(company1, company2);
        when(companyRepository.findAll()).thenReturn(companies);

        CompanyDTO dto1 = new CompanyDTO();
        dto1.setId("1L");
        dto1.setRaisonSociale("Company A");
        CompanyDTO dto2 = new CompanyDTO();
        dto2.setId("2L");
        dto2.setRaisonSociale("Company B");

        when(companyMapper.toDTO(company1)).thenReturn(dto1);
        when(companyMapper.toDTO(company2)).thenReturn(dto2);

        // WHEN
        List<CompanyDTO> result = companyService.getAllDto();

        // THEN
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        verify(companyRepository).findAll();
        verify(companyMapper).toDTO(company1);
        verify(companyMapper).toDTO(company2);
    }

    @Test
    void getAll_shouldReturnAllCompanies() {
        // GIVEN
        Company company1 = new Company();
        company1.setId("1L");
        company1.setRaisonSociale("Company A");
        company1.setRevenue("100");
        Company company2 = new Company();
        company2.setId("2L");
        company2.setRaisonSociale("Company B");
        company2.setRevenue("200");

        List<Company> companies = List.of(company1, company2);
        when(companyRepository.findAll()).thenReturn(companies);

        // WHEN
        List<Company> result = companyService.getAll();
        Company result1 = result.get(0);
        Company result2 = result.get(1);

        // THEN
        assertEquals(2, result.size());
        assertEquals(company1, result1);
        assertEquals("100", result1.getRevenue());
        assertEquals(company2, result2);
        assertEquals("200", result2.getRevenue());
        verify(companyRepository).findAll();
    }

    @Test
    void getDtoById_shouldReturnCompanyDtoWithMatchingId() {
        // GIVEN
        Company company = new Company();
        company.setId("1L");
        company.setRaisonSociale("Company A");

        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId("1L");
        companyDTO.setRaisonSociale("Company A");

        when(companyRepository.findById("1L")).thenReturn(java.util.Optional.of(company));
        when(companyMapper.toDTO(company)).thenReturn(companyDTO);

        // WHEN
        CompanyDTO result = companyService.getDtoById("1L");

        // THEN
        assertEquals(companyDTO, result);
        verify(companyRepository).findById("1L");
        verify(companyMapper).toDTO(company);
    }

    @Test
    void getDtoById_shouldReturnErrorWhenIdNotFound() {
        // GIVEN
        String id = "1L";
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(NoSuchElementException.class, () -> companyService.getDtoById(id));

        verify(companyRepository).findById(id);
        // le mapper ne doit pas être appelé si l'entité est absente
        verifyNoInteractions(companyMapper);
    }

    @Test
    void shouldReturnEntity_whenIdExists() {
        // GIVEN
        String id = "abc";
        Company entity = mock(Company.class); // évite de dépendre du constructeur
        when(companyRepository.findById(id)).thenReturn(Optional.of(entity));

        // WHEN
        Optional<Company> result = companyService.getById(id);

        // THEN
        assertTrue(result.isPresent());
        assertSame(entity, result.get());
        verify(companyRepository).findById(id);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void shouldReturnEmpty_whenIdDoesNotExist() {
        // GIVEN
        String id = "missing";
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN
        Optional<Company> result = companyService.getById(id);

        // THEN
        assertTrue(result.isEmpty());
        verify(companyRepository).findById(id);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void shouldThrowIllegalArgument_whenIdIsNull() {
        // Spring Data JPA jette IllegalArgumentException pour findById(null)
        when(companyRepository.findById("id"))
                .thenThrow(new IllegalArgumentException("id must not be null"));

        assertThrows(IllegalArgumentException.class, () -> companyService.getById("id"));

        verify(companyRepository).findById("id");
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void saveDto_shouldReturnCreatedDTO() {
        // GIVEN
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId("1L");
        companyDTO.setRaisonSociale("Company A");

        Company entity = new Company();
        entity.setId("1L");
        entity.setRaisonSociale("Company A");

        when(companyMapper.toEntity(companyDTO)).thenReturn(entity);
        when(companyMapper.toDTO(entity)).thenReturn(companyDTO);
        when(companyRepository.save(entity)).thenReturn(entity);

        // WHEN
        CompanyDTO result = companyService.saveDto(companyDTO);

        // THEN
        assertEquals(companyDTO, result);
        verify(companyMapper).toEntity(companyDTO);
        verify(companyRepository).save(entity);
    }


    @Test
    void updateFields_shouldMergeOnlyNonNullFields_andPersist() {
        // GIVEN
        String id = "1";
        Company existing = new Company();
        existing.setId(id);
        existing.setRaisonSociale("Old");
        existing.setDescription("Keep");
        existing.setCity("Paris");

        Company patch = new Company();
        patch.setRaisonSociale("New"); // doit être MAJ
        patch.setDescription(null);    // doit rester "Keep"
        patch.setCity(null);           // doit rester "Paris"

        when(companyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        Company saved = companyService.updateFields(id, patch);

        // THEN
        assertEquals("New", saved.getRaisonSociale());
        assertEquals("Keep", saved.getDescription());
        assertEquals("Paris", saved.getCity());

        InOrder io = inOrder(companyRepository);
        io.verify(companyRepository).findById(id);
        io.verify(companyRepository).save(existing); // on sauvegarde l'entité MERGÉE (même instance)
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void updateFields_shouldThrow_whenIdNotFound() {
        when(companyRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> companyService.updateFields("missing", new Company()));
        verify(companyRepository).findById("missing");
        verifyNoMoreInteractions(companyRepository);
    }


    @Test
    void updateLogo_shouldUploadAndPersistUrl() throws IOException {
        String id = "1";
        MultipartFile file = mock(MultipartFile.class);
        Company existing = new Company(); existing.setId(id);

        when(companyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(imageStorageService.uploadImage(file)).thenReturn("https://cdn/logo.png");
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        Company saved = companyService.updateLogo(id, file);

        assertEquals("https://cdn/logo.png", saved.getLogoUrl());
        InOrder io = inOrder(companyRepository, imageStorageService);
        io.verify(companyRepository).findById(id);
        io.verify(imageStorageService).uploadImage(file);
        io.verify(companyRepository).save(existing);
        verifyNoMoreInteractions(companyRepository, imageStorageService);
    }

    @Test
    void updateTeamPhoto_shouldUploadAndPersistUrl() throws IOException {
        String id = "1";
        MultipartFile file = mock(MultipartFile.class);
        Company existing = new Company(); existing.setId(id);

        when(companyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(imageStorageService.uploadImage(file)).thenReturn("https://cdn/team.png");
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        Company saved = companyService.updateTeamPhoto(id, file);

        assertEquals("https://cdn/team.png", saved.getTeamPhotoUrl());
        verify(companyRepository).findById(id);
        verify(imageStorageService).uploadImage(file);
        verify(companyRepository).save(existing);
        verifyNoMoreInteractions(companyRepository, imageStorageService);
    }

    @Test
    void addWorks_shouldAppendToExistingList_andPersist() throws IOException {
        String id = "1";
        MultipartFile f1 = mock(MultipartFile.class);
        MultipartFile f2 = mock(MultipartFile.class);

        Company existing = new Company();
        existing.setId(id);
        existing.setWorksUrl(List.of("a.jpg", "b.jpg")); // existant

        when(companyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(imageStorageService.uploadImage(f1)).thenReturn("c.jpg");
        when(imageStorageService.uploadImage(f2)).thenReturn("d.jpg");
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        Company saved = companyService.addWorks(id, List.of(f1, f2));

        assertEquals(List.of("a.jpg", "b.jpg", "c.jpg", "d.jpg"), saved.getWorksUrl());
        verify(imageStorageService).uploadImage(f1);
        verify(imageStorageService).uploadImage(f2);
        verify(companyRepository).save(existing);
    }

    @Test
    void addWorks_shouldInitializeList_whenNull() throws IOException {
        String id = "1";
        MultipartFile f = mock(MultipartFile.class);

        Company existing = new Company(); existing.setId(id);
        existing.setWorksUrl(null); // cas important

        when(companyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(imageStorageService.uploadImage(f)).thenReturn("x.jpg");
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        Company saved = companyService.addWorks(id, List.of(f));

        assertEquals(List.of("x.jpg"), saved.getWorksUrl());
    }

    @Test
    void addWorks_shouldWrapException_andNotSave() throws IOException {
        String id = "1";
        MultipartFile f = mock(MultipartFile.class);
        Company existing = new Company(); existing.setId(id);

        when(companyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(imageStorageService.uploadImage(f)).thenThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class, () -> companyService.addWorks(id, List.of(f)));
        verify(companyRepository, never()).save(any());
    }


    @Test
    void deleteWork_shouldNotNPE_whenListIsNull_andStillPersist() {
        String id = "1";
        Company existing = new Company();
        existing.setId(id);
        existing.setWorksUrl(null);

        when(companyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        Company saved = companyService.deleteWork(id, "a.jpg");

        assertNull(saved.getWorksUrl()); // reste null
        verify(companyRepository).save(existing);
    }
}