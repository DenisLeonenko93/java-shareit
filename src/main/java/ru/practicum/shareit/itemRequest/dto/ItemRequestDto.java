package ru.practicum.shareit.itemRequest.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Поле description должно быть заполнено.")
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
