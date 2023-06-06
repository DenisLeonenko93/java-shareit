package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class Item {
    private Long id;
    @NotBlank(message = "Поле name должно быть заполнено.")
    private String name;
    @NotBlank(message = "Поле description должно быть заполнено.")
    private String description;
    @NotNull(message = "Поле available должно быть заполнено.")
    private Boolean available;
    private Long ownerId;
    private ItemRequest request;
}
