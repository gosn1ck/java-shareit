package ru.practicum.shareit.exception;

public class InternalServerException extends RuntimeException {

    public InternalServerException(String message, String part) {
        super(String.format(message, part));
    }

}
