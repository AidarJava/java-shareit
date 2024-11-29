package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(final NotFoundException e) {
        log.error("Ошибка 404 {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка 404 ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse notValidUser(final NotValidOwner e) {
        log.error("Ошибка 403 {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка 403 ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflictEmail(final ConflictException e) {
        log.error("Ошибка 409 {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка 409 ", e.getMessage());
    }
}
