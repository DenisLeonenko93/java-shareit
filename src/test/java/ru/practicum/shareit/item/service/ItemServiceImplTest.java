package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentsRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> argumentCaptorItem;
    @Captor
    private ArgumentCaptor<Comment> argumentCaptorComment;

    @Test
    void create_withUserAndRequestExist_thenReturnItemDto() {
        Long userId = 0L;
        UserDto userDto = UserDto.builder().build();
        User user = User.builder().id(0L).build();
        when(userService.findById(userId)).thenReturn(userDto);
        when(userMapper.userFromDto(userDto)).thenReturn(user);

        ItemDto itemDto = ItemDto.builder().requestId(0L).build();
        Item item = Item.builder().build();
        Item itemSaved = Item.builder().build();
        ItemDto savedItemDto = ItemDto.builder().id(0L).name("saved").build();
        when(itemMapper.itemFromDto(itemDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(itemSaved);
        when(itemMapper.itemToDto(itemSaved)).thenReturn(savedItemDto);

        ItemRequest request = ItemRequest.builder().id(0L).build();
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));

        ItemDto actualItemDto = itemService.create(userId, itemDto);

        assertEquals(0L, actualItemDto.getId());
        assertEquals("saved", actualItemDto.getName());

        verify(itemRepository).save(argumentCaptorItem.capture());

        Item savedItem = argumentCaptorItem.getValue();

        assertNotNull(savedItem.getOwner());
        assertEquals(0L, savedItem.getOwner().getId());
        assertNotNull(savedItem.getRequest());
        assertEquals(0L, savedItem.getRequest().getId());
    }

    @Test
    void create_withUserNotExist_thenEntityNotFoundExceptionThrow() {
        Long userId = 0L;
        ItemDto itemDto = ItemDto.builder().requestId(0L).build();
        doThrow(EntityNotFoundException.class).when(userService).findById(userId);

        assertThrows(EntityNotFoundException.class,
                () -> itemService.create(userId, itemDto));
        verify(itemRepository, never()).save(Mockito.any());
    }

    @Test
    void create_withUserExistRequestNotExist_thenEntityNotFoundExceptionThrow() {
        Long userId = 0L;
        ItemDto itemDto = ItemDto.builder().requestId(0L).build();
        when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.create(userId, itemDto));
        verify(itemRepository, never()).save(Mockito.any());
    }

    @Test
    void update_whenItemExistAndUserIsOwner_thenUpdateAndReturnItemDto() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto itemDtoUpdate = ItemDto.builder().name("Updated").build();
        Item itemOld = Item.builder()
                .id(0L)
                .name("Old")
                .owner(User.builder().id(0L).build())
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemOld));
        doAnswer(invocation -> {
            ItemDto itemDto = invocation.getArgument(0, ItemDto.class);
            Item item = invocation.getArgument(1, Item.class);
            item.setName(itemDto.getName());
            return null;
        }).when(itemMapper).updateItemFromDto(any(ItemDto.class), any(Item.class));

        itemService.update(userId, itemId, itemDtoUpdate);

        verify(itemMapper).updateItemFromDto(itemDtoUpdate, itemOld);
        verify(itemRepository).save(argumentCaptorItem.capture());

        Item savedItem = argumentCaptorItem.getValue();

        assertEquals(0L, savedItem.getId());
        assertEquals("Updated", savedItem.getName());
    }

    @Test
    void update_whenItemNotExist_thenEntityNotFoundExceptionThrow() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto itemDtoUpdate = ItemDto.builder().name("Updated").build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.update(userId, itemId, itemDtoUpdate));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenUserNotOwner_thenDataAccessExceptionThrow() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto itemDto = ItemDto.builder().build();
        Item itemOld = Item.builder()
                .owner(User.builder().id(1L).build())
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemOld));

        assertThrows(DataAccessException.class,
                () -> itemService.update(userId, itemId, itemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getByItemId() {
    }

    @Test
    void getAllItemsDyUserId() {
    }

    @Test
    void delete_withItemExistAndUserIsOwner_thenInvokeItemRepositoryDeleteMethod() {
        Long userId = 0L;
        Long itemId = 0L;
        Item item = Item.builder()
                .owner(User.builder().id(0L).build())
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.delete(userId, itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void delete_withItemNotExist_thenEntityNotFoundExceptionThrow() {
        Long userId = 0L;
        Long itemId = 0L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.delete(userId, itemId));

        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_withItemExistAndUserNotOwner_thenDataAccessExceptionThrow() {
        Long userId = 0L;
        Long itemId = 0L;
        Item item = Item.builder()
                .owner(User.builder().id(1L).build())
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(DataAccessException.class,
                () -> itemService.delete(userId, itemId));

        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void search_whenInvoke_thenReturnCollectionItemDto() {
        Long userId = 0L;
        String text = "any";
        Integer from = 1;
        Integer size = 1;
        Item item = Item.builder().build();
        List<Item> items = List.of(item);
        ItemDto itemDto = ItemDto.builder().build();
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        when(itemRepository.search(text, page)).thenReturn(items);
        when(itemMapper.itemToDto(item)).thenReturn(itemDto);

        List<ItemDto> actualItems = itemService.search(userId, text, from, size);

        assertFalse(actualItems.isEmpty());
        assertEquals(itemDto, actualItems.get(0));
    }

    @Test
    void search_withEmptyText_thenReturnCollectionItemDto() {
        Long userId = 0L;
        String text = "";
        Integer from = 1;
        Integer size = 1;

        List<ItemDto> actualItems = itemService.search(userId, text, from, size);

        assertTrue(actualItems.isEmpty());
    }

    @Test
    void search_whenNotFoundItems_thenEntityNotFoundExceptionThrows() {
        Long userId = 0L;
        String text = "any";
        Integer from = 1;
        Integer size = 1;
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        when(itemRepository.search(text, page)).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.search(userId, text, from, size));
    }

    @Test
    void createComment_withValidParams_thenReturnCommentDto() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto commentDto = CommentDto.builder().text("Comment").build();
        UserDto userDto = UserDto.builder().build();
        User user = User.builder().build();
        Item item = Item.builder().build();
        Comment comment = Comment.builder().id(0L).build();
        Comment savedComment = Comment.builder().build();
        CommentDto savedDto = CommentDto.builder().text("saved").build();
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userMapper.userFromDto(userDto)).thenReturn(user);
        when(bookingRepository
                .findFirstByItemIdAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Optional.of(Booking.builder().build()));
        when(commentMapper.commentFromDto(commentDto)).thenReturn(comment);
        when(commentsRepository.save(comment)).thenReturn(savedComment);
        when(commentMapper.commentToDto(savedComment)).thenReturn(savedDto);

        CommentDto actualDto = itemService.createComment(userId, itemId, commentDto);

        assertEquals("saved", actualDto.getText());

        verify(commentsRepository).save(argumentCaptorComment.capture());

        Comment commentSendToSave = argumentCaptorComment.getValue();

        assertEquals(0L, commentSendToSave.getId());
        assertNotNull(commentSendToSave.getItem());
        assertNotNull(commentSendToSave.getAuthor());
    }

    @Test
    void createComment_withUserNotExist_thenEntityNotFoundExceptionThrow() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto commentDto = CommentDto.builder().text("Comment").build();
        when(userService.findById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> itemService.createComment(userId, itemId, commentDto));

        verify(commentsRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenItemNotExist_thenEntityNotFoundExceptionThrow() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto commentDto = CommentDto.builder().text("Comment").build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.createComment(userId, itemId, commentDto));

        verify(commentsRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenUserNotBookingItem_thenValidationException() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto commentDto = CommentDto.builder().text("Comment").build();
        UserDto userDto = UserDto.builder().build();
        User user = User.builder().build();
        Item item = Item.builder().build();
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userMapper.userFromDto(userDto)).thenReturn(user);
        when(bookingRepository
                .findFirstByItemIdAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> itemService.createComment(userId, itemId, commentDto));

        verify(commentsRepository, never()).save(any(Comment.class));
    }

}