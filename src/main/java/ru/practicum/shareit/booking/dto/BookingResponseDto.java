package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemForBookingResponseDto;
import ru.practicum.shareit.user.dto.UserForBookingResponseDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponseDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private ItemForBookingResponseDto item;
    private UserForBookingResponseDto booker;
}
