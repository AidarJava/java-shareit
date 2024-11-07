package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;


import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final HashMap<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public User save(User user) {
        checkConflictEmail(user);
        user.setId(findMaxUserId());
        users.put(findMaxUserId(), user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        checkEmptyUser(userId);
        return users.get(userId);
    }

    @Override
    public User updateUser(User user) {
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
        return user;
    }

    @Override
    public String deleteUserById(Long userId) {
        checkEmptyUser(userId);
        users.remove(userId);
        if (!users.containsKey(userId)) {
            return "Удаление прошло успешно!";
        } else {
            return "Ошибка удаления!";
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

