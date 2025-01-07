package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    Principal principal;

    @InjectMocks
    UserService userService;


    @Test
    void hasDesignerAccount() {
        // GIVEN
        User user = new User();
        user.setDesignerId("123");

        String id = "123";

        when(userRepository.findByUserId(id)).thenReturn(Optional.of(user));

        // WHEN
        boolean hasDesignerResult = userService.hasDesignerAccount(id);

        // THEN
        assertTrue(hasDesignerResult);
        verify(userRepository, times(1)).findByUserId(id);

    }

    @Test
    void isDesignerOwner_shouldReturnTrue() {
        // GIVEN
        String designerId = "designer123";
        String userId = "user456";
        User user = new User();
        user.setUserId(userId);
        user.setDesignerId(designerId);

        when(principal.getName()).thenReturn(userId);
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        // WHEN
        boolean result = userService.isDesignerOwner(designerId, principal);

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

        when(principal.getName()).thenReturn(userId);
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        // WHEN
        boolean result = userService.isDesignerOwner(designerId1, principal);

        // THEN
        assertFalse(result);
    }

}