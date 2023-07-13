package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    @NotNull(message = "Необходимо указать бронируемую вещь")
    @Positive(message = "Указано отрицательное значение Id")
    private long itemId;
    @NotNull(message = "Дата начала бронирования указана некорректно")
    @FutureOrPresent(message = "Дата начала бронирования указана некорректно")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания бронирования указана некорректно")
    @Future(message = "Дата окончания бронирования указана некорректно")
    private LocalDateTime end;
}
