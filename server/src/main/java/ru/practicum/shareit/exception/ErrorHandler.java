package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse notValidRequest(final NotValidRequestException e) {
        log.error("Ошибка 400 {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка 400 ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse notValidRequestEx(final ServerErrorException e) {
        log.error("Ошибка 500 {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка 500 ", e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // HTTP 409 Conflict
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("Ошибка сохранения данных: {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка сохранения данных", e.getMessage());
    }
}
