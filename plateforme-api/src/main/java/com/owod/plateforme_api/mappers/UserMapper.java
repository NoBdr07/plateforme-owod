package com.owod.plateforme_api.mappers;

import com.owod.plateforme_api.models.dtos.UserDto;
import com.owod.plateforme_api.models.entities.User;
import org.springframework.stereotype.Component;

/**
 * Component responsible for converting User entities to UserDto objects.
 */
@Component
public class UserMapper {

    /**
     * Maps a User entity to its corresponding UserDto.
     *
     * @param user the User entity to map; must not be null
     * @return a UserDto containing the user's ID, first name, and last name
     * @throws IllegalArgumentException if the provided user is null
     */
    public UserDto userToDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        return dto;
    }
}
