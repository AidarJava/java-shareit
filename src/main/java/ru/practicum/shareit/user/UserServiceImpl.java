package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDtoOut> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDtoOut saveUser(UserDtoIn userDtoIn) {
        return userRepository.save(userDtoIn);
    }

    @Override
    public UserDtoOut getUserById(Long userId) {
        return userRepository.getUserById(userId);
    }

    @Override
    public UserDtoOut updateUser(Long userId, UserDtoIn userDtoIn) {
        userDtoIn.setId(userId);
        return userRepository.updateUser(userDtoIn);
    }

    @Override
    public ResponseEntity<String> deleteUserById(Long userId) {
        return userRepository.deleteUserById(userId);
    }
}

