package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.repositories.DesignerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesignerServiceTest {

    @Mock
    DesignerRepository designerRepository;

    @InjectMocks
    DesignerService designerService;

    @Test
    void getAll() {
        // GIVEN
        Designer designer1 = new Designer();
        designer1.setLastname("Doe");
        Designer designer2 = new Designer();
        designer2.setLastname("Smith");

        List<Designer> designers = List.of(designer1, designer2);

        when(designerRepository.findAll()).thenReturn(designers);

        // WHEN
        List<Designer> designersFound = designerService.getAll();

        // THEN
        assertEquals(designers, designersFound);
        assertEquals(2, designersFound.size());
        assertEquals("Doe", designersFound.get(0).getLastname());
        assertEquals("Smith", designersFound.get(1).getLastname());
        verify(designerRepository, times(1)).findAll();

    }

    @Test
    void save() {
        // GIVEN
        Designer designer = new Designer();
        designer.setLastname("Doe");
        when(designerRepository.save(designer)).thenReturn(designer);

        // WHEN
        Designer savedDesigner = designerService.save(designer);

        // THEN
        assertNotNull(savedDesigner); // Vérifie que le retour n'est pas null
        assertEquals("Doe", savedDesigner.getLastname()); // Vérifie que le champ est correct
        verify(designerRepository, times(1)).save(designer); // Vérifie que save() a été appelé une fois
    }

    @Test
    void findById() {
        // GIVEN
        String id = "123";
        Designer designer = new Designer();
        designer.setId(id);
        designer.setLastname("Doe");

        when(designerRepository.findById(id)).thenReturn(Optional.of(designer));

        // WHEN
        Optional<Designer> foundDesigner = designerService.findById(id);

        // THEN
        assertTrue(foundDesigner.isPresent()); // Vérifie que l'objet est présent
        assertEquals("Doe", foundDesigner.get().getLastname()); // Vérifie le champ lastname
        verify(designerRepository, times(1)).findById(id); // Vérifie que findById() a été appelé une fois
    }

    @Test
    void findById_notFound() {
        // GIVEN
        String id = "123";
        when(designerRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN
        Optional<Designer> foundDesigner = designerService.findById(id);

        // THEN
        assertFalse(foundDesigner.isPresent()); // Vérifie que l'objet n'est pas présent
        verify(designerRepository, times(1)).findById(id); // Vérifie que findById() a été appelé une fois
    }

    @Test
    void findBySpecialty() {
        // GIVEN
        String specialty = "Web Design";
        Designer designer1 = new Designer();
        designer1.setLastname("Doe");
        designer1.setSpecialties(List.of("Web Design", "Graphic Design"));

        Designer designer2 = new Designer();
        designer2.setLastname("Smith");
        designer2.setSpecialties(List.of("Web Design"));

        List<Designer> designers = List.of(designer1, designer2);

        when(designerRepository.findBySpecialtiesContaining(specialty)).thenReturn(designers);

        // WHEN
        List<Designer> foundDesigners = designerService.findBySpecialty(specialty);

        // THEN
        assertEquals(2, foundDesigners.size()); // Vérifie le nombre d'éléments trouvés
        assertEquals("Doe", foundDesigners.get(0).getLastname()); // Vérifie le premier élément
        assertEquals("Smith", foundDesigners.get(1).getLastname()); // Vérifie le second élément
        verify(designerRepository, times(1)).findBySpecialtiesContaining(specialty); // Vérifie que la méthode a été appelée une fois
    }

}