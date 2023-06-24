package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    private Long id;
    @Positive(message = "Указано отрицательное значение Id")
    private Long itemId;

    @NotNull(message = "Дата начала бронирования указана некорректно")
    @FutureOrPresent(message = "Дата начала бронирования указана некорректно")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования указана некорректно")
    @Future(message = "Дата окончания бронирования указана некорректно")
    private LocalDateTime end;
}
