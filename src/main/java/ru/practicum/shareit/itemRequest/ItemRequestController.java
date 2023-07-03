package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;
import ru.practicum.shareit.util.ExistValid;
import ru.practicum.shareit.util.LogExecution;
import ru.practicum.shareit.util.ModelType;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @LogExecution
    public ResponseEntity<ItemRequestDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return ResponseEntity.status(CREATED)
                .body(itemRequestService.create(userId, itemRequestDto));
    }

    @GetMapping
    @LogExecution
    @ExistValid(value = ModelType.USER, idPropertyName = "userId")
    public ResponseEntity<List<ItemRequestDto>> getAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemRequestService.getAllRequestByUser(userId));
    }

    @GetMapping("/{requestId}")
    @LogExecution
    @ExistValid(value = ModelType.USER, idPropertyName = "userId")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable Long requestId) {
        return ResponseEntity.ok(itemRequestService.getRequestById(userId, requestId));
    }

    @GetMapping("/all")
    @LogExecution(withArgs = true)
    @ExistValid(value = ModelType.USER, idPropertyName = "userId")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(required = false, defaultValue = "0") Integer from,
                                                               @RequestParam(required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(itemRequestService.getAllRequests(userId, from, size));
    }
}
