package ru.practicum.shareit.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
public class ApiException {
    private final HttpStatus status;
    private final String message;
    private final ZonedDateTime dateTime;
}
