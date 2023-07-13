package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    private User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .email("name@name.ru")
                .name("Name").build();
    }

    @Test
    void userToDto() {
        UserDto userDto = mapper.userToDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(),
                userDto.getEmail());
    }

    @Test
    void userFromDto() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("name@name.ru")
                .name("Name").build();

        User actualUser = mapper.userFromDto(userDto);

        assertEquals(1L, actualUser.getId());
        assertEquals("Name", actualUser.getName());
        assertEquals("name@name.ru", actualUser.getEmail());
    }

    @Test
    void updateUserFromDto_whenEmailIsNull_thenEmailNotUpdate() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Update").build();

        mapper.updateUserFromDto(userDto, user);

        assertEquals("name@name.ru", user.getEmail());
        assertEquals("Update", user.getName());
    }

    @Test
    void updateUserFromDto_whenNameIsNull_thenNameNotUpdate() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("Update@name.ru").build();

        mapper.updateUserFromDto(userDto, user);

        assertEquals("Update@name.ru", user.getEmail());
        assertEquals("Name", user.getName());
    }

    @Test
    void updateUserFromDto_whenUserDtoIsNull_thenUserNotUpdate() {
        UserDto userDto = UserDto.builder().build();

        mapper.updateUserFromDto(userDto, user);

        assertEquals(1L, user.getId());
        assertEquals("name@name.ru", user.getEmail());
        assertEquals("Name", user.getName());
    }
}