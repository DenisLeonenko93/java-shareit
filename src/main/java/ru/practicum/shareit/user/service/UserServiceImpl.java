package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User findById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userId.toString()));
    }

    @Override
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    @Override
    public User update(Long userId, User user) {
        findById(userId);
        return userRepository.update(userId, user);
    }
}
