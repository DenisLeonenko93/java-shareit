package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Поле name должно быть заполнено.")
    private String name;

    @NotBlank(message = "Поле email должно быть заполнено.")
    @Email(message = "Введен неправильный формат поля email.")
    private String email;
}
