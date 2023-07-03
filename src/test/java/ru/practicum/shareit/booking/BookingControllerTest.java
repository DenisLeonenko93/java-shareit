package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void create_whenInvoked_thenResponseStatusCreateWithBookingResponseDtoInBody() {
        Long userId = 0L;
        BookingRequestDto requestDto = BookingRequestDto.builder().build();
        BookingResponseDto expectedDto = BookingResponseDto.builder().build();
        when(bookingService.create(anyLong(), any())).thenReturn(expectedDto);

        ResponseEntity<BookingResponseDto> response = bookingController.create(userId, requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void bookingConfirmation_whenInvoked_thenResponseStatusOkWithBookingResponseDtoInBody() {
        Long userId = 0L;
        Long bookingId = 0L;
        BookingResponseDto expectedDto = BookingResponseDto.builder().build();
        when(bookingService.bookingConfirmation(anyLong(), anyLong(), anyBoolean())).thenReturn(expectedDto);

        ResponseEntity<BookingResponseDto> response = bookingController.bookingConfirmation(userId, bookingId, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void getBookingById_whenInvoked_thenResponseStatusOkWithBookingResponseDtoInBody() {
        Long userId = 0L;
        Long bookingId = 0L;
        BookingResponseDto expectedDto = BookingResponseDto.builder().build();
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(expectedDto);

        ResponseEntity<BookingResponseDto> response = bookingController.getBookingById(userId, bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void getAllBookings_whenInvoked_thenResponseStatusOkWithListBookingResponseDtoInBody() {
        Long userId = 0L;
        String state = "All";
        Integer from = 1;
        Integer size = 5;
        BookingResponseDto expectedDto = BookingResponseDto.builder().build();
        List<BookingResponseDto> expectedCollection = List.of(expectedDto);
        when(bookingService.getAllBookingsByState(userId, state, from, size, false)) //значение false указано для параметра hasOwner. Два метода имеют схожий функционал, отличаются наличием хозяина вещи.
                .thenReturn(expectedCollection);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingController.getAllBookings(userId, state, from, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getAllBookingsByItemOwner_whenInvoked_thenResponseStatusOkWithListBookingResponseDtoInBody() {
        Long userId = 0L;
        String state = "All";
        Integer from = 1;
        Integer size = 5;
        BookingResponseDto expectedDto = BookingResponseDto.builder().build();
        List<BookingResponseDto> expectedCollection = List.of(expectedDto);
        when(bookingService.getAllBookingsByState(userId, state, from, size, true)) //значение false указано для параметра hasOwner. Два метода имеют схожий функционал, отличаются наличием хозяина вещи.
                .thenReturn(expectedCollection);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingController.getAllBookingsByItemOwner(userId, state, from, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
}