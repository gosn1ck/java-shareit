package ru.practicum.shareit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import static ru.practicum.shareit.util.Constants.USER_HEADER;

import java.util.List;

@FeignClient(
        value = "booking",
        url = "${feign.url.booking}"
)
public interface BookingClient {

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<BookingResponse> add(@RequestHeader(USER_HEADER) Long userId, @RequestBody BookingDto dto);

    @PatchMapping(path = "/{id}")
    ResponseEntity<BookingResponse> approve(@RequestHeader(USER_HEADER) Long userId,
                                            @PathVariable("id") Long id, @RequestParam Boolean approved);

    @GetMapping(path = "/{id}")
    ResponseEntity<BookingResponse> getById(@RequestHeader(USER_HEADER) Long userId, @PathVariable("id") Long id);

    @GetMapping(path = "/owner")
    ResponseEntity<List<BookingResponse>> getAllOwner(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size);

    @GetMapping
    ResponseEntity<List<BookingResponse>> getAllBooker(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size);

}
