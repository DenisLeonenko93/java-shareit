package ru.practicum.shareit.exception;

public class CreateDuplicateEntityException extends RuntimeException {
    public CreateDuplicateEntityException(Class<?> entityClass, Long message) {
        super(String.format("Entity %s is exist. ID: %d", entityClass.getSimpleName(), message));
    }
}
