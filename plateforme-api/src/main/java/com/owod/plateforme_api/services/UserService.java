package com.owod.plateforme_api.services;

import com.mongodb.DuplicateKeyException;
import com.owod.plateforme_api.models.entities.AccountType;
import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for managing User entities, including CRUD operations,
 * password reset token handling, and user-designer relationships.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DesignerService designerService;

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for
     * @return an Optional containing the User if found, or empty otherwise
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds a user by their unique user ID.
     *
     * @param userId the ID of the user to find
     * @return an Optional containing the User if found, or empty otherwise
     */
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * Checks whether a user exists with the given email.
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Saves a new or existing user. Throws an exception if the email is already in use.
     *
     * @param user the User entity to save
     * @return the saved User entity
     * @throws IllegalArgumentException if the email is already used
     */
    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Email is already in use");
        }
    }

    /**
     * Determines if a user has an associated designer account or company account.
     *
     * @param userId the ID of the user to check
     * @return DESIGNER if a designer account exists, COMPANY if a company account exists, NONE otherwise.
     */
public AccountType hasAccount(String userId) {
        return userRepository.findByUserId(userId)
                .map(u -> {
                    if (u.getDesignerId() != null) {
                        return AccountType.DESIGNER;
                    } else if (u.getCompanyId() != null) {
                        return AccountType.COMPANY;
                    } else {
                        return AccountType.NONE;
                    }
                })
                .orElse(AccountType.NONE);
    }

    /**
     * Checks if the authenticated user is the owner of the given designer.
     *
     * @param designerId  the ID of the designer to verify ownership
     * @param userDetails the authenticated user's details
     * @return true if the user owns the designer, false otherwise
     */
    public boolean isDesignerOwner(String designerId, UserDetails userDetails) {
        String userId = userDetails.getUsername();
        return findByUserId(userId)
                .map(u -> designerId.equals(u.getDesignerId()))
                .orElse(false);
    }

    /**
     * Finds a user by a password reset token.
     *
     * @param token the reset token to search for
     * @return an Optional containing the User if the token matches, or empty otherwise
     */
    public Optional<User> findByResetToken(String token) {
        return userRepository.findByResetToken(token);
    }

    /**
     * Deletes the association between a user and their designer, and removes the designer.
     *
     * @param userId     the ID of the user
     * @param designerId the ID of the designer to delete
     */
    public void deleteDesigner(String userId, String designerId) {
        Optional<User> optUser = findByUserId(userId);
        designerService.delete(designerId);
        optUser.ifPresent(user -> {
            user.setDesignerId(null);
            save(user);
        });
    }

    /**
     * Retrieves the list of Designer friends for a given user.
     *
     * @param userId the ID of the user whose friends to retrieve
     * @return a list of Designer entities representing the user's friends
     */
    public List<Designer> getUserFriends(String userId) {
        return findByUserId(userId)
                .map(user -> user.getFriendsId().stream()
                        .map(designerService::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * Adds a friend (designer) to the user's friend list.
     *
     * @param userId   the ID of the user
     * @param friendId the ID of the designer to add as friend
     * @return the updated User entity
     * @throws UsernameNotFoundException if the user is not found
     */
    public User addFriend(String userId, String friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        if (user.getFriendsId() == null) {
            user.setFriendsId(new HashSet<>());
        }
        user.getFriendsId().add(friendId);
        return userRepository.save(user);
    }

    /**
     * Removes a friend (designer) from the user's friend list.
     *
     * @param userId   the ID of the user
     * @param friendId the ID of the designer to remove from friends
     * @return the updated User entity
     * @throws UsernameNotFoundException if the user is not found
     */
    public User deleteFriend(String userId, String friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        if (user.getFriendsId() != null) {
            user.getFriendsId().remove(friendId);
        }
        return userRepository.save(user);
    }
}
