package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBooked;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIT {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private Long userId;
    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        UserDto userDto = UserDto.builder()
                .name("User")
                .email("user@mail.ru").build();
        userId = userService.create(userDto).getId();

        itemDto = ItemDto.builder()
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true).build();
    }

    @Test
    void create() {
        itemService.create(userId, itemDto);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemSaved = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertNotNull(itemSaved.getId());
        assertEquals(itemSaved.getName(), itemDto.getName());
        assertEquals(itemSaved.getDescription(), itemDto.getDescription());
        assertEquals(itemSaved.getAvailable(), itemDto.getAvailable());
        assertNotNull(itemSaved.getOwner());
    }

    @Test
    void update() {
        Long itemId = itemService.create(userId, itemDto).getId();
        ItemDto itemDtoUpdate = ItemDto.builder().available(false).build();

        ItemDto itemDtoSaved = itemService.update(userId, itemId, itemDtoUpdate);

        assertNotNull(itemDtoSaved.getId());
        assertEquals(itemDtoSaved.getName(), itemDto.getName());
        assertEquals(itemDtoSaved.getDescription(), itemDto.getDescription());
        assertEquals(itemDtoSaved.getAvailable(), itemDtoUpdate.getAvailable());
    }

    @Test
    void getByItemId() {
        Long itemId = itemService.create(userId, itemDto).getId();
        LocalDateTime start = LocalDateTime.now();
        UserDto userDtoBooker = UserDto.builder()
                .name("Booker")
                .email("booker@mail.ru").build();
        Long bookerId = userService.create(userDtoBooker).getId();
        BookingRequestDto bookingRequestDtoLast = BookingRequestDto.builder()
                .itemId(itemId)
                .start(start)
                .end(start.plusNanos(10L)).build();
        Long bookingIdLast = bookingService.create(bookerId, bookingRequestDtoLast).getId();
        BookingRequestDto bookingRequestDtoNext = BookingRequestDto.builder()
                .itemId(itemId)
                .start(start.plusHours(1L))
                .end(start.plusHours(2L)).build();
        Long bookingIdNext = bookingService.create(bookerId, bookingRequestDtoNext).getId();
        bookingService.bookingConfirmation(userId, bookingIdLast, true);
        bookingService.bookingConfirmation(userId, bookingIdNext, true);

        ItemBooked itemTarget = itemService.getByItemId(userId, itemId);


        assertNotNull(itemTarget.getId());
        assertEquals(itemTarget.getName(), itemDto.getName());
        assertEquals(itemTarget.getDescription(), itemDto.getDescription());
        assertEquals(itemTarget.getAvailable(), itemDto.getAvailable());

        assertNotNull(itemTarget.getLastBooking());
        assertEquals(itemTarget.getLastBooking().getId(), bookingIdLast);
        assertEquals(itemTarget.getNextBooking().getId(), bookingIdNext);
    }

    @Test
    void getAllItemsDyUserId() {
        Long itemId = itemService.create(userId, itemDto).getId();
        LocalDateTime start = LocalDateTime.now();
        UserDto userDtoBooker = UserDto.builder()
                .name("Booker")
                .email("booker@mail.ru").build();
        Long bookerId = userService.create(userDtoBooker).getId();
        BookingRequestDto bookingRequestDtoLast = BookingRequestDto.builder()
                .itemId(itemId)
                .start(start)
                .end(start.plusNanos(10L)).build();
        Long bookingIdLast = bookingService.create(bookerId, bookingRequestDtoLast).getId();
        BookingRequestDto bookingRequestDtoNext = BookingRequestDto.builder()
                .itemId(itemId)
                .start(start.plusHours(1L))
                .end(start.plusHours(2L)).build();
        Long bookingIdNext = bookingService.create(bookerId, bookingRequestDtoNext).getId();
        bookingService.bookingConfirmation(userId, bookingIdLast, true);
        bookingService.bookingConfirmation(userId, bookingIdNext, true);

        List<ItemBooked> itemsTarget = itemService.getAllItemsDyUserId(userId, 0, 5);
        assertNotNull(itemsTarget);
        assertFalse(itemsTarget.isEmpty());

        ItemBooked itemTarget = itemsTarget.get(0);

        assertNotNull(itemTarget.getId());
        assertEquals(itemTarget.getName(), itemDto.getName());
        assertEquals(itemTarget.getDescription(), itemDto.getDescription());
        assertEquals(itemTarget.getAvailable(), itemDto.getAvailable());

        assertNotNull(itemTarget.getLastBooking());
        assertEquals(itemTarget.getLastBooking().getId(), bookingIdLast);
        assertEquals(itemTarget.getNextBooking().getId(), bookingIdNext);
    }

    @Test
    void delete() {
        Long itemId = itemService.create(userId, itemDto).getId();
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemSaved = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertNotNull(itemSaved.getId());

        itemService.delete(userId, itemId);

        query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        List<Item> items = query.setParameter("name", itemDto.getName()).getResultList();

        assertTrue(items.isEmpty());
    }

    @Test
    void search() {
        ItemDto itemDto1 = ItemDto.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true).build();
        itemService.create(userId, itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Пила")
                .description("Простая")
                .available(true).build();
        itemService.create(userId, itemDto2);

        List<ItemDto> itemsExpected = List.of(itemDto1);

        List<ItemDto> targetItems = itemService.search(userId, "дрель", 0, 5);

        assertThat(targetItems, hasSize(itemsExpected.size()));
        for (ItemDto sourceRequest : itemsExpected) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceRequest.getName())),
                    hasProperty("description", equalTo(sourceRequest.getDescription()))
            )));
        }
    }

    @Test
    void createComment() {
        Long itemId = itemService.create(userId, itemDto).getId();
        UserDto userDtoBooker = UserDto.builder()
                .name("Booker")
                .email("booker@mail.ru").build();
        Long bookerId = userService.create(userDtoBooker).getId();
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusNanos(100)).build();
        Long bookingId = bookingService.create(bookerId, bookingRequestDto).getId();
        bookingService.bookingConfirmation(userId, bookingId, true);
        CommentDto commentDto = CommentDto.builder().text("text").build();
        itemService.createComment(bookerId, itemId, commentDto);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment commentSaved = query.setParameter("text", commentDto.getText()).getSingleResult();

        assertNotNull(commentSaved.getId());
        assertEquals(commentSaved.getText(), commentDto.getText());
        assertEquals(commentSaved.getAuthor().getName(), userDtoBooker.getName());
        assertNotNull(commentSaved.getItem());
    }
}