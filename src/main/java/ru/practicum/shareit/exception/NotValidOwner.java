package ru.practicum.shareit.exception;

public class NotValidOwner extends RuntimeException {
    public NotValidOwner(String message) {
        super(message);
    }
}

