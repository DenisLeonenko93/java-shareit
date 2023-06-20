package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(Long userId, BookingDto bookingDto);

    BookingResponseDto bookingConfirmation(Long userId, Long bookingId, Boolean approved);

    List<BookingResponseDto> findAllBookingsByState(Long userId, String state);

    BookingResponseDto getBookingById(Long userId, Long bookingId);
}
