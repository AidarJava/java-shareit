package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDtoOut> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::mapUserToUserDtoOut)
                .toList();
    }

    @Override
    public UserDtoOut saveUser(UserDtoIn userDtoIn) {
        User user = userMapper.mapUserDtoInToUser(userDtoIn);
        return userMapper.mapUserToUserDtoOut(userRepository.save(user));
    }

    @Override
    public UserDtoOut getUserById(Long userId) {
        return userMapper.mapUserToUserDtoOut(userRepository.getUserById(userId));
    }

    @Override
    public UserDtoOut updateUser(Long userId, UserDtoIn userDtoIn) {
        userDtoIn.setId(userId);
        User user = userMapper.mapUserDtoInToUser(userDtoIn);
        return userMapper.mapUserToUserDtoOut(userRepository.updateUser(user));
    }

    @Override
    public String deleteUserById(Long userId) {
        return userRepository.deleteUserById(userId);
    }
}

