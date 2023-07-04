package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.LogExecution;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @LogExecution
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        return ResponseEntity.ok(userService.getAll(from, size));
    }

    @GetMapping("/{userId}")
    @LogExecution(withArgs = true)
    public ResponseEntity<UserDto> getById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PostMapping
    @LogExecution
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(userDto));
    }

    @DeleteMapping("/{userId}")
    @LogExecution(withArgs = true)
    public ResponseEntity<Void> delete(@PathVariable("userId") Long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}")
    @LogExecution(withArgs = true)
    public ResponseEntity<UserDto> update(@PathVariable("userId") Long userId,
                                          @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.update(userId, userDto));
    }

}
