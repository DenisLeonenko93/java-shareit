package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void getAll_whenInvoked_thenReturnUserDtoCollections() {
        User user = new User();
        UserDto userDto = new UserDto();
        List<User> usersFromRepository = List.of(user);
        List<UserDto> expectedUsersDto = List.of(userDto);
        when(userRepository.findAll()).thenReturn(usersFromRepository);
        when(userMapper.userToDto(user)).thenReturn(userDto);

        List<UserDto> actualUsersDto = userService.getAll();

        assertEquals(expectedUsersDto, actualUsersDto);
    }

    @Test
    void findById_whenUserFound_thenReturnedUserDto() {
        long userId = 0L;
        User expectedUser = new User();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));
        UserDto expectedUserDto = new UserDto();
        when(userMapper.userToDto(expectedUser))
                .thenReturn(expectedUserDto);

        UserDto actualUser = userService.findById(userId);

        assertEquals(expectedUserDto, actualUser);
    }

    @Test
    void findById_whenUserNotFound_thenEntityNotFoundExceptionThrow() {
        long userId = 0L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.findById(userId));

        assertEquals("Entity User not found by ID: 0", entityNotFoundException.getMessage());
    }

    @Test
    void create_whenCreateUser_returnUserDto() {
        User userToSave = User.builder()
                .id(1L)
                .name("Name")
                .email("test@mail.ru").build();
        UserDto expectedUserDto = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("test@mail.ru").build();
        when(userMapper.userFromDto(expectedUserDto)).thenReturn(userToSave);
        when(userRepository.save(userToSave)).thenReturn(userToSave);
        when(userMapper.userToDto(userToSave))
                .thenReturn(expectedUserDto);

        UserDto actualUserDto = userService.create(expectedUserDto);

        assertEquals(expectedUserDto, actualUserDto);
        verify(userRepository).save(userToSave);
    }

    @Test
    void delete_whenInvoke_thenInvokeUserRepository() {
        userService.delete(0L);

        verify(userRepository).deleteById(0L);
    }

    @Test
    void update_whenUserFound_thenUpdatedOnlyAvailableFields() {
        Long userId = 0L;
        User oldUser = User.builder()
                .id(0L)
                .name("Name")
                .email("test@mail.ru").build();
        UserDto newUserDto = UserDto.builder()
                .name("Update").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        doAnswer(invocation -> {
            UserDto userDto = invocation.getArgument(0, UserDto.class);
            User user = invocation.getArgument(1, User.class);
            user.setName(userDto.getName());
            return null;
        }).when(userMapper).updateUserFromDto(any(UserDto.class), any(User.class));

        userService.update(userId, newUserDto);

        verify(userMapper).updateUserFromDto(newUserDto, oldUser);
        verify(userRepository).saveAndFlush(userArgumentCaptor.capture());

        User savedUser = userArgumentCaptor.getValue();

        assertEquals(0L, savedUser.getId());
        assertEquals("Update", savedUser.getName());
        assertEquals("test@mail.ru", savedUser.getEmail());
    }

    @Test
    void update_whenUserNotFound_thenEntityNotFoundExceptionThrow() {
        Long userId = 0L;
        User oldUser = User.builder()
                .id(0L)
                .name("Name")
                .email("test@mail.ru").build();
        User newUser = User.builder()
                .id(0L)
                .name("Update")
                .email("test@mail.ru").build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.findById(userId));
        verify(userRepository, never()).saveAndFlush(newUser);
        verify(userRepository, never()).saveAndFlush(oldUser);
    }
}