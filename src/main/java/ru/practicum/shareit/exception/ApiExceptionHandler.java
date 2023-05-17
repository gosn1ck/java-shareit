package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(value = InternalServerException.class)
    public ResponseEntity<ApiException> handleException(InternalServerException e) {
        log.error(e.getMessage(), e);
        ApiException exception = new ApiException(e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ApiException> handleException(NotFoundException e) {
        log.error(e.getMessage(), e);
        ApiException exception = new ApiException(e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiException> handleException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        ApiException exception = new ApiException(e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

}
