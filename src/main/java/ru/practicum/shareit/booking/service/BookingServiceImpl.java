package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.SimpleBookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final SimpleBookingMapper bookingMapper;

    @Override
    public BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto) {
        checkBookingDate(bookingRequestDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", userId)));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(Item.class, String.format("ID: %s", bookingRequestDto.getItemId())));

        if (!item.getAvailable()) {
            throw new ValidationException("Указанный в бронировании предмет занят.");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Пользователь не может забронировать свой предмет.");
        }

        Booking booking = bookingMapper.bookingFromRequestDto(bookingRequestDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return bookingMapper.bookingToResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto bookingConfirmation(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(Booking.class, String.format("ID: %s", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Указанный пользователь не является владельцем предмета.");
        }
        if (booking.getStatus().equals(APPROVED)) {
            throw new ValidationException("Данное бронирование уже подтверждено.");
        }
        booking.setStatus((approved) ? APPROVED : BookingStatus.REJECTED);
        return bookingMapper.bookingToResponseDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponseDto> findAllBookingsByItemOwner(Long userId, String bookingState) {
        userService.findById(userId);
        List<Booking> bookings;
        BookingState state;
        try {
            state = BookingState.valueOf(bookingState);
        } catch (Exception e) {
            throw new UnsupportedStatusException(bookingState);
        }

        if (bookingRepository.findByItemOwnerIdOrderByStartDesc(userId).isEmpty()) {
            throw new EntityNotFoundException("Пользователь не имеет забронированных предметов.");
        }

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
        }
        return bookings.stream()
                .map(bookingMapper::bookingToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> findAllBookingsByState(Long userId, String bookingState) {
        userService.findById(userId);
        List<Booking> bookings;
        BookingState state;
        try {
            state = BookingState.valueOf(bookingState);
        } catch (Exception e) {
            throw new UnsupportedStatusException(bookingState);
        }

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId,
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
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
        }
        return bookings.stream()
                .map(bookingMapper::bookingToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        userService.findById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(Booking.class, String.format("ID: %s", bookingId)));
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Указанный пользователь не является владельцем предмета или автором бронирования.");
        }
        return bookingMapper.bookingToResponseDto(booking);
    }

    private void checkBookingDate(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart()) ||
                bookingRequestDto.getEnd().equals(bookingRequestDto.getStart())) {
            throw new ValidationException("Дата окончания бронирования раньше даты начала.");
        }
    }

}
