package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookingResponse> add(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingDto dto) {
        log.info("New booking registration {}; user id {}", dto, userId);
        if (!dto.validate()) {
            throw new BadRequestException("start booking must be before end booking: %s", dto.toString());
        }
        var savedBooking = bookingService.add(dto, userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedBooking.getId()).toUri();
        return ResponseEntity.created(location)
                .body(bookingMapper.entityToBookingResponse(savedBooking));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<BookingResponse> approve(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("id") @Positive Long id,
            @RequestParam Boolean approved) {
        log.info("approve item by id: {}; user id: {}", id, userId);
        var response = bookingService.approve(id, userId, approved);
        return ResponseEntity.ok(bookingMapper.entityToBookingResponse(response));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<BookingResponse> getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("id") @Positive Long id) {
        log.info("Get booking by id: {}, user id: {} ", id, userId);
        var response = bookingService.getById(id, userId);
        return response.map(bookingMapper::entityToBookingResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<List<BookingResponse>> getAllOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        log.info("Get owner bookings by user id: {}, state: {} ",  userId, state);
        var response = bookingService.getAllOwner(userId, state);
        return ResponseEntity.ok(bookingMapper.entitiesToBookingResponses(response));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        var response = bookingService.getAllBooker(userId, state);
        return ResponseEntity.ok(bookingMapper.entitiesToBookingResponses(response));
    }

}
