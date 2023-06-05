package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);
    List<Item> getAllByUserId(Long userId);
    Optional<Item> findById(Long itemId);
    void delete(Long itemId);
    Item update(Long itemId, ItemDto itemDto);

    List<Item> search(String text);
}
