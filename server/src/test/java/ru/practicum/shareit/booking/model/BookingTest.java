package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {
    private Booking testingBooking;

    @Test
    void testEquals_withNull_returnFalse() {
        Booking booking = null;
        testingBooking = new Booking();

        assertFalse(testingBooking.equals(booking));
    }

    @Test
    void testEquals_withNotUser_returnFalse() {
        testingBooking = new Booking();
        Object booking = new Object();

        assertFalse(testingBooking.equals(booking));
    }

    @Test
    void testEquals_withEqualUser_returnTrue() {
        testingBooking = Booking.builder().id(1L).build();
        Booking booking = Booking.builder().id(1L).start(LocalDateTime.now()).build();

        assertTrue(testingBooking.equals(booking));
    }

    @Test
    void testEquals_withNotEqualUser_returnFalse() {
        testingBooking = Booking.builder().id(1L).build();
        Booking booking = Booking.builder().id(2L).build();

        assertFalse(testingBooking.equals(booking));
    }

    @Test
    void testHashCode_whenInvoke_thenReturnNotNull() {
        testingBooking = Booking.builder().id(1L).build();
        int hash = testingBooking.hashCode();

        assertNotNull(hash);
    }
}