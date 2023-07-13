package ru.practicum.shareit.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> entityClass, String message) {
        super(String.format("Entity %s not found by %s", entityClass.getSimpleName(), message));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
