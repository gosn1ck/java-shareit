package ru.practicum.shareit.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Long id) {
        super(String.format(message, id));
    }

}
