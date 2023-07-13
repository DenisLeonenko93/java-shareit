package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    private Comment testingComment;

    @Test
    void testEquals_withNull_returnFalse() {
        Comment comment = null;
        testingComment = new Comment();

        assertFalse(testingComment.equals(comment));
    }

    @Test
    void testEquals_withNotUser_returnFalse() {
        testingComment = new Comment();
        Object comment = new Object();

        assertFalse(testingComment.equals(comment));
    }

    @Test
    void testEquals_withEqualUser_returnTrue() {
        testingComment = Comment.builder().id(1L).build();
        Comment comment = Comment.builder().id(1L).created(Instant.now()).build();

        assertTrue(testingComment.equals(comment));
    }

    @Test
    void testEquals_withNotEqualUser_returnFalse() {
        testingComment = Comment.builder().id(1L).build();
        Comment comment = Comment.builder().id(2L).build();

        assertFalse(testingComment.equals(comment));
    }

    @Test
    void testHashCode_whenInvoke_thenReturnNotNull() {
        testingComment = Comment.builder().id(1L).build();
        int hash = testingComment.hashCode();

        assertNotNull(hash);
    }
}