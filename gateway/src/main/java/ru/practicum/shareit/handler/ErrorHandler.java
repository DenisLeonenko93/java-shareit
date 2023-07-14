package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice

public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorInfo handleUnsupportedStatusException(ValidationException e) {
        log.warn(e.getMessage());
        return new ErrorInfo(ValidationException.class,
                e.getMessage());
    }

    @ExceptionHandler(UnsupportedStatusException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorInfo handleUnsupportedStatusException(UnsupportedStatusException e) {
        log.warn(e.getMessage());
        return new ErrorInfo(UnsupportedStatusException.class,
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

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorInfo handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn(e.getMessage());
        return new ErrorInfo(MissingServletRequestParameterException.class,
                e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorInfo handleHibernateViolationException(ConstraintViolationException e) {
        log.warn(e.getLocalizedMessage());
        return new ErrorInfo(ConstraintViolationException.class,
                e.getMessage());
    }


    private static class ErrorInfo {
        String error;
        String errorClass;

        public ErrorInfo(Class<?> entityClass, String message) {
            this.errorClass = entityClass.getSimpleName();
            this.error = message;
        }

        public String getErrorClass() {
            return errorClass;
        }

        public String getError() {
            return error;
        }
    }
}
