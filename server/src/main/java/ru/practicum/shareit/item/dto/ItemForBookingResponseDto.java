package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemForBookingResponseDto {
    private Long id;
    private String name;
}
