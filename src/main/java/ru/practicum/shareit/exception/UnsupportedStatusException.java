package ru.practicum.shareit.exception;

public class UnsupportedStatusException extends RuntimeException{
    public UnsupportedStatusException(String message) {
        super(String.format("Unknown state: %s", message));
    }
}