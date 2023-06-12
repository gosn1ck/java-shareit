package ru.practicum.shareit.exception;

public class BadRequestException extends RuntimeException{

    public BadRequestException(String message, Long id) {
        super(String.format(message, id));
    }

    public BadRequestException(String message, String part) {
        super(String.format(message, part));
    }

}
