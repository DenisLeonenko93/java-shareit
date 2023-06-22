package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.SimpleBookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBooked;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentsRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentsRepository commentsRepository;
    private final UserService userService;
    private final SimpleBookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userMapper.userFromDto(userService.findById(userId));
        Item item = itemMapper.itemFromDto(itemDto);
        item.setOwner(user);
        return itemMapper.itemToDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userService.findById(userId);
        Item item = isExist(itemId);
        checkItemOwner(userId, item);
        itemMapper.updateItemFromDto(itemDto, item);
        return itemMapper.itemToDto(itemRepository.save(item));
    }

    @Override
    public ItemBooked getByItemId(Long userId, Long itemId) {
        userService.findById(userId);
        Item item = isExist(itemId);
        ItemBooked itemBooked = itemMapper.itemToItemBooked(item);
        if (item.getOwner().getId().equals(userId)) {
            itemBooked.setLastBooking(bookingMapper.bookingForItemResponseDto(
                    bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(itemBooked.getId(), BookingStatus.APPROVED, LocalDateTime.now())
                            .orElse(null)));
            itemBooked.setNextBooking(bookingMapper.bookingForItemResponseDto(
                    bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemBooked.getId(), BookingStatus.APPROVED, LocalDateTime.now())
                            .orElse(null)));
        }
        List<CommentDto> comments = commentsRepository.findByItem(item).stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList());

        itemBooked.setComments(comments);
        return itemBooked;
    }

    @Override
    public List<ItemBooked> getAllItemsDyUserId(Long userId) {
        userService.findById(userId);
        List<ItemBooked> items = itemRepository.findAllByUserId(userId)
                .stream()
                .map(item -> {
                    ItemBooked itemBooked = itemMapper.itemToItemBooked(item);
                    itemBooked.setLastBooking(bookingMapper.bookingForItemResponseDto(
                            bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(itemBooked.getId(), BookingStatus.APPROVED, LocalDateTime.now())
                                    .orElse(null)));
                    itemBooked.setNextBooking(bookingMapper.bookingForItemResponseDto(
                            bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemBooked.getId(), BookingStatus.APPROVED, LocalDateTime.now())
                                    .orElse(null)));

                    List<CommentDto> comments = commentsRepository.findByItem(item).stream()
                            .map(commentMapper::commentToDto)
                            .collect(Collectors.toList());
                    itemBooked.setComments(comments);

                    return itemBooked;
                })
                .collect(Collectors.toList());

        return items;
    }

    @Override
    public void delete(Long userId, Long itemId) {
        userService.findById(userId);
        Item item = isExist(itemId);
        checkItemOwner(userId, item);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        userService.findById(userId);
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.search(text);
        if (items.isEmpty()) {
            throw new EntityNotFoundException(Item.class, String.format("text: %s", text));
        }
        return items.stream()
                .map(itemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userMapper.userFromDto(userService.findById(userId));
        Item item = isExist(itemId);
        bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())
                .orElseThrow(() -> (new ValidationException("Пользователь не брал предмет в аренду")));
        Comment comment = commentMapper.commentFromDto(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.commentToDto(commentsRepository.save(comment));
    }


    private void checkItemOwner(Long userId, Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new DataAccessException(
                    String.format("User %d is not the owner item %d",
                            userId, item.getId()));
        }
    }

    private Item isExist(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(Item.class, String.format("ID: %s", itemId)));
    }


}
