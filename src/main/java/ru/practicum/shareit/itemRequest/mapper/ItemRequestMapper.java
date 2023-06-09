package ru.practicum.shareit.itemRequest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequest fromDto(ItemRequestDto itemRequestDto);

    ItemRequestDto toDto(ItemRequest save);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto itemToDto(Item item);

}
