package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.mappers.UserMapper;
import com.owod.plateforme_api.models.dtos.UserDto;
import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/{userId}/has-designer")
    public ResponseEntity<Boolean> hasDesignerId(@PathVariable String userId) {
        boolean hasDesignerId = userService.hasDesignerAccount(userId);
        return ResponseEntity.ok(hasDesignerId);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable String userId) {
        User user = userService.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserDto userDto = userMapper.userToDto(user);
        return ResponseEntity.ok(userDto);
    }

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

    @PostMapping("/add/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable String friendId, Principal principal) {

        String currentUserId = principal.getName();

        User updatedUser = userService.addFriend(currentUserId, friendId);

        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/delete/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable String friendId, Principal principal) {

        String currentUserId = principal.getName();

        User updatedUser = userService.deleteFriend(currentUserId, friendId);

        return ResponseEntity.ok(updatedUser);
    }

}
