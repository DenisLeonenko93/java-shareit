package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();

    Optional<User> findById(Long userId);

    User create(User user);

    void delete(Long userId);

    User update(Long userId, User user);
}
