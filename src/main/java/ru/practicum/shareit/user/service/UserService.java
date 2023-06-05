package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User findById(Long userId);

    User create(User user);

    void delete(Long userId);

    User update(Long userId, User user);
}
