package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDtoOut> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::mapUserToUserDtoOut)
                .toList();
    }

    @Transactional
    @Override
    public UserDtoOut saveUser(UserDtoIn userDtoIn) {
        return userMapper.mapUserToUserDtoOut(userRepository.save(userMapper.mapUserDtoInToUser(userDtoIn)));
    }

    @Override
    public UserDtoOut getUserById(Long userId) {
        return userMapper.mapUserToUserDtoOut(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет в базе!")));
    }

    @Transactional
    @Override
    public UserDtoOut updateUser(Long userId, UserDtoIn userDtoIn) {
        UserDtoOut userOld = getUserById(userId);
        userDtoIn.setId(userId);
        if (userDtoIn.getEmail() == null) {
            userDtoIn.setEmail(userOld.getEmail());
        }
        if (userDtoIn.getName() == null) {
            userDtoIn.setName(userOld.getName());
        }
        return userMapper.mapUserToUserDtoOut(userRepository.save(userMapper.mapUserDtoInToUser(userDtoIn)));
    }

    @Transactional
    @Override
    public ResponseEntity<String> deleteUserById(Long userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.ok("Удаление прошло успешно!");
    }

}

