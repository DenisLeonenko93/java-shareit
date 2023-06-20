package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.service.BookingState.REJECTED;

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

    @Override
    public BookingResponseDto bookingConfirmation(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(Booking.class, String.format("ID: %s", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Указанный пользователь не является владельцем предмета.");
        }
        booking.setStatus((approved) ? APPROVED : BookingStatus.REJECTED);
        return mapper.toResponseDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponseDto> findAllBookingsByState(Long userId, String bookingState) {
        userService.findById(userId);
        List<Booking> bookings = new ArrayList<>();
        BookingState state;
        try {
            state = BookingState.valueOf(bookingState);
        } catch (Exception e) {
            throw new UnsupportedStatusException(bookingState);
        }

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingState.WAITING.toString());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, REJECTED.toString());
                break;
            default:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
        }
        return bookings.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        userService.findById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(Booking.class, String.format("ID: %s", bookingId)));
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new DataAccessException("Указанный пользователь не является владельцем предмета или автором бронирования.");
        }
        return mapper.toResponseDto(booking);
    }

    private void checkBookingDate(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания бронирования раньше даты начала.");
        }
    }

    private void itemIsAvailable(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Указанный в бронировании предмет занят.");
        }
    }
}
