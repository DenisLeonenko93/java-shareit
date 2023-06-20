package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;

    @NotNull(message = "Необходимо поле text.")
    @NotBlank(message = "Комментарий не может быть пустым.")
    private String text;

    private String authorName;
    private LocalDateTime created;
}
