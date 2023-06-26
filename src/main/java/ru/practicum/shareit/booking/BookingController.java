package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.LogExecution;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @LogExecution
    public ResponseEntity<BookingResponseDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.create(userId, bookingRequestDto));
    }

    @PatchMapping("/{bookingId}")
    @LogExecution(withArgs = true)
    public ResponseEntity<BookingResponseDto> bookingConfirmation(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @PathVariable Long bookingId,
                                                                  @RequestParam Boolean approved) {
        return ResponseEntity.ok(bookingService.bookingConfirmation(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    @LogExecution(withArgs = true)
    public ResponseEntity<BookingResponseDto> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    @LogExecution(withArgs = true)
    public ResponseEntity<List<BookingResponseDto>> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                   @RequestParam(defaultValue = "ALL", required = false) String state) {
        return ResponseEntity.ok(bookingService.findAllBookingsByState(userId, state));
    }

    @GetMapping("/owner")
    @LogExecution(withArgs = true)
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsByItemOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                              @RequestParam(defaultValue = "ALL", required = false) String state) {
        return ResponseEntity.ok(bookingService.findAllBookingsByItemOwner(userId, state));
    }
}
