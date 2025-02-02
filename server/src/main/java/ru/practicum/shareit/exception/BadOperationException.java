package ru.practicum.shareit.exception;

public class BadOperationException extends RuntimeException {
    public BadOperationException(String message) {
        super(message);
    }
}
