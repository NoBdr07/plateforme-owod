package com.owod.plateforme_api.services;

import com.mongodb.DuplicateKeyException;
import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DesignerService designerService;

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

    public void deleteDesigner(String userId, String designerId) {
        Optional<User> optUser = findByUserId(userId);

        designerService.delete(designerId);

        if(optUser.isPresent()) {
            User user = optUser.get();
            user.setDesignerId(null);
            save(user);
        }
    }

    public List<Designer> getUserFriends(String userId) {
        return findByUserId(userId)
                .map(user -> user.getFriendsId().stream()
                        .map(friendId -> designerService.findById(friendId))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public User addFriend(String userId, String friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Initialiser la collection si elle est null
        if (user.getFriendsId() == null) {
            user.setFriendsId(new HashSet<>());
        }

        // Ajouter l'ami s'il n'est pas déjà présent
        user.getFriendsId().add(friendId);

        // Sauvegarder l'utilisateur mis à jour
        return userRepository.save(user);
    }

    public User deleteFriend(String userId, String friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.getFriendsId().remove(friendId);

        return userRepository.save(user);
    }

}
