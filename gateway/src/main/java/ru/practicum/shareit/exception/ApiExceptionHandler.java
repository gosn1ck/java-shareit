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

import java.util.Map;

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

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<Map<String, String>> handleException(Throwable e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(Map.of("error", e.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(Map.of("error", "not valid argument"), BAD_REQUEST);
    }

}
