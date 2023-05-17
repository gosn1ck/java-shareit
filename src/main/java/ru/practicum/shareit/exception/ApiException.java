package ru.practicum.shareit.exception;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ApiException {
    private final String message;
    private final ZonedDateTime dateTime;
}
