package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message, Long id) {
        super(String.format(message, id));
    }

    public NotFoundException(String message, Long itemId, Long userId) {
        super(String.format(message, itemId, userId));
    }

}
