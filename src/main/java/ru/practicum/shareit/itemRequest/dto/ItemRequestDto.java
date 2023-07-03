package ru.practicum.shareit.itemRequest.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Поле description должно быть заполнено.")
    private String description;
    private Instant created;
    private List<ItemDto> items;
}
