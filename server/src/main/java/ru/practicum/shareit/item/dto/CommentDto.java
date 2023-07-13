package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private Instant created;
}
