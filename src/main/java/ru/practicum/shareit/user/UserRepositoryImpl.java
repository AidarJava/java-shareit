package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.dto.UserMapper;


import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDtoOut> findAll() {
        return users.values().stream().map(userMapper::mapUserToUserDtoOut).toList();
    }

    @Override
    public UserDtoOut save(UserDtoIn userDtoIn) {
        User user = userMapper.mapUserDtoInToUser(userDtoIn);
        checkConflictEmail(user);
        user.setId(findMaxUserId());
        users.put(findMaxUserId(), user);
        return userMapper.mapUserToUserDtoOut(user);
    }

    @Override
    public UserDtoOut getUserById(Long userId) {
        checkEmptyUser(userId);
        return userMapper.mapUserToUserDtoOut(users.get(userId));
    }

    @Override
    public UserDtoOut updateUser(UserDtoIn userDtoIn) {
        User user = userMapper.mapUserDtoInToUser(userDtoIn);
        checkEmptyUser(user.getId());
        User upUser = users.get(user.getId());
        if (user.getEmail() != null) {
            checkConflictEmail(user);
            upUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            upUser.setName(user.getName());
        }
        users.put(user.getId(), upUser);
        return userMapper.mapUserToUserDtoOut(user);
    }

    @Override
    public ResponseEntity<String> deleteUserById(Long userId) {
        checkEmptyUser(userId);
        users.remove(userId);
        if (!users.containsKey(userId)) {
            return ResponseEntity.ok("Удаление прошло успешно!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка удаления!");
        }
    }

    private long findMaxUserId() {
        long maxId = users.keySet().stream()
                .mapToLong(user -> user)
                .max()
                .orElse(0);
        return ++maxId;
    }

    private void checkEmptyUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Такого пользователя нет в списке!");
        }
    }

    private void checkConflictEmail(User user) {
        if (!users.values().stream().filter(u -> u.getEmail().equals(user.getEmail())).toList().isEmpty()) {
            throw new ConflictException("Такой email уже существует!");
        }
    }
}

