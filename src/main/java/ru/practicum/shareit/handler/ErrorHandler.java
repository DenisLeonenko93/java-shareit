package ru.practicum.shareit.handler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.CreateDuplicateEntityException;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.EntityNotFoundException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorInfo handleNotFoundEntity(EntityNotFoundException e) {
        return new ErrorInfo(EntityNotFoundException.class,
                e.getMessage());
    }

    @ExceptionHandler(CreateDuplicateEntityException.class)
    @ResponseStatus(CONFLICT)
    public ErrorInfo handleDuplicateEntity(CreateDuplicateEntityException e) {
        return new ErrorInfo(CreateDuplicateEntityException.class,
                e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorInfo handleDataAccess(DataAccessException e) {
        return new ErrorInfo(DataAccessException.class,
                e.getMessage());
    }

    private static class ErrorInfo {
        String error;
        String description;

        public ErrorInfo(Class<?> entityClass, String description) {
            this.error = entityClass.getSimpleName();
            this.description = description;
        }

        public String getError() {
            return error;
        }

        public String getDescription() {
            return description;
        }
    }
}
