package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.LogExecution;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserMapper userMapper;


    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userMapper.userFromDto(userService.findById(userId));
        ItemRequest itemRequest = itemRequestMapper.fromDto(itemRequestDto);
        itemRequest.setRequestor(requestor);
        return itemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllRequestByUser(Long userId) {
        userService.findById(userId);

        List<ItemRequestDto> requests = itemRequestRepository.findByRequestorIdOrderByCreatedAsc(userId).stream()
                .map(itemRequestMapper::toDto)
                .collect(Collectors.toList());

        return requests;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.findById(userId);

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(ItemRequest.class, String.format("ID: %s", requestId)));

        return itemRequestMapper.toDto(request);
    }

    @Override
    @LogExecution(withArgs = true)
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userService.findById(userId);

        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры запроса указаны некорректно, не могуть быть отрицательными.");
        }

        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);

        return itemRequestRepository.findByRequestorIdNotOrderByCreatedAsc(userId, page)
                .stream()
                .map(itemRequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
