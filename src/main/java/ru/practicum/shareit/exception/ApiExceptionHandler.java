package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(value = InternalServerException.class)
    public ResponseEntity<ApiException> handleException(InternalServerException e) {
        log.error(e.getMessage(), e);
        ApiException exception = new ApiException(INTERNAL_SERVER_ERROR, e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(exception, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ApiException> handleException(NotFoundException e) {
        log.error(e.getMessage(), e);
        ApiException exception = new ApiException(NOT_FOUND, e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(exception, NOT_FOUND);
    }

    @ExceptionHandler(value = ClientErrorException.class)
    public ResponseEntity<ApiException> handleException(ClientErrorException e) {
        log.error(e.getMessage(), e);
        ApiException exception = new ApiException(CONFLICT, e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(exception, CONFLICT);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiException> handleException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String errorMessage = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList()).toString();
        ApiException exception = new ApiException(BAD_REQUEST, errorMessage, ZonedDateTime.now());

        return new ResponseEntity<>(exception, BAD_REQUEST);
    }

    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public ResponseEntity<ApiException> handleException(MissingRequestHeaderException e) {
        log.error(e.getMessage(), e);
        ApiException exception = new ApiException(BAD_REQUEST, e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(exception, BAD_REQUEST);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ApiException> handleException(BadRequestException e) {
        log.error(e.getMessage(), e);
        ApiException exception = new ApiException(BAD_REQUEST, e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(exception, BAD_REQUEST);
    }

}
