package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = UserMapper.fromUserDto(userService.findById(userId));
        Item item = ItemMapper.fromDto(itemDto);
        item.setOwner(user);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userService.findById(userId);
        Item oldItem = isExist(itemId);
        checkItemOwner(userId, oldItem);
        return ItemMapper.toDto(itemRepository.save(ItemMapper.updateItemFromDto(oldItem, itemDto)));
    }

    @Override
    public ItemDto getByItemId(Long userId, Long itemId) {
        userService.findById(userId);
        Item item = isExist(itemId);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto findOne(Long itemId) {
        return ItemMapper.toDto(isExist(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsDyUserId(Long userId) {
        userService.findById(userId);
        return itemRepository.findAllByUserId(userId)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
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
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
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
