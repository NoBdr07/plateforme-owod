package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.DesignerEvent;
import com.owod.plateforme_api.repositories.DesignerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Designer entities and their related events.
 * <p>
 * Provides methods for CRUD operations on designers and for adding, modifying,
 * and deleting DesignerEvent instances associated with a Designer.
 */
@Service
public class DesignerService {

    @Autowired
    private DesignerRepository designerRepository;

    /**
     * Retrieves all Designer entities.
     *
     * @return a list of all designers
     */
    public List<Designer> getAll() {
        return designerRepository.findAll();
    }

    /**
     * Saves or updates a Designer entity.
     *
     * @param designer the designer to save
     * @return the persisted Designer entity
     */
    public Designer save(Designer designer) {
        return designerRepository.save(designer);
    }

    /**
     * Finds a Designer by its unique identifier.
     *
     * @param id the ID of the designer to retrieve
     * @return an Optional containing the Designer if found, or empty otherwise
     */
    public Optional<Designer> findById(String id) {
        return designerRepository.findById(id);
    }

    /**
     * Finds designers that have a given specialty.
     *
     * @param specialty the specialty to filter by
     * @return a list of matching Designer entities
     */
    public List<Designer> findBySpecialty(String specialty) {
        return designerRepository.findBySpecialtiesContaining(specialty);
    }

    /**
     * Deletes a Designer by its ID.
     *
     * @param designerId the ID of the designer to delete
     */
    public void delete(String designerId) {
        designerRepository.deleteById(designerId);
    }

    /**
     * Adds an event to the specified Designer's event list.
     *
     * @param designerId the ID of the designer to update
     * @param event the DesignerEvent to add
     * @return the updated Designer entity
     * @throws UsernameNotFoundException if no designer is found with the given ID
     */
    public Designer addEvent(String designerId, DesignerEvent event) {
        Designer designer = designerRepository.findById(designerId)
                .orElseThrow(() -> new UsernameNotFoundException("Designer not found: " + designerId));

        designer.getEvents().add(event);
        return designerRepository.save(designer);
    }

    /**
     * Modifies an existing event of a Designer.
     *
     * @param designerId the ID of the designer containing the event
     * @param eventToModify the event with updated data (must contain a valid event ID)
     * @return the updated Designer entity
     * @throws UsernameNotFoundException if no designer is found with the given ID
     * @throws IllegalArgumentException if the specified event is not found
     */
    public Designer modifyEvent(String designerId, DesignerEvent eventToModify) {
        Designer designer = designerRepository.findById(designerId)
                .orElseThrow(() -> new UsernameNotFoundException("Designer not found: " + designerId));

        List<DesignerEvent> events = designer.getEvents();
        int indexToModify = -1;
        for (int i = 0; i < events.size(); i++) {
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
            throw new IllegalArgumentException("Event not found for designer: " + designerId);
        }

        return designerRepository.save(designer);
    }

    /**
     * Removes an event from a Designer's event list.
     *
     * @param designerId the ID of the designer containing the event
     * @param eventToRemove the event to remove (must contain a valid event ID)
     * @return the updated Designer entity
     * @throws UsernameNotFoundException if no designer is found with the given ID
     * @throws IllegalArgumentException if the specified event is not found
     */
    public Designer deleteEvent(String designerId, DesignerEvent eventToRemove) {
        Designer designer = designerRepository.findById(designerId)
                .orElseThrow(() -> new UsernameNotFoundException("Designer not found: " + designerId));

        List<DesignerEvent> events = designer.getEvents();
        int indexToDelete = -1;
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(eventToRemove.getId())) {
                indexToDelete = i;
                break;
            }
        }

        if (indexToDelete != -1) {
            events.remove(indexToDelete);
        } else {
            throw new IllegalArgumentException("Event not found for designer: " + designerId);
        }

        return designerRepository.save(designer);
    }

    /**
     * Retrieves designers created by a specific admin user.
     *
     * @param userId the ID of the admin user who created designers
     * @return a list of Designer entities created by the specified user
     */
    public List<Designer> getDesignersCreatedByAdmin(String userId) {
        return designerRepository.findByCreatedBy(userId);
    }
}
