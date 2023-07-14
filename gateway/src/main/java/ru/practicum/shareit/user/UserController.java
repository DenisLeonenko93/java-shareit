package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    @Autowired
    private UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Get users with from={}, size={}", from, size);
        return userClient.getUsers(from, size);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable("userId") Long userId) {
        log.info("Get user with userId={}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.create(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") Long userId) {
        log.info("Delete user {}", userId);
        return userClient.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") Long userId,
                                         @RequestBody UserDto userDto) {
        log.info("Update user {}, userId={}", userDto, userId);
        return userClient.update(userId, userDto);
    }
}
