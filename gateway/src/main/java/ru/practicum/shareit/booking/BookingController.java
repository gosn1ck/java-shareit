package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.client.BookingClient;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient client;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookingResponse> add(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody BookingDto dto) {
        log.info("New booking registration {}; user id {}", dto, userId);
        return client.add(userId, dto);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<BookingResponse> approve(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable("id") @Positive Long id,
            @RequestParam Boolean approved) {
        log.info("approve item by id: {}; user id: {}", id, userId);
        return client.approve(userId, id, approved);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<BookingResponse> getById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable("id") @Positive Long id) {
        log.info("Get booking by id: {}, user id: {} ", id, userId);
        return client.getById(userId, id);
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<List<BookingResponse>> getAllOwner(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "minimum value for from param is 0") Integer from,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "minimum value for size param is 1") Integer size) {
        log.info("Get owner bookings by user id: {}, state: {} ",  userId, state);
        return client.getAllOwner(userId, state, from, size);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBooker(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "minimum value for from param is 0") Integer from,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "minimum value for size param is 1") Integer size) {
        log.info("Get all bookings by user id: {}, state: {} ",  userId, state);
        return client.getAllBooker(userId, state, from, size);
    }

}
