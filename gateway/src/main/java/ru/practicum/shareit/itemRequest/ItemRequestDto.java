package ru.practicum.shareit.itemRequest;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Поле description должно быть заполнено.")
    private String description;
    private Instant created;
    private List<ItemDto> items;
}
