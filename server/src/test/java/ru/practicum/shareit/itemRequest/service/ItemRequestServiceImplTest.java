package ru.practicum.shareit.itemRequest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.mapper.ItemRequestMapper;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Captor
    private ArgumentCaptor<ItemRequest> argumentCaptorItemRequest;

    private Long userId;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("desc")
                .created(Instant.MIN).build();
        itemRequest = ItemRequest.builder().build();
    }


    @Test
    void create_whenCreateItemRequestAndUserFound_thenReturnItemRequestDto() {
        UserDto userDto = UserDto.builder().build();
        User user = User.builder().build();
        ItemRequest itemRequest = ItemRequest.builder()
                .created(Instant.MIN).build();
        ItemRequest expectedItemRequest = ItemRequest.builder()
                .id(1L)
                .requestor(user)
                .created(Instant.MIN).build();

        when(userService.findById(userId)).thenReturn(userDto);
        when(userMapper.userFromDto(userDto)).thenReturn(user);
        when(itemRequestMapper.fromDto(itemRequestDto)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(expectedItemRequest);
        when(itemRequestMapper.toDto(expectedItemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto actualItemRequestDto = itemRequestService.create(userId, itemRequestDto);

        assertEquals(1L, actualItemRequestDto.getId());
        assertEquals("desc", actualItemRequestDto.getDescription());
        assertEquals(Instant.MIN, actualItemRequestDto.getCreated());
        assertNull(actualItemRequestDto.getItems());

        verify(itemRequestRepository).save(argumentCaptorItemRequest.capture());
        ItemRequest savedRequest = argumentCaptorItemRequest.getValue();

        assertEquals(user, savedRequest.getRequestor());
    }

    @Test
    void create_whenCreateItemRequestAndUserNotFound_thenEntityNotFoundExceptionThrow() {
        when(userService.findById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create(userId, itemRequestDto));
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getAllRequestByUser_whenInvoke_thenReturnCollectionItemRequestDto() {
        List<ItemRequest> requests = List.of(itemRequest);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedAsc(userId)).thenReturn(requests);
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> actualRequests = itemRequestService.getAllRequestByUser(userId);

        assertFalse(actualRequests.isEmpty());
        assertEquals(itemRequestDto, actualRequests.get(0));
    }

    @Test
    void getRequestById_whenItemRequestFound_thenReturnItemRequestDto() {
        Long requestId = 0L;
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto actualRequestDto = itemRequestService.getRequestById(userId, requestId);

        assertEquals(itemRequestDto, actualRequestDto);
    }

    @Test
    void getRequestById_whenItemRequestNotFound_thenEntityNotFoundExceptionThrow() {
        Long requestId = 0L;
        when(itemRequestRepository.findById(requestId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
    }

    @Test
    void getAllRequests_whenInvoke_thenReturnCollectionItemRequestDto() {
        int from = 1;
        int size = 5;
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> requests = List.of(itemRequest);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedAsc(userId, page)).thenReturn(requests);
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> actualRequests = itemRequestService.getAllRequests(userId, from, size);

        assertFalse(actualRequests.isEmpty());
        assertEquals(itemRequestDto, actualRequests.get(0));
    }
}