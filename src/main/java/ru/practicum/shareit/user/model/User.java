package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    private Long id;

    @NotBlank(message = "Поле name должно быть заполнено.")
    private String name;

    @NotBlank(message = "Поле email должно быть заполнено.")
    @Email
    @EqualsAndHashCode.Include
    private String email;
}
