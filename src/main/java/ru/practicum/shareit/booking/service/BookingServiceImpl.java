package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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

    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

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
    public List<BookingResponseDto> getAllBookingsByState(Long userId,
                                                          String bookingState,
                                                          Integer from,
                                                          Integer size,
                                                          Boolean hasOwner) {
        userService.findById(userId);
        List<Booking> bookings;
        BookingState state;
        try {
            state = BookingState.valueOf(bookingState);
        } catch (Exception e) {
            throw new UnsupportedStatusException(bookingState);
        }

        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);

        if (hasOwner) {
            bookings = getAllBookingsByStateForOwner(userId, state, page);
        } else {
            bookings = getAllBookingsByStateWithoutOwner(userId, state, page);
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

    private List<Booking> getAllBookingsByStateForOwner(Long userId, BookingState state, Pageable page) {
        List<Booking> bookings;
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        page);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        page);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING,
                        page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED,
                        page);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, page);
                break;
        }
        return bookings;
    }

    private List<Booking> getAllBookingsByStateWithoutOwner(Long userId, BookingState state, Pageable page) {
        List<Booking> bookings;
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page);
                break;
            default:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
                break;
        }
        return bookings;
    }

    private void checkBookingDate(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart()) ||
                bookingRequestDto.getEnd().equals(bookingRequestDto.getStart())) {
            throw new ValidationException("Дата окончания бронирования раньше даты начала.");
        }
    }

}
