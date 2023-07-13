package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBooked;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private Long userId;
    private Long itemId;

    private ItemDto requestDto;
    private ItemDto expectedItem;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
        itemId = 0L;
        requestDto = ItemDto.builder().build();
        expectedItem = ItemDto.builder().id(0L).build();
    }

    @Test
    void create_whenInvoked_thenResponseStatusCreateWithItemDtoInBody() {
        when(itemService.create(userId, requestDto)).thenReturn(expectedItem);

        ResponseEntity<ItemDto> response = itemController.create(userId, requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedItem, response.getBody());
    }

    @Test
    void update_whenInvoked_thenResponseStatusOKWithItemDtoInBody() {
        when(itemService.update(userId, itemId, requestDto)).thenReturn(expectedItem);

        ResponseEntity<ItemDto> response = itemController.update(userId, itemId, requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItem, response.getBody());
    }

    @Test
    void getByItemId_whenInvoked_thenResponseStatusOkWithItemBookedInBody() {
        ItemBooked expectedItem = ItemBooked.builder().build();
        when(itemService.getByItemId(userId, itemId)).thenReturn(expectedItem);

        ResponseEntity<ItemBooked> response = itemController.getByItemId(userId, itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItem, response.getBody());
    }

    @Test
    void getAllItemsByUserId_whenInvoked_thenResponseStatusOkWithListItemBookedInBody() {
        Integer from = 1;
        Integer size = 1;
        ItemBooked expectedItem = ItemBooked.builder().build();
        List<ItemBooked> expectedCollection = List.of(expectedItem);
        when(itemService.getAllItemsDyUserId(userId, from, size)).thenReturn(expectedCollection);

        ResponseEntity<List<ItemBooked>> response = itemController.getAllItemsByUserId(userId, from, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void delete_whenInvoked_thenResponseStatusNoContentWithEmptyBodyMustInvokeItemServiceDeleteMethod() {
        ResponseEntity<Void> response = itemController.delete(userId, itemId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(itemService, times(1)).delete(userId, itemId);
    }

    @Test
    void search_whenInvoked_thenResponseStatusOkWithListItemBookedInBody() {
        Integer from = 1;
        Integer size = 1;
        String text = " ";
        List<ItemDto> expectedCollection = List.of(expectedItem);
        when(itemService.search(userId, text, from, size)).thenReturn(expectedCollection);

        ResponseEntity<List<ItemDto>> response = itemController.search(userId, text, from, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void createComment_whenInvoked_thenResponseStatusCreatedWithCommentDtoInBody() {
        CommentDto requestComment = CommentDto.builder().build();
        CommentDto expectedComment = CommentDto.builder().id(0L).build();
        when(itemService.createComment(userId, itemId, requestComment)).thenReturn(expectedComment);

        ResponseEntity<CommentDto> response = itemController.createComment(userId, itemId, requestComment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedComment, response.getBody());

    }
}