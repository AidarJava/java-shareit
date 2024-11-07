package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User save(User user);

    User getUserById(Long userId);

    User updateUser(User user);

    String deleteUserById(Long userId);
}
