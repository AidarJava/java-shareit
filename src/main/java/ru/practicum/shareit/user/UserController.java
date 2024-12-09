package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;


    @GetMapping
    public List<UserDtoOut> getAllUsers() {
        log.info("GET/ Проверка запроса метода getAllUsers");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDtoOut getUserById(@Positive @PathVariable("userId") Long userId) {
        log.info("GET/ Проверка параметров запроса метода getUserById, userId - {}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDtoOut saveUser(@Valid @RequestBody UserDtoIn userDtoIn) {
        log.info("POST/ Проверка параметров запроса метода saveUser, userDtoIn - {}", userDtoIn);
        return userService.saveUser(userDtoIn);
    }

    @PatchMapping("/{userId}")
    public UserDtoOut updateUser(@Positive @PathVariable("userId") Long userId,
                                 @RequestBody UserDtoIn userDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода updateUser, userId - {}, userDtoIn - {} ", userId, userDtoIn);
        return userService.updateUser(userId, userDtoIn);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(@Positive @PathVariable("userId") Long userId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteUserById, userId - {}", userId);
        return userService.deleteUserById(userId);
    }
}

