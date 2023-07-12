package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User testingUser;
    @Test
    void testEquals_withNull_returnFalse() {
        User user = null;
        testingUser = new User();

        assertNotEquals(testingUser, user);
    }

    @Test
    void testEquals_withNotUser_returnFalse() {
        testingUser = new User();
        Object user = new Object();

        assertNotEquals(testingUser, user);
    }

    @Test
    void testEquals_withEqualUser_returnTrue() {
        testingUser = User.builder().id(1L).build();
        User user = User.builder().id(1L).name("Test").build();

        assertEquals(testingUser, user);
    }

    @Test
    void testEquals_withNotEqualUser_returnFalse() {
        testingUser = User.builder().id(1L).build();
        User user = User.builder().id(2L).name("Test").build();

        assertNotEquals(testingUser, user);
    }

    @Test
    void testHashCode_whenInvoke_thenReturnNotNull() {
        testingUser = User.builder().id(1L).build();
        int hash = testingUser.hashCode();
        assertNotNull(hash);
    }
}