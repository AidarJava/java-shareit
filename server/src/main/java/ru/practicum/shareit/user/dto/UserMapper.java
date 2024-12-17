package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;

@Service
public class UserMapper {
    public User mapUserDtoInToUser(UserDtoIn userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public UserDtoOut mapUserToUserDtoOut(User user) {
        return UserDtoOut.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
