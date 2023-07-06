package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIT {
    private final EntityManager em;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private Long userId;
    private Long itemId;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void beforeEach() {
        UserDto userDto = UserDto.builder()
                .name("User")
                .email("user@mail.ru").build();
        userId = userService.create(userDto).getId();

        ItemDto itemDto = ItemDto.builder()
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true).build();
        itemId = itemService.create(userId, itemDto).getId();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1L)).build();
    }

    @Test
    void create() {
        UserDto userDtoBooker = UserDto.builder()
                .name("Booker")
                .email("booker@mail.ru").build();
        Long bookerId = userService.create(userDtoBooker).getId();
        Long bookingId = bookingService.create(bookerId, bookingRequestDto).getId();
        TypedQuery<Booking> query = em.createQuery(
                "Select b from Booking b where b.id = :id",
                Booking.class);
        Booking bookingSaved = query.setParameter("id", bookingId).getSingleResult();

        assertNotNull(bookingSaved);
        assertNotNull(bookingSaved.getItem());
        assertEquals(itemId, bookingSaved.getItem().getId());
        assertNotNull(bookingSaved.getBooker());
        assertEquals(bookerId, bookingSaved.getBooker().getId());
        assertEquals(BookingStatus.WAITING, bookingSaved.getStatus());
    }

    @Test
    void bookingConfirmation() {
        UserDto userDtoBooker = UserDto.builder()
                .name("Booker")
                .email("booker@mail.ru").build();
        Long bookerId = userService.create(userDtoBooker).getId();
        Long bookingId = bookingService.create(bookerId, bookingRequestDto).getId();

        BookingResponseDto bookingApproved = bookingService.bookingConfirmation(userId, bookingId, true);

        assertNotNull(bookingApproved);
        assertNotNull(bookingApproved.getItem());
        assertEquals(itemId, bookingApproved.getItem().getId());
        assertNotNull(bookingApproved.getBooker());
        assertEquals(bookerId, bookingApproved.getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookingApproved.getStatus());
    }

    @Test
    void getAllBookingsByState_whenInvokeBooker_thenReturnListBookingResponseDto() {
        List<User> users = new ArrayList<>();
        fillTheUserRepository(users);
        Long bookerId = users.get(1).getId();
        List<Item> items = new ArrayList<>();
        fillTheItemRepository(users, items);
        List<Booking> bookings = new ArrayList<>();
        fillTheBookingRepository(users, items, bookings);

        List<BookingResponseDto> resultBookings =
                bookingService.getAllBookingsByState(bookerId,
                        "ALL",
                        0,
                        10,
                        false);
        assertThat(resultBookings, hasSize(5));

        resultBookings = bookingService.getAllBookingsByState(bookerId,
                "CURRENT",
                0,
                10,
                false);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(0).getId()));

        resultBookings = bookingService.getAllBookingsByState(bookerId,
                "FUTURE",
                0,
                10,
                false);
        assertThat(resultBookings, hasSize(3));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(4).getId()));
        assertThat(resultBookings.get(1).getId(), equalTo(bookings.get(3).getId()));
        assertThat(resultBookings.get(2).getId(), equalTo(bookings.get(2).getId()));

        resultBookings = bookingService.getAllBookingsByState(bookerId,
                "PAST",
                0,
                10,
                false);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(1).getId()));

        resultBookings = bookingService.getAllBookingsByState(bookerId,
                "WAITING",
                0,
                10,
                false);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(4).getId()));

        resultBookings = bookingService.getAllBookingsByState(bookerId,
                "REJECTED",
                0,
                10,
                false);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(3).getId()));
    }

    @Test
    void getAllBookingsByState_whenInvokeItemOwner_thenReturnListBookingResponseDto() {
        List<User> users = new ArrayList<>();
        fillTheUserRepository(users);
        Long ownerId = users.get(0).getId();
        List<Item> items = new ArrayList<>();
        fillTheItemRepository(users, items);
        List<Booking> bookings = new ArrayList<>();
        fillTheBookingRepository(users, items, bookings);

        List<BookingResponseDto> resultBookings =
                bookingService.getAllBookingsByState(ownerId,
                        "ALL",
                        0,
                        10,
                        true);
        assertThat(resultBookings, hasSize(5));

        resultBookings = bookingService.getAllBookingsByState(ownerId,
                "CURRENT",
                0,
                10,
                true);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(0).getId()));

        resultBookings = bookingService.getAllBookingsByState(ownerId,
                "FUTURE",
                0,
                10,
                true);
        assertThat(resultBookings, hasSize(3));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(4).getId()));
        assertThat(resultBookings.get(1).getId(), equalTo(bookings.get(3).getId()));
        assertThat(resultBookings.get(2).getId(), equalTo(bookings.get(2).getId()));

        resultBookings = bookingService.getAllBookingsByState(ownerId,
                "PAST",
                0,
                10,
                true);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(1).getId()));

        resultBookings = bookingService.getAllBookingsByState(ownerId,
                "WAITING",
                0,
                10,
                true);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(4).getId()));

        resultBookings = bookingService.getAllBookingsByState(ownerId,
                "REJECTED",
                0,
                10,
                true);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(3).getId()));
    }

    @Test
    void getBookingById() {
        UserDto userDtoBooker = UserDto.builder()
                .name("Booker")
                .email("booker@mail.ru").build();
        Long bookerId = userService.create(userDtoBooker).getId();
        Long bookingId = bookingService.create(bookerId, bookingRequestDto).getId();

        BookingResponseDto booking = bookingService.getBookingById(bookerId, bookingId);

        assertNotNull(booking);
        assertNotNull(booking.getItem());
        assertEquals(itemId, booking.getItem().getId());
        assertNotNull(booking.getBooker());
        assertEquals(bookerId, booking.getBooker().getId());
    }

    private void fillTheUserRepository(List<User> users) {
        User user2 = User.builder()
                .name("User1")
                .email("user1@mail.ru").build();
        User user3 = User.builder()
                .name("User2")
                .email("user2@mail.ru").build();
        User user4 = User.builder()
                .name("User3")
                .email("user3@mail.ru").build();
        users.addAll(List.of(user2, user3, user4));
        for (User user : users) {
            user = userRepository.saveAndFlush(user);
        }
    }

    private void fillTheItemRepository(List<User> users, List<Item> items) {
        Item item2 = Item.builder()
                .name("Отвертка")
                .description("Description")
                .available(true)
                .owner(users.get(0)).build();
        Item item3 = Item.builder()
                .name("Дрель")
                .description("Description")
                .available(true)
                .owner(users.get(0)).build();
        Item item4 = Item.builder()
                .name("Пила")
                .description("Description")
                .available(true)
                .owner(users.get(0)).build();
        Item item5 = Item.builder()
                .name("Шуруповерт")
                .description("Description")
                .available(true)
                .owner(users.get(1)).build();
        items.addAll(List.of(item2, item3, item4, item5));
        for (Item item : items) {
            item = itemRepository.saveAndFlush(item);
        }
    }

    private void fillTheBookingRepository(List<User> users,
                                          List<Item> items,
                                          List<Booking> bookings) {
        LocalDateTime start = LocalDateTime.now();
        Booking booking2 = Booking.builder()
                .start(start.minusHours(1))
                .end(start.plusHours(1))
                .item(items.get(0))
                .booker(users.get(1))
                .status(BookingStatus.APPROVED).build();
        Booking booking3 = Booking.builder()
                .start(start.minusHours(2))
                .end(start.minusHours(1))
                .item(items.get(0))
                .booker(users.get(1))
                .status(BookingStatus.APPROVED).build();
        Booking booking4 = Booking.builder()
                .start(start.plusHours(1))
                .end(start.plusHours(2))
                .item(items.get(1))
                .booker(users.get(1))
                .status(BookingStatus.APPROVED).build();
        Booking booking5 = Booking.builder()
                .start(start.plusHours(2))
                .end(start.plusHours(3))
                .item(items.get(2))
                .booker(users.get(1))
                .status(BookingStatus.REJECTED).build();
        Booking booking6 = Booking.builder()
                .start(start.plusHours(3))
                .end(start.plusHours(4))
                .item(items.get(2))
                .booker(users.get(1))
                .status(BookingStatus.WAITING).build();
        Booking booking7 = Booking.builder()
                .start(start.plusHours(1))
                .end(start.plusHours(2))
                .item(items.get(3))
                .booker(users.get(0))
                .status(BookingStatus.WAITING).build();
        bookings.addAll(List.of(booking2, booking3, booking4, booking5, booking6, booking7));

        for (Booking booking : bookings) {
            booking = bookingRepository.saveAndFlush(booking);
        }
    }

}