package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;


    @Test
    void isDesignerOwner_shouldReturnTrue() {
        // GIVEN
        String designerId = "designer123";
        String userId = "user456";
        User user = new User();
        user.setUserId(userId);
        user.setDesignerId(designerId);

        // ici on construit un UserDetails Spring Security en fully-qualified
        org.springframework.security.core.userdetails.UserDetails springUser =
                new org.springframework.security.core.userdetails.User(
                        userId,
                        "passwordIrrelevant",
                        Collections.emptyList()
                );

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        // WHEN
        boolean result = userService.isDesignerOwner(designerId, springUser);

        // THEN
        assertTrue(result);
    }

    @Test
    void isDesignerOwner_shouldReturnFalse() {
        // GIVEN
        String designerId1 = "designer123";
        String designerId2 = "designer1234";
        String userId = "user456";

        User user = new User();
        user.setUserId(userId);
        user.setDesignerId(designerId2);

        // ici on construit un UserDetails Spring Security
        org.springframework.security.core.userdetails.UserDetails springUser =
                new org.springframework.security.core.userdetails.User(
                        userId,
                        "passwordIrrelevant",
                        Collections.emptyList()
                );

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        // WHEN
        boolean result = userService.isDesignerOwner(designerId1, springUser);

        // THEN
        assertFalse(result);
    }

}
