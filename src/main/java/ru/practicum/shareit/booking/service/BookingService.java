package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@Component
public interface BookingService {

    BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto bookingConfirmation(Long userId, Long bookingId, Boolean approved);

    List<BookingResponseDto> getAllBookingsByState(Long userId,
                                                   String state,
                                                   Integer from,
                                                   Integer size,
                                                   Boolean hasOwner);

    BookingResponseDto getBookingById(Long userId, Long bookingId);
}
