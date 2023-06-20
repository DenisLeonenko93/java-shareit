package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemBooked;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.LogExecution;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @LogExecution
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    @LogExecution(withArgs = true)
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("itemId") Long itemId,
                                          @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.update(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    @LogExecution(withArgs = true)
    public ResponseEntity<ItemBooked> getByItemId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable("itemId") Long itemId) {
        return ResponseEntity.ok(itemService.getByItemId(userId, itemId));
    }

    @GetMapping
    @LogExecution
    public ResponseEntity<List<ItemBooked>> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getAllItemsDyUserId(userId));
    }

    @DeleteMapping("/{itemId}")
    @LogExecution(withArgs = true)
    public ResponseEntity<Void> delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable("itemId") Long itemId) {
        itemService.delete(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @LogExecution
    public ResponseEntity<List<ItemDto>> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam("text") String text) {
        return ResponseEntity.ok(itemService.search(userId, text));
    }
}
