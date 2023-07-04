package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;
    @Captor
    private ArgumentCaptor<BookingStatus> bookingStatusArgumentCaptor;
    private Long userId;
    private Long bookingId;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;

    private User user;
    private Item item;


    @BeforeEach
    void beforeEach() {
        userId = 0L;
        bookingId = 0L;
        booking = Booking.builder().build();
        bookingRequestDto = BookingRequestDto.builder()
                .end(LocalDateTime.MAX)
                .start(LocalDateTime.MIN).build();
        bookingResponseDto = BookingResponseDto.builder().build();
        user = User.builder().id(1L).build();
        item = Item.builder().owner(user).available(true).build();
    }

    @Test
    void create_whenInvoke_thenReturnBookingResponseDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(bookingMapper.bookingFromRequestDto(bookingRequestDto)).thenReturn(booking);
        when(bookingMapper.bookingToResponseDto(any())).thenReturn(bookingResponseDto);

        BookingResponseDto actualBooking = bookingService.create(userId, bookingRequestDto);

        assertNotNull(actualBooking);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());

        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertNotNull(savedBooking.getBooker());
        assertNotNull(savedBooking.getItem());
        assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
    }

    @Test
    void create_whenStartAfterEnd_thenValidationExceptionThrow() {
        bookingRequestDto.setStart(LocalDateTime.MAX);
        bookingRequestDto.setEnd(LocalDateTime.MIN);

        assertThrows(ValidationException.class,
                () -> bookingService.create(userId, bookingRequestDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenItemNotAvailable_thenValidationExceptionThrow() {
        item.setAvailable(false);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));

        assertThrows(ValidationException.class,
                () -> bookingService.create(userId, bookingRequestDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenUserIsOwner_thenEntityNotFoundExceptionThrow() {
        user.setId(0L);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(userId, bookingRequestDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenUserNotFound_thenEntityNotFoundExceptionThrow() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(userId, bookingRequestDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenItemNotFound_thenEntityNotFoundExceptionThrow() {
        user.setId(0L);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(userId, bookingRequestDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void bookingConfirmation_whenInvokeWithApprovedTrue_thenReturnBookingResponseDtoWithStatusAPPROVED() {
        user.setId(0L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.bookingToResponseDto(any())).thenReturn(bookingResponseDto);

        BookingResponseDto actualBooking = bookingService.bookingConfirmation(userId, bookingId, true);

        assertNotNull(actualBooking);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());

        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(BookingStatus.APPROVED, savedBooking.getStatus());
    }

    @Test
    void bookingConfirmation_whenInvokeWithApprovedFalse_thenReturnBookingResponseDtoWithStatusREJECTED() {
        user.setId(0L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.bookingToResponseDto(any())).thenReturn(bookingResponseDto);

        BookingResponseDto actualBooking = bookingService.bookingConfirmation(userId, bookingId, false);

        assertNotNull(actualBooking);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());

        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(BookingStatus.REJECTED, savedBooking.getStatus());
    }

    @Test
    void bookingConfirmation_whenBookingNotFound_thenEntityNotFoundExceptionThrow() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.bookingConfirmation(userId, bookingId, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void bookingConfirmation_whenUserNotOwner_thenEntityNotFoundExceptionThrow() {
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.bookingConfirmation(userId, bookingId, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void bookingConfirmation_whenBookingStatusApproved_thenValidationExceptionThrow() {
        user.setId(0L);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.bookingConfirmation(userId, bookingId, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getAllBookingsByState_withWrongState_thenUnsupportedStatusExceptionThrow() {
        String state = "wrong";

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllBookingsByState(userId, state, 1, 1, true));
    }

    @Test
    void getAllBookingsByState_withOwnerAndCURRENTState_thenInvokeFindByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        String state = "CURRENT";

        bookingService.getAllBookingsByState(userId, state, 1, 1, true);
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                        any(),
                        any(),
                        any());
    }

    @Test
    void getAllBookingsByState_withOwnerAndFUTUREState_thenInvokeFindByItemOwnerIdAndStartAfterOrderByStartDesc() {
        String state = "FUTURE";

        bookingService.getAllBookingsByState(userId, state, 1, 1, true);
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getAllBookingsByState_withOwnerAndPASTState_thenInvokeFindByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        String state = "PAST";

        bookingService.getAllBookingsByState(userId, state, 1, 1, true);
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getAllBookingsByState_withOwnerAndWAITINGState_thenInvokeFindByItemOwnerIdAndStatusOrderByStartDescWithWaitingInParams() {
        String state = "WAITING";

        bookingService.getAllBookingsByState(userId, state, 1, 1, true);
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
                        bookingStatusArgumentCaptor.capture(),
                        any());

        BookingStatus status = bookingStatusArgumentCaptor.getValue();
        assertEquals(BookingStatus.WAITING, status);
    }

    @Test
    void getAllBookingsByState_withOwnerAndREJECTEDState_thenInvokeFindByItemOwnerIdAndStatusOrderByStartDescWithRejectedInParams() {
        String state = "REJECTED";

        bookingService.getAllBookingsByState(userId, state, 1, 1, true);
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
                        bookingStatusArgumentCaptor.capture(),
                        any());

        BookingStatus status = bookingStatusArgumentCaptor.getValue();
        assertEquals(BookingStatus.REJECTED, status);
    }

    @Test
    void getAllBookingsByState_withOwnerAndAllState_thenInvokeFindByItemOwnerIdOrderByStartDesc() {
        String state = "ALL";

        bookingService.getAllBookingsByState(userId, state, 1, 1, true);
        verify(bookingRepository, times(1))
                .findByItemOwnerIdOrderByStartDesc(anyLong(),
                        any());
    }

    @Test
    void getAllBookingsByState_withNotOwnerAndCURRENTState_thenInvokeFindByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc() {
        String state = "CURRENT";

        bookingService.getAllBookingsByState(userId, state, 1, 1, false);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(),
                        any(),
                        any(),
                        any());
    }

    @Test
    void getAllBookingsByState_withNotOwnerAndFUTUREState_thenInvokeFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        String state = "FUTURE";

        bookingService.getAllBookingsByState(userId, state, 1, 1, false);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getAllBookingsByState_withNotOwnerAndPASTState_thenInvokeFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        String state = "PAST";

        bookingService.getAllBookingsByState(userId, state, 1, 1, false);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getAllBookingsByState_withNotOwnerAndWAITINGState_thenInvokeFindAllByBookerIdAndStatusOrderByStartDescWithWaitingInParams() {
        String state = "WAITING";

        bookingService.getAllBookingsByState(userId, state, 1, 1, false);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                        bookingStatusArgumentCaptor.capture(),
                        any());

        BookingStatus status = bookingStatusArgumentCaptor.getValue();
        assertEquals(BookingStatus.WAITING, status);
    }

    @Test
    void getAllBookingsByState_withNotOwnerAndREJECTEDState_thenInvokeFindAllByBookerIdAndStatusOrderByStartDescWithRejectedInParams() {
        String state = "REJECTED";

        bookingService.getAllBookingsByState(userId, state, 1, 1, false);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                        bookingStatusArgumentCaptor.capture(),
                        any());

        BookingStatus status = bookingStatusArgumentCaptor.getValue();
        assertEquals(BookingStatus.REJECTED, status);
    }

    @Test
    void getAllBookingsByState_withNotOwnerAndAllState_thenInvokeFindAllByBookerIdOrderByStartDesc() {
        String state = "ALL";

        bookingService.getAllBookingsByState(userId, state, 1, 1, false);
        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDesc(anyLong(),
                        any());
    }

    @Test
    void getBookingById_whenInvoke_thenReturnBookingResponseDto() {
        User booker = User.builder().id(0L).build();
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.bookingToResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto actualBooking = bookingService.getBookingById(userId, bookingId);

        assertNotNull(actualBooking);
        assertEquals(bookingResponseDto, actualBooking);
    }

    @Test
    void getBookingById_whenBookingNotFound_thenEntityNotFoundExceptionThrow() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(userId, bookingId));
    }

    @Test
    void getBookingById_whenUserNotBooker_thenReturnBookingResponseDto() {
        User owner = User.builder().id(0L).build();
        item.setOwner(owner);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.bookingToResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto actualBooking = bookingService.getBookingById(userId, bookingId);

        assertNotNull(actualBooking);
        assertEquals(bookingResponseDto, actualBooking);
    }

    @Test
    void getBookingById_whenUserNotOwner_thenReturnBookingResponseDto() {
        User booker = User.builder().id(0L).build();
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.bookingToResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto actualBooking = bookingService.getBookingById(userId, bookingId);

        assertNotNull(actualBooking);
        assertEquals(bookingResponseDto, actualBooking);
    }

    @Test
    void getBookingById_whenUserNotOwnerAndNotBooker_thenEntityNotFoundExceptionThrow() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        item.setOwner(owner);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(userId, bookingId));
    }
}