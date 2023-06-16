package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
    public static ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item fromDto(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item updateItemFromDto(Item oldItem, ItemDto newItemDto) {
        oldItem.setName(newItemDto.getName() != null ? newItemDto.getName() : oldItem.getName());
        oldItem.setDescription(
                newItemDto.getDescription() != null ? newItemDto.getDescription() : oldItem.getDescription());
        oldItem.setAvailable(newItemDto.getAvailable() != null ? newItemDto.getAvailable() : oldItem.getAvailable());

        return oldItem;
    }
}
