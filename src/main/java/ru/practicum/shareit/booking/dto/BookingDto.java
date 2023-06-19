package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDto {
    private Long id;
    @Positive(message = "Указано отрицательное значение Id")
    private Long itemId;

    @NotNull(message = "Дата указана некорректно")
    @FutureOrPresent(message = "Дата указана некорректно")
    private LocalDateTime start;

    @NotNull(message = "Дата указана некорректно")
    @Future(message = "Дата указана некорректно")
    private LocalDateTime end;
}
