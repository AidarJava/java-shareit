package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.util.List;

interface UserService {
    List<UserDtoOut> getAllUsers();

    UserDtoOut saveUser(UserDtoIn userDtoIn);

    UserDtoOut getUserById(Long userId);

    UserDtoOut updateUser(Long userId, UserDtoIn userDtoIn);

    String deleteUserById(Long userId);
}
