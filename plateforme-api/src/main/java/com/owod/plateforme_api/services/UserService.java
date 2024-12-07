package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return userRepository.save(user);
    }

}
