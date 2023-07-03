package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void create_whenInvoke_thenReturnStatusCreateWithItemRequestDtoInBody() {
        Long userId = 0L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();
        ItemRequestDto expectedDto = ItemRequestDto.builder().id(0L).build();
        when(itemRequestService.create(userId, itemRequestDto)).thenReturn(expectedDto);

        ResponseEntity<ItemRequestDto> response = itemRequestController.create(userId, itemRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void getAllRequestsByUser_whenInvoke_thenReturnStatusOKWithListItemRequestDtoInBody() {
        Long userId = 0L;
        List<ItemRequestDto> expectedItemRequests = List.of(ItemRequestDto.builder().build());
        when(itemRequestService.getAllRequestByUser(anyLong())).thenReturn(expectedItemRequests);

        ResponseEntity<List<ItemRequestDto>> response = itemRequestController.getAllRequestsByUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getRequestById_whenInvoke_thenReturnStatusOKWithItemRequestDtoInBody() {
        Long userId = 0L;
        Long requestId = 0L;
        ItemRequestDto expectedItemRequests = ItemRequestDto.builder().build();
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(expectedItemRequests);

        ResponseEntity<ItemRequestDto> response = itemRequestController.getRequestById(userId, requestId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemRequests, response.getBody());
    }

    @Test
    void getAllRequests_whenInvoke_thenReturnStatusOKWithListItemRequestDtoInBody() {
        Long userId = 0L;
        Integer from = 1;
        Integer size = 5;
        List<ItemRequestDto> expectedItemRequests = List.of(ItemRequestDto.builder().build());
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(expectedItemRequests);

        ResponseEntity<List<ItemRequestDto>> response = itemRequestController.getAllRequests(userId, from, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
}