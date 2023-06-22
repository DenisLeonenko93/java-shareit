package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItemResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
@Component
public class BookingMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;


    public Booking fromRequestDto(Long userId, BookingDto bookingDto) {
        if (bookingDto == null) return null;
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", userId)));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(Item.class, String.format("ID: %s", bookingDto.getItemId())));
        Booking booking = fromDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);

        return booking;
    }

    public Booking fromDto(BookingDto bookingDto) {
        if (bookingDto == null) return null;
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public BookingDto toDto(Booking booking) {
        if (booking == null) return null;
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .build();
    }

    public BookingResponseDto toResponseDto(Booking booking) {
        if (booking == null) return null;
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(itemMapper.itemToDtoForBookingResponseDto(booking.getItem()))
                .booker(userMapper.userToDtoForBookingResponseDto(booking.getBooker()))
                .build();
    }

    public BookingDtoForItemResponseDto forItemResponseDto(Booking booking) {
        if (booking == null) return null;
        return BookingDtoForItemResponseDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
