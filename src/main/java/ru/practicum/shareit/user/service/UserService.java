package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();
    UserDto findById(Long userId);
    UserDto create(User user);
    void delete(Long userId);
    UserDto update(Long userId, User user);
}
