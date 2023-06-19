package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper mapper;

    @Override
    public BookingResponseDto create(Long userId, BookingDto bookingDto) {
        checkBookingDate(bookingDto);
        Booking booking = mapper.fromRequestDto(userId, bookingDto);
        itemIsAvailable(booking);
        booking.setStatus(BookingStatus.WAITING);
        return mapper.toResponseDto(bookingRepository.save(booking));
    }

    private void checkBookingDate(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
        bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания бронирования раньше даты начала.");
        }
    }

    private void itemIsAvailable(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Указанная в бронировании вещь занята.");
        }
    }
}
