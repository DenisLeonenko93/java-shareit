package ru.practicum.shareit.user.service;

import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.CreateDuplicateEntityException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll(Integer from, Integer size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);

        return userRepository.findAll(page)
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

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        try {
            User user = userMapper.userFromDto(userDto);
            User sacedUser = userRepository.save(user);
            return userMapper.userToDto(sacedUser);
        } catch (DataIntegrityViolationException e) {
            throw new CreateDuplicateEntityException("Пользователь с email уже существует.");
        }
    }


    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", userId)));
        userMapper.updateUserFromDto(userDto, user);
        return userMapper.userToDto(userRepository.saveAndFlush(user));
    }
}
