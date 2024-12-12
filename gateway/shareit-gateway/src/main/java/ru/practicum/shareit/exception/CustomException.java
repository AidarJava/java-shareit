package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;
}
