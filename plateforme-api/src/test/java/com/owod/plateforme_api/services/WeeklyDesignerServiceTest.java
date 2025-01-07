package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.WeeklyDesigner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeeklyDesignerServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private DesignerService designerService;

    @InjectMocks
    private WeeklyDesignerService weeklyDesignerService;

    @Test
    void selectNewWeeklyDesigner_shouldSelectAndSaveNewDesigner() {
        // GIVEN
        // Créez deux designers, designer1 sera le précédent WeeklyDesigner
        Designer designer1 = new Designer();
        designer1.setId("designer1");

        Designer designer2 = new Designer();
        designer2.setId("designer2");

        WeeklyDesigner lastWeeklyDesigner = new WeeklyDesigner();
        lastWeeklyDesigner.setDesignerId("designer1"); // Designer 1 est le précédent

        // Simulez les données
        when(designerService.getAll()).thenReturn(List.of(designer1, designer2));
        when(mongoTemplate.findOne(any(Query.class), eq(WeeklyDesigner.class))).thenReturn(lastWeeklyDesigner);

        // WHEN
        weeklyDesignerService.selectNewWeeklyDesigner();

        // THEN
        // Capturez l'objet sauvegardé pour le vérifier
        ArgumentCaptor<WeeklyDesigner> captor = ArgumentCaptor.forClass(WeeklyDesigner.class);
        verify(mongoTemplate, times(1)).save(captor.capture());

        WeeklyDesigner savedWeeklyDesigner = captor.getValue();

        // Vérifiez que le nouveau WeeklyDesigner est bien le designer2
        assertNotNull(savedWeeklyDesigner);
        assertEquals("designer2", savedWeeklyDesigner.getDesignerId()); // Designer 2 doit être sélectionné
        verify(mongoTemplate, times(1)).findOne(any(Query.class), eq(WeeklyDesigner.class));
        verify(designerService, times(1)).getAll();
    }


    @Test
    void selectNewWeeklyDesigner_shouldNotSaveWhenNoDesignersAvailable() {
        // GIVEN
        when(designerService.getAll()).thenReturn(List.of());

        // WHEN
        weeklyDesignerService.selectNewWeeklyDesigner();

        // THEN
        verify(mongoTemplate, never()).save(any(WeeklyDesigner.class));
        verify(designerService, times(1)).getAll();
    }

    @Test
    void getLastWeeklyDesigner_shouldReturnMostRecentDesigner() {
        // GIVEN
        WeeklyDesigner weeklyDesigner = new WeeklyDesigner();
        weeklyDesigner.setDesignerId("designer1");

        when(mongoTemplate.findOne(any(Query.class), eq(WeeklyDesigner.class))).thenReturn(weeklyDesigner);

        // WHEN
        WeeklyDesigner result = weeklyDesignerService.getLastWeeklyDesigner();

        // THEN
        assertNotNull(result);
        assertEquals("designer1", result.getDesignerId());
        verify(mongoTemplate, times(1)).findOne(any(Query.class), eq(WeeklyDesigner.class));
    }
}
