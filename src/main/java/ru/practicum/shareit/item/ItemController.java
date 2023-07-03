package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBooked;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.ExistValid;
import ru.practicum.shareit.util.LogExecution;
import ru.practicum.shareit.util.ModelType;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @LogExecution
    @ExistValid(value = ModelType.USER, idPropertyName = "userId")
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    @LogExecution(withArgs = true)
    @ExistValid(value = ModelType.USER, idPropertyName = "userId")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("itemId") Long itemId,
                                          @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.update(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    @LogExecution(withArgs = true)
    @ExistValid(value = ModelType.USER, idPropertyName = "userId")
    public ResponseEntity<ItemBooked> getByItemId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable("itemId") Long itemId) {
        return ResponseEntity.ok(itemService.getByItemId(userId, itemId));
    }

    @GetMapping
    @LogExecution
    @ExistValid(value = ModelType.USER, idPropertyName = "userId")
    public ResponseEntity<List<ItemBooked>> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                                                @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        return ResponseEntity.ok(itemService.getAllItemsDyUserId(userId, from, size));
    }

    @DeleteMapping("/{itemId}")
    @LogExecution(withArgs = true)
    @ExistValid(value = ModelType.USER, idPropertyName = "userId")
    public ResponseEntity<Void> delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable("itemId") Long itemId) {
        itemService.delete(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @LogExecution
    @ExistValid(value = ModelType.USER, idPropertyName = "userId")
    public ResponseEntity<List<ItemDto>> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam("text") String text,
                                                @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        return ResponseEntity.ok(itemService.search(userId, text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    @LogExecution(withArgs = true)
    @ExistValid(value = ModelType.USER, idPropertyName = "userId")
    public ResponseEntity<CommentDto> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable("itemId") Long itemId,
                                                    @RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok().body(itemService.createComment(userId, itemId, commentDto));
    }
}
