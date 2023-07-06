package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerIT {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private static Long userId;

    @BeforeAll
    static void beforeAll() {
        userId = 0L;
    }

    @SneakyThrows
    @Test
    void create_whenInvoke_thenReturnStatusOk() {
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(2L)).build();
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(2L)).build();
        when(bookingService.create(userId, bookingRequestDto)).thenReturn(bookingResponseDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @SneakyThrows
    @Test
    void create_whenUserNotFound_thenReturnStatusNotFound() {
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(2L)).build();
        when(bookingService.create(userId, bookingRequestDto)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void create_whenNotValidBody_thenReturnStatusBadRequest() {
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusHours(1L))
                .end(LocalDateTime.now().plusHours(2L)).build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(userId, bookingRequestDto);
    }

    @SneakyThrows
    @Test
    void bookingConfirmation_whenInvoke_thenReturnStatusOkAndBookingResponseDtoInBody() {
        Long bookingId = 0L;
        Boolean approved = true;
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(2L)).build();
        when(bookingService.bookingConfirmation(userId, bookingId, approved)).thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void bookingConfirmation_whenDataNotFound_thenReturnStatusNotFound() {
        Long bookingId = 0L;
        Boolean approved = true;
        when(bookingService.bookingConfirmation(userId, bookingId, approved))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getBookingById_whenInvoke_thenReturnStatusOK() {
        Long bookingId = 0L;
        BookingResponseDto responseDto = BookingResponseDto.builder().build();
        when(bookingService.getBookingById(userId, bookingId)).thenReturn(responseDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseDto), result);

    }

    @SneakyThrows
    @Test
    void getBookingById_whenDataNotFound_thenReturnStatusNotFound() {
        Long bookingId = 0L;
        when(bookingService.getBookingById(userId, bookingId))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getBookingById_whenDuplicateBookingStatus_thenReturnStatusBadRequest() {
        Long bookingId = 0L;
        when(bookingService.getBookingById(userId, bookingId))
                .thenThrow(ValidationException.class);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllBookings_whenInvoke_thenReturnStatusOK() {
        String state = "ALL";
        Integer from = 1;
        Integer size = 1;
        List<BookingResponseDto> responseDtoList = Collections.emptyList();
        when(bookingService.getAllBookingsByState(userId, state, from, size, true))
                .thenReturn(responseDtoList);

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseDtoList), result);
    }

    @SneakyThrows
    @Test
    void getAllBookings_whenNotValidParams_thenReturnStatusBadRequest() {
        String state = "ALL";
        Integer from = -1;
        Integer size = -1;
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllBookingsByItemOwner_whenInvoke_thenReturnStatusOK() {
        String state = "ALL";
        Integer from = 1;
        Integer size = 1;
        List<BookingResponseDto> responseDtoList = Collections.emptyList();
        when(bookingService.getAllBookingsByState(userId, state, from, size, true))
                .thenReturn(responseDtoList);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseDtoList), result);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByItemOwner_whenNotValidParams_thenReturnStatusBadRequest() {
        String state = "ALL";
        Integer from = -1;
        Integer size = -1;
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());
    }
}