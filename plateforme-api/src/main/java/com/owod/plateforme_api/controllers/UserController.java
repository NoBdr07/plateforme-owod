package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.mappers.UserMapper;
import com.owod.plateforme_api.models.dtos.UserDto;
import com.owod.plateforme_api.models.entities.AccountType;
import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * REST controller for managing user operations such as retrieval and friend management.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * Checks if a user has an associated designer account or company account or none.
     *
     * @param userId the ID of the user to check
     * @return ResponseEntity containing DESIGNER, COMPANY or NONE if no account has been created.
     */
    @GetMapping("/{userId}/has-account")
    public ResponseEntity<AccountType> hasDesignerId(@PathVariable String userId) {
        AccountType accountType = userService.hasAccount(userId);
        return ResponseEntity.ok(accountType);
    }

    /**
     * Retrieves a user's details by their user ID.
     *
     * @param userId the ID of the user to retrieve
     * @return ResponseEntity containing the UserDto or 404 if not found
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable String userId) {
        User user = userService.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserDto userDto = userMapper.userToDto(user);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Retrieves a user by email. Requires ADMIN role.
     *
     * @param userEmail the email of the user to retrieve
     * @return ResponseEntity containing the User or 404 if not found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/email/{userEmail}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String userEmail) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        UserDto userDto = userMapper.userToDto(user);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Retrieves the list of designers (friends) for the authenticated user.
     *
     * @param principal security principal of the authenticated user
     * @return ResponseEntity containing a list of Designer or 404 if user not found
     */
    @GetMapping("/friends")
    public ResponseEntity<List<Designer>> getFriends(Principal principal) {
        String currentUserId = principal.getName();
        if (currentUserId != null) {
            List<Designer> friends = userService.getUserFriends(currentUserId);
            return ResponseEntity.ok(friends);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Adds a friend (designer) to the authenticated user's friend list.
     *
     * @param friendId  the ID of the designer to add as a friend
     * @param principal security principal of the authenticated user
     * @return ResponseEntity containing the updated User
     */
    @PostMapping("/add/{friendId}")
    public ResponseEntity<UserDto> addFriend(@PathVariable String friendId, Principal principal) {
        String currentUserId = principal.getName();
        User updatedUser = userService.addFriend(currentUserId, friendId);
        UserDto userDto = userMapper.userToDto(updatedUser);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Removes a friend (designer) from the authenticated user's friend list.
     *
     * @param friendId  the ID of the designer to remove
     * @param principal security principal of the authenticated user
     * @return ResponseEntity containing the updated User
     */
    @PostMapping("/delete/{friendId}")
    public ResponseEntity<UserDto> deleteFriend(@PathVariable String friendId, Principal principal) {
        String currentUserId = principal.getName();
        User updatedUser = userService.deleteFriend(currentUserId, friendId);

        UserDto userDto = userMapper.userToDto(updatedUser);

        return ResponseEntity.ok(userDto);
    }

}
