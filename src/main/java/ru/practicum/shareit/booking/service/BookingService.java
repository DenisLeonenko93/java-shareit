package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

public interface BookingService {

    BookingResponseDto create(Long userId, BookingDto bookingDto);
}
