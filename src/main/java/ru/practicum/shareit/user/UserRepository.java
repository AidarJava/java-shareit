package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.util.List;

public interface UserRepository {
    List<UserDtoOut> findAll();

    UserDtoOut save(UserDtoIn user);

    UserDtoOut getUserById(Long userId);

    UserDtoOut updateUser(UserDtoIn user);

    ResponseEntity<String> deleteUserById(Long userId);
}
