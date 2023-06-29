package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoForItemResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemForBookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserForBookingResponseDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "id", source = "bookingRequestDto.id")
    Booking bookingFromRequestDto(BookingRequestDto bookingRequestDto);

    BookingResponseDto bookingToResponseDto(Booking booking);

    UserForBookingResponseDto userToDtoForBookingResponseDto(User user);

    ItemForBookingResponseDto itemToDtoForBookingResponseDto(Item item);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingDtoForItemResponseDto bookingForItemResponseDto(Booking booking);
}
