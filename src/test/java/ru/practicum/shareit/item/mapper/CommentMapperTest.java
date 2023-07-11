package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentMapperTest {

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void commentFromDto() {
        CommentDto commentDto = CommentDto.builder()
                .id(0L)
                .text("description")
                .created(LocalDateTime.MIN).build();

        Comment comment = mapper.commentFromDto(commentDto);

        assertEquals(0L, comment.getId());
        assertEquals("description", comment.getText());
        assertEquals(LocalDateTime.MIN, comment.getCreated());
        assertNull(comment.getItem());
        assertNull(comment.getAuthor());
    }

    @Test
    void commentToDto() {
        Comment comment = Comment.builder()
                .id(0L)
                .text("description")
                .author(User.builder().name("Author").build())
                .created(LocalDateTime.MIN).build();

        CommentDto commentDto = mapper.commentToDto(comment);


        assertEquals(0L, commentDto.getId());
        assertEquals("description", commentDto.getText());
        assertEquals(LocalDateTime.MIN, commentDto.getCreated());
        assertEquals("Author", commentDto.getAuthorName());
    }
}