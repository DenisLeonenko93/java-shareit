package ru.practicum.shareit.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> entityClass, String message) {
        super(String.format("Entity %s not found by ID: %s", entityClass.getSimpleName(), message));
    }
}
