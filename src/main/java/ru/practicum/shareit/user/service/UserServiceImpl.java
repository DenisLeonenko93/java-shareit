package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.ExistValid;
import ru.practicum.shareit.util.ModelType;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", userId)));
        return userMapper.userToDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.userFromDto(userDto);
        return userMapper.userToDto(userRepository.save(user));
    }


    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", userId)));
        userMapper.updateUserFromDto(userDto, user);
        return userMapper.userToDto(userRepository.saveAndFlush(user));
    }
}
