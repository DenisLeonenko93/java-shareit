package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemMemoRepository implements ItemRepository {

    private final Map<Long, Item> storage = new HashMap<>();
    private Long lastId = 1L;

    @Override
    public Item create(Item item) {
        item.setId(lastId++);
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAllByUserId(Long userId) {
        return storage.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        if (storage.containsKey(itemId)) {
            return Optional.of(storage.get(itemId));
        }
        return Optional.empty();
    }

    @Override
    public void delete(Long itemId) {
        storage.remove(itemId);
    }


    @Override
    public Item update(Long itemId, ItemDto itemDto) {
        Item oldItem = storage.get(itemId);
        oldItem.setName(itemDto.getName() != null ? itemDto.getName() : oldItem.getName());
        oldItem.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : oldItem.getDescription());
        oldItem.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : oldItem.getAvailable());
        return storage.put(oldItem.getId(), oldItem);
    }

    @Override
    public List<Item> search(String text) {
        return storage.values()
                .stream()
                .filter(item -> {
                    StringBuilder str = new StringBuilder(item.getName());
                    str.append(item.getDescription());
                    return str.toString().toLowerCase().contains(text.toLowerCase());
                })
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
