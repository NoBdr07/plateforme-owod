package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.DesignerEvent;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.DesignerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public void delete(String designerId) {
        designerRepository.deleteById(designerId);
    }

    public Designer addEvent(String designerId, DesignerEvent event) {
        Designer designer = designerRepository.findById(designerId)
                .orElseThrow(() -> new UsernameNotFoundException("designer not found"));

        designer.getEvents().add(event);

        return designerRepository.save(designer);
    }

    public Designer modifyEvent(String designerId, DesignerEvent eventToModify) {
        Designer designer = designerRepository.findById(designerId)
                .orElseThrow(() -> new UsernameNotFoundException("designer not found"));

        List<DesignerEvent> events = designer.getEvents();

        int indexToModify = -1;
        for(int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(eventToModify.getId())) {
                indexToModify = i;
                break;
            }
        }

        if (indexToModify != -1) {
            DesignerEvent existingEvent = events.get(indexToModify);
            existingEvent.setDescription(eventToModify.getDescription());
            existingEvent.setTitle(eventToModify.getTitle());
            existingEvent.setStartDate(eventToModify.getStartDate());
            existingEvent.setEndDate(eventToModify.getEndDate());
        } else {
            throw new IllegalArgumentException("Event not foud for designer");
        }

        return designerRepository.save(designer);
    }

    public Designer deleteEvent(String designerId, DesignerEvent eventToRemove) {
        Designer designer = designerRepository.findById(designerId)
                .orElseThrow(() -> new UsernameNotFoundException("designer not found"));

        List<DesignerEvent> events = designer.getEvents();

        int indexToDelete = -1;
        for(int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(eventToRemove.getId())) {
                indexToDelete = i;
                break;
            }
        }

        if (indexToDelete != -1) {
            events.remove(indexToDelete);
        } else {
            throw new IllegalArgumentException("Event not foud for designer");
        }

        return designerRepository.save(designer);
    }

    public List<Designer> getDesignersCreatedByAdmin(String userId) {
        return designerRepository.findByCreatedBy(userId);
    }
}
