package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBooked;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemBooked getByItemId(Long userId, Long itemId);

    List<ItemBooked> getAllItemsDyUserId(Long userId, Integer from, Integer size);

    void delete(Long userId, Long itemId);

    List<ItemDto> search(Long userId, String text, Integer from, Integer size);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);

}
