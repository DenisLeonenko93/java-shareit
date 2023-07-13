package ru.practicum.shareit.itemRequest.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    private ItemRequest testingItemRequest;

    @Test
    void testEquals_withNull_returnFalse() {
        ItemRequest itemRequest = null;
        testingItemRequest = new ItemRequest();

        assertFalse(testingItemRequest.equals(itemRequest));
    }

    @Test
    void testEquals_withNotUser_returnFalse() {
        testingItemRequest = new ItemRequest();
        Object itemRequest = new Object();

        assertFalse(testingItemRequest.equals(itemRequest));
    }

    @Test
    void testEquals_withEqualUser_returnTrue() {
        testingItemRequest = ItemRequest.builder().id(1L).build();
        ItemRequest itemRequest = ItemRequest.builder().id(1L).created(Instant.now()).build();

        assertTrue(testingItemRequest.equals(itemRequest));
    }

    @Test
    void testEquals_withNotEqualUser_returnFalse() {
        testingItemRequest = ItemRequest.builder().id(1L).build();
        ItemRequest itemRequest = ItemRequest.builder().id(2L).build();

        assertFalse(testingItemRequest.equals(itemRequest));
    }

    @Test
    void testHashCode_whenInvoke_thenReturnNotNull() {
        testingItemRequest = ItemRequest.builder().id(1L).build();
        int hash = testingItemRequest.hashCode();

        assertNotNull(hash);
    }
}