package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDto savedUser;
    private UserDto expectedUser;

    @BeforeEach
    void beforeEach() {
        savedUser = UserDto.builder().build();
        expectedUser = UserDto.builder().id(0L).build();
    }

    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOKWithUserDtoCollectionsInBody() {
        List<UserDto> expectedUsers = List.of(new UserDto());
        Integer from = 1;
        Integer size = 1;
        when(userService.getAll(from, size))
                .thenReturn(expectedUsers);

        ResponseEntity<List<UserDto>> response = userController.getAllUsers(from, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
    }

    @Test
    void getById_whenInvoked_thenResponseStatusOKWithUserDtoInBody() {
        when(userService.findById(Mockito.anyLong()))
                .thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
    }

    @Test
    void create_whenInvoked_thenResponseStatusCreateWithUserDtoInBody() {
        when(userService.create(savedUser)).thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.create(savedUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
    }

    @Test
    void delete_whenInvoked_thenResponseStatusNoContentWithEmptyBody() {
        ResponseEntity<Void> response = userController.delete(0L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).delete(0L);
    }

    @Test
    void update_whenInvoked_thenResponseStatusOKWithUserDtoInBody() {
        when(userService.update(0L, savedUser)).thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.update(0L, savedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
    }
}