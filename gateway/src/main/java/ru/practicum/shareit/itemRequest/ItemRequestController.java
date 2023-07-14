package ru.practicum.shareit.itemRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Create itemRequest {}, userId={}", itemRequestDto, userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get itemRequests with userId={}", userId);
        return itemRequestClient.getAllRequestByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Get itemRequest with userId={}, requestId={}",userId, requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Get itemRequests with userIs={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }
}
