package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.LogExecution;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @LogExecution
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{userId}")
    @LogExecution(withArgs = true)
    public ResponseEntity<UserDto> getById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PostMapping
    @LogExecution
    public ResponseEntity<UserDto> create(@RequestBody @Valid User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(user));
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
                                          @RequestBody User user) {
        return ResponseEntity.ok(userService.update(userId, user));
    }

}
