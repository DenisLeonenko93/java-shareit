package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserBookerDto;
import ru.practicum.shareit.user.model.User;

//TODO прикрутить MapStruct
public class UserMapper {

    public static UserBookerDto toBookerDto(User user) {
        return UserBookerDto.builder()
                .id(user.getId())
                .build();
    }
}
