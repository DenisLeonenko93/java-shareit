package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserBookerDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

//TODO прикрутить MapStruct
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User fromUserDto(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static User updateUserFromDto(UserDto oldUser, UserDto userDto) {
        oldUser.setName(userDto.getName() != null ? userDto.getName() : oldUser.getName());
        oldUser.setEmail(userDto.getEmail() != null ? userDto.getEmail() : oldUser.getEmail());
        return fromUserDto(oldUser);
    }

    public static UserBookerDto toBookerDto(User user) {
        return UserBookerDto.builder()
                .id(user.getId())
                .build();
    }
}
