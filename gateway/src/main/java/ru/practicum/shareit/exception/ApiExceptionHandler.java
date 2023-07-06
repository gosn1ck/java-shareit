package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @SneakyThrows
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> handleException(FeignException e) {
        ObjectMapper mapper = new ObjectMapper();
        String message = "";
        if (!e.contentUTF8().isBlank()) {
            message = mapper.readTree(e.contentUTF8()).get("error").asText();
        }
        log.error(message);
        return new ResponseEntity<>(Map.of("error", message), HttpStatus.valueOf(e.status()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(Map.of("error", e.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(Map.of("error", "not valid argument"), BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        var errors = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(Map.of("error", errors.toString()), BAD_REQUEST);
    }

}
