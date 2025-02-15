package com.owod.plateforme_api.services;

import com.mongodb.DuplicateKeyException;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Find a user from an email
     * @param email
     * @return the User if it exists
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * Checks if a User exists from an email
     * @param email
     * @return a boolean
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Method to save a new User
     * @param user
     * @return the newly created User
     */
    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Cet email est déjà utilisé !");
        }
    }

    /**
     * Method to check if a user is linked to a designer account or not
     * @param userId
     * @return a boolean
     */
    public boolean hasDesignerAccount(String userId) {
        return userRepository.findByUserId(userId)
                .map(user -> user.getDesignerId() != null)
                .orElse(false);
    }

    /**
     * Method to check if the user that send the request is the owner of the designer
     * @param designerId
     * @param principal
     * @return
     */
    public boolean isDesignerOwner(String designerId, Principal principal) {
        String userId = principal.getName();
        Optional<User> optionalUser = findByUserId(userId);

        return optionalUser.isPresent() && optionalUser.get().getDesignerId().equals(designerId);
    }

    public Optional<User> findByResetToken(String token) {
        return userRepository.findByResetToken(token);
    }

    public void deleteDesignerId(String userId) {
        Optional<User> optUser = findByUserId(userId);
        if(optUser.isPresent()) {
            User user = optUser.get();
            user.setDesignerId(null);
            save(user);
        }
    }

}
