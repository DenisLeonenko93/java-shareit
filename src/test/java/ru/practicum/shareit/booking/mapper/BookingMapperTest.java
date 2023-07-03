package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDtoForItemResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemForBookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserForBookingResponseDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {

    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void bookingFromRequestDto() {
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .id(0L)
                .itemId(0L)
                .start(LocalDateTime.MIN)
                .end(LocalDateTime.MAX).build();

        Booking actualBooking = mapper.bookingFromRequestDto(bookingRequestDto);

        assertEquals(0L, actualBooking.getId());
        assertEquals(LocalDateTime.MIN, actualBooking.getStart());
        assertEquals(LocalDateTime.MAX, actualBooking.getEnd());
        assertNull(actualBooking.getItem());
        assertNull(actualBooking.getBooker());
        assertNull(actualBooking.getStatus());
    }

    @Test
    void bookingToResponseDto() {
        Booking booking = Booking.builder()
                .id(0L)
                .item(Item.builder().id(1L).build())
                .booker(User.builder().id(1L).build())
                .start(LocalDateTime.MIN)
                .end(LocalDateTime.MAX).build();

        BookingResponseDto actualBooking = mapper.bookingToResponseDto(booking);

        assertEquals(0L, actualBooking.getId());
        assertEquals(LocalDateTime.MIN, actualBooking.getStart());
        assertEquals(LocalDateTime.MAX, actualBooking.getEnd());
        assertEquals(1L, actualBooking.getItem().getId());
        assertEquals(1L, actualBooking.getBooker().getId());
        assertNull(actualBooking.getStatus());
    }

    @Test
    void bookingForItemResponseDto() {
        Booking booking = Booking.builder()
                .id(0L)
                .item(Item.builder().id(1L).build())
                .booker(User.builder().id(1L).build())
                .start(LocalDateTime.MIN)
                .end(LocalDateTime.MAX).build();

        BookingDtoForItemResponseDto actualBooking = mapper.bookingForItemResponseDto(booking);

        assertEquals(0L, actualBooking.getId());
        assertEquals(LocalDateTime.MIN, actualBooking.getStart());
        assertEquals(LocalDateTime.MAX, actualBooking.getEnd());
        assertEquals(1L, actualBooking.getBookerId());
    }

    @Test
    void userToDtoForBookingResponseDto() {
        User user = User.builder()
                .id(1L)
                .email("name@name.ru")
                .name("Name").build();

        UserForBookingResponseDto actualUser = mapper.userToDtoForBookingResponseDto(user);

        assertEquals(1L, actualUser.getId());
    }

    @Test
    void itemToDtoForBookingResponseDto() {
        Item item = Item.builder()
                .id(0L)
                .name("Name")
                .description("desc")
                .available(false)
                .owner(new User())
                .request(ItemRequest.builder().id(0L).build())
                .build();

        ItemForBookingResponseDto actualItem = mapper.itemToDtoForBookingResponseDto(item);

        assertEquals(0L, actualItem.getId());
        assertEquals("Name", actualItem.getName());
    }
}