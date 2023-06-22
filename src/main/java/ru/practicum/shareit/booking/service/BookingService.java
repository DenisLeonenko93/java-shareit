package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto bookingConfirmation(Long userId, Long bookingId, Boolean approved);

    List<BookingResponseDto> findAllBookingsByState(Long userId, String state);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> findAllBookingsByItemOwner(Long userId, String state);
}
