package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBooked;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemBooked getByItemId(Long userId, Long itemId);

    ItemDto findOne(Long itemId);

    List<ItemBooked> getAllItemsDyUserId(Long userId);

    void delete(Long userId, Long itemId);

    List<ItemDto> search(Long userId, String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);

    void isOwner(Long userId, ItemDto itemDto);
}
