package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoForItemResponseDto;

@Data
@Builder
public class ItemBooked {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoForItemResponseDto lastBooking;
    private BookingDtoForItemResponseDto nextBooking;
}
