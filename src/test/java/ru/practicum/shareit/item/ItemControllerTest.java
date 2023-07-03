package ru.practicum.shareit.item;

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

    @Test
    void create_whenInvoked_thenResponseStatusCreateWithItemDtoInBody() {
        Long userId = 0L;
        ItemDto requestDto = ItemDto.builder().build();
        ItemDto expectedItem = ItemDto.builder().id(0L).build();
        when(itemService.create(userId, requestDto)).thenReturn(expectedItem);

        ResponseEntity<ItemDto> response = itemController.create(userId, requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedItem, response.getBody());
    }

    @Test
    void update_whenInvoked_thenResponseStatusOKWithItemDtoInBody() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto requestDto = ItemDto.builder().build();
        ItemDto expectedItem = ItemDto.builder().id(0L).build();
        when(itemService.update(userId, itemId, requestDto)).thenReturn(expectedItem);

        ResponseEntity<ItemDto> response = itemController.update(userId, itemId, requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItem, response.getBody());
    }

    @Test
    void getByItemId_whenInvoked_thenResponseStatusOkWithItemBookedInBody() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemBooked expectedItem = ItemBooked.builder().build();
        when(itemService.getByItemId(userId, itemId)).thenReturn(expectedItem);

        ResponseEntity<ItemBooked> response = itemController.getByItemId(userId, itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItem, response.getBody());
    }

    @Test
    void getAllItemsByUserId_whenInvoked_thenResponseStatusOkWithListItemBookedInBody() {
        Long userId = 0L;
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
        Long userId = 0L;
        Long itemId = 0L;

        ResponseEntity<Void> response = itemController.delete(userId, itemId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(itemService, times(1)).delete(userId, itemId);
    }

    @Test
    void search_whenInvoked_thenResponseStatusOkWithListItemBookedInBody() {
        Long userId = 0L;
        Integer from = 1;
        Integer size = 1;
        String text = " ";
        ItemDto expectedItem = ItemDto.builder().build();
        List<ItemDto> expectedCollection = List.of(expectedItem);
        when(itemService.search(userId, text, from, size)).thenReturn(expectedCollection);

        ResponseEntity<List<ItemDto>> response = itemController.search(userId, text, from, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void createComment_whenInvoked_thenResponseStatusCreatedWithCommentDtoInBody() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto requestComment = CommentDto.builder().build();
        CommentDto expectedComment = CommentDto.builder().id(0L).build();
        when(itemService.createComment(userId, itemId, requestComment)).thenReturn(expectedComment);

        ResponseEntity<CommentDto> response = itemController.createComment(userId, itemId, requestComment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedComment, response.getBody());

    }
}