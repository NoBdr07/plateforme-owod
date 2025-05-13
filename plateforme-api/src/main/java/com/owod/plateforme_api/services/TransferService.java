package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service responsible for transferring ownership of a Designer entity from an admin to a user.
 * <p>
 * Ensures transactional integrity and validation of user and designer existence.
 */
@Service
public class TransferService {

    private final UserService userService;
    private final DesignerService designerService;

    @Autowired
    public TransferService(UserService userService,
                           DesignerService designerService) {
        this.userService = userService;
        this.designerService = designerService;
    }

    /**
     * Transfers the specified Designer to the given User.
     * <p>
     * - Validates that both User and Designer exist.
     * - Ensures the User does not already own a Designer.
     * - Clears the Designer's createdBy field and saves the change.
     * - Assigns the Designer to the User and persists the update.
     *
     * @param userId     the ID of the user to receive the designer
     * @param designerId the ID of the designer to transfer
     * @throws ResponseStatusException if the User or Designer is not found (HTTP 404)
     * @throws IllegalStateException   if the User already has an associated Designer
     */
    @Transactional
    public void transferDesigner(String userId, String designerId) {
        User user = userService.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Designer designer = designerService.findById(designerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Designer not found"));

        if (user.getDesignerId() != null) {
            throw new IllegalStateException("User already has a designer assigned");
        }

        designer.setCreatedBy(null);
        designerService.save(designer);

        user.setDesignerId(designerId);
        userService.save(user);
    }
}
