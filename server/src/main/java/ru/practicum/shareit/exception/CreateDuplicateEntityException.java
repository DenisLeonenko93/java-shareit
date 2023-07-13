package ru.practicum.shareit.exception;

public class CreateDuplicateEntityException extends RuntimeException {
    public CreateDuplicateEntityException(String message) {
        super(message);
    }
}
