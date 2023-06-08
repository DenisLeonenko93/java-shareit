package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.CreateDuplicateEntityException;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.EntityNotFoundException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorInfo handleNotFoundEntity(EntityNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorInfo(EntityNotFoundException.class,
                e.getMessage());
    }

    @ExceptionHandler(CreateDuplicateEntityException.class)
    @ResponseStatus(CONFLICT)
    public ErrorInfo handleDuplicateEntity(CreateDuplicateEntityException e) {
        log.warn(e.getMessage());
        return new ErrorInfo(CreateDuplicateEntityException.class,
                e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorInfo handleDataAccess(DataAccessException e) {
        log.warn(e.getMessage());
        return new ErrorInfo(DataAccessException.class,
                e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorInfo handlerMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return new ErrorInfo(MethodArgumentNotValidException.class,
                e.getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorInfo handleMissingRequestHeader(MissingRequestHeaderException e) {
        log.warn(e.getMessage());
        return new ErrorInfo(MissingRequestHeaderException.class,
                e.getMessage());
    }

    private static class ErrorInfo {
        String error;
        String message;

        public ErrorInfo(Class<?> entityClass, String message) {
            this.error = entityClass.getSimpleName();
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}
