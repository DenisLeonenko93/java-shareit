package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        User booker = UserMapper.fromUserDto(userService.findById(userId));
        Item item = ItemMapper.fromDto(itemService.findOne(bookingDto.getItemId()));
        Booking booking = BookingMapper.fromDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }
}
